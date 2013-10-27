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

object PDF {
  // static membervariables for the different styles
  val NORMAL = 0;
  val BOLD = 1;
  val ITALIC = 2;
  val UNDERLINE = 4;
  val STRIKETHRU = 8;
  val BOLDITALIC = BOLD | ITALIC;
}

/**
 * Main class for PDF DSL
 */
abstract class PDF extends Document {

  // The state of the PDF generator
  val state = new AnyRef {
    val elements: MapQueue[Element] = Queue[Element]()
    val commands: MapQueue[Command] = Queue[Command]()
  }

  // Define the file to save the PDF to
  def file(path: String) {
    PdfWriter.getInstance(this, new FileOutputStream(path));
    open()
  }

  def paragraph(contents: String) {
    val para = new Paragraph(contents)
    state.elements.map(para.add)
    add(para)
  }
  def paragraph(contents: Element): Unit = paragraph("")

  def phrase(body: String) = {
    val phrase = new Phrase(body)
    state.commands.map(_(phrase))
    state.elements.map(_.isInstanceOf[Chunk], phrase.add)
    state.elements.enqueue(phrase)
    phrase
  }
  def phrase(contents: Element): Phrase = phrase("")

  def leading(l: Float) {
    state.commands.enqueue(new Command() {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def setLeading(l: Float) }].setLeading(l)
      }
    })
  }

  def chunk(body: String) = {
    val chunk = new Chunk(body)
    state.commands.map(_(chunk))
    state.elements.enqueue(chunk)
    chunk
  }

  def font(size: Int = 10, family: Int = 0, style: Int = 0, color: Color = null) = {
    val font = new Font(family, size, style)
    if (color != null) {
      font.setColor(color)
    }
    state.commands.enqueue(new Command() {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def setFont(f: Font) }].setFont(font)
      }
    })
  }

  def background(color: Color) = {
    state.commands.enqueue(new Command() {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def setBackground(c: Color) }].setBackground(color)
      }
    })
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
   * Queue that dequeues on map and enqueues back any failed to apply.
   * Also, its map returns Unit - this is necessary to be able to enqueue back items that failed to apply.
   * TODO: fix broken enqueue varargs
   */
  implicit class MapQueue[A](val o: Queue[A]) {
    def map[B](f: A => B) {
      o.dequeueAll(_ => true).map(safeApply(f))
    }
    def map[B](selector: A => Boolean, f: A => B) {
      o.dequeueAll(selector).map(f)
    }
    def safeApply[B](f: A => B)(item: A) {
      try {
        f(item)
      } catch {
        case e: Exception => o.enqueue(item)
      }
    }
    def enqueue(elems: A): Unit = o.enqueue(elems)
  }

  abstract class Command {
    def apply(e: Element): Unit
  }

}
class PdfException(msg: String) extends RuntimeException(msg)
