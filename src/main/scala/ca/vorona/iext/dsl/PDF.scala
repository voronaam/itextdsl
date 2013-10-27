package ca.vorona.iext.dsl

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.Paragraph
import java.io.FileOutputStream
import com.lowagie.text.Phrase
import com.lowagie.text.Chunk
import com.lowagie.text.Font
import java.awt.Color
import scala.AnyVal
import scala.collection.mutable.Queue
import com.lowagie.text.Element
import scala.collection.generic.CanBuildFrom

/**
 * Main class for PDF DSL
 */
abstract class PDF extends Document {

  // The state of the PDF generator
  val state = new AnyRef {
    var font: OneTimeOption[Font] = None
    val elements: MapQueue[Element] = Queue[Element]()
    var background: OneTimeOption[Color]= None
  }

  def file(path: String) {
    PdfWriter.getInstance(this, new FileOutputStream(path));
    open()
  }

  def paragraph(contents: String) {
    val para = new Paragraph(contents)
    state.elements.map(para.add)
    add(para)
  }

  def paragraph(contents: Element) {
    paragraph("")
  }

  def phrase(body: String) = {
    val phrase = new Phrase(body)
    state.elements.map(_.isInstanceOf[Chunk], phrase.add)
    state.elements.enqueue(phrase)
    phrase
  }

  def chunk(body: String) = {
    val chunk = new Chunk(body)
    state.font.map(chunk.setFont)
    state.background.map(chunk.setBackground)
    state.elements.enqueue(chunk)
    chunk
  }

  def font(size: Int = 0, family: Int = 0, style: Int = 0, color: Color = null) = {
    val font = new Font(family, size, style)
    if (color != null) {
      font.setColor(color)
    }
    state.font = Some(font)
    font
  }

  def background(color: Color) = {
    state.background = Some(color)
    color
  }

  /**
   * Converts string of rgb"0xRRGGBB" type to AWT Color
   */
  implicit class ColorHelper(val sc: StringContext) {
    def rgb(args: Any*): Color = {
      val colorDefHex = sc.s()
      val colorDef = if (colorDefHex.startsWith("0x")) colorDefHex.substring(2) else colorDefHex
      if (colorDef.length() != 6) {
        throw new PdfException(s"Incorrect color defined: $colorDefHex")
      }
      val r = Integer.parseInt(colorDef.substring(0, 2), 16)
      val g = Integer.parseInt(colorDef.substring(2, 4), 16)
      val b = Integer.parseInt(colorDef.substring(4, 6), 16)
      new Color(r, g, b)
    }
  }
  
  /**
   * An option that can be used only once.
   * TODO: Should also change other methods (such as get()) do the same
   */
  implicit class OneTimeOption[A](val o: Option[A]) {
    var used = false
    final def map[B](f: A => B): Option[B] =
      if (used || o.isEmpty) None else {
        val r = Some(f(o.get))
        used = true
        r
      }
  }
  
  /**
   * Queue that dequeues on map
   * TODO: fix broken enqueue varargs
   */
  implicit class MapQueue[A](val o: Queue[A]) {
    def map[B, That](f: A => B) = {
      o.dequeueAll(_ => true).map(f)
    }
    def map[B, That](selector: A => Boolean, f: A => B) = {
      o.dequeueAll(selector).map(f)
    }
    def enqueue(elems: A): Unit = o.enqueue(elems)
  }


}
class PdfException(msg: String) extends RuntimeException(msg)
