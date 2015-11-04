package ca.vorona.iext.dsl

import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.Paragraph
import java.io.FileOutputStream
import com.itextpdf.text.Phrase
import com.itextpdf.text.Chunk
import com.itextpdf.text.Font
import com.itextpdf.text.BaseColor
import scala.AnyVal
import scala.collection.mutable.Queue
import scala.collection.mutable.Stack
import com.itextpdf.text.Element
import scala.collection.generic.CanBuildFrom
import com.itextpdf.text.TextElementArray

object PDF {
  // static membervariables for the different styles
  val NORMAL = 0;
  val BOLD = 1;
  val ITALIC = 2;
  val UNDERLINE = 4;
  val STRIKETHRU = 8;
  val BOLDITALIC = BOLD | ITALIC;
  
  def noop = Unit
}

/**
 * Main class for PDF DSL
 */
abstract class PDF extends Document {

  // Define the file to save the PDF to
  def file(path: String) {
    PdfWriter.getInstance(this, new FileOutputStream(path));
    open()
  }
  
  def paragraph(body: =>Any = PDF.noop, text: String = ""): Unit = {
    val state = call(body)
    val para = new Paragraph(text)
    state.commands.map(_(para))
    state.elements.map(para.add)
    add(para)
  }
  def paragraph(text: String): Unit = paragraph(PDF.noop, text)

  def phrase(body: =>Any = PDF.noop, text: String = "") = {
    val state = call(body)
    val phrase = new Phrase(text)
    state.commands.map(_(phrase))
    state.elements.map(phrase.add)
    enqueueElement(phrase)
  }
  def phrase(text: String): Unit = phrase(PDF.noop, text)

  def chunk(body: =>Any, text: String = "") = {
    val state = call(body)
    val chunk = new Chunk(text)
    state.commands.map(_(chunk))
    enqueueElement(chunk)
  }
  def chunk(text: String): Unit = chunk(PDF.noop, text)


  def leading(l: Float) {
    enqueueCommand(new Command("leading") {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def setLeading(l: Float) }].setLeading(l)
      }
    })
  }

  def text(text: String) {
    enqueueCommand(new Command("text") {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def append(s: String) }].append(text)
      }
    })
  }

  def font(size: Int = 10, family: Option[Font.FontFamily] = None, style: Int = 0, color: BaseColor = null) = {
    val font = new Font(family.getOrElse(Font.FontFamily.UNDEFINED), size, style)
    if (color != null) {
      font.setColor(color)
    }
    enqueueCommand(new Command("font") {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def setFont(f: Font) }].setFont(font)
      }
    })
  }

  def background(color: BaseColor) {
    enqueueCommand(new Command("background") {
      def apply(e: Element) {
        e.asInstanceOf[AnyRef { def setBackground(c: BaseColor) }].setBackground(color)
      }
    })
  }

  /**
   * Converts string of rgb"0xRRGGBB" type to iText Color
   */
  implicit class ColorHelper(val sc: StringContext) {
    def rgb(args: Any*): BaseColor = {
      val colorDefHex = sc.s()
      val colorDef = if (colorDefHex.startsWith("0x")) colorDefHex.substring(2) else colorDefHex
      if (colorDef.length() != 6) {
        throw new PdfException(s"Incorrect color defined: $colorDefHex")
      }
      val r = Integer.parseInt(colorDef.substring(0, 2), 16)
      val g = Integer.parseInt(colorDef.substring(2, 4), 16)
      val b = Integer.parseInt(colorDef.substring(4, 6), 16)
      new BaseColor(r, g, b)
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
        case e: Exception => throw new PdfException(s"Incorrect location for ${item.toString}()")
      }
    }
    def enqueue(elems: A): Unit = o.enqueue(elems)
  }

  abstract class Command(val name: String) {
    def apply(e: Element): Unit
    override def toString() = name
  }
  
  private class State() {
    // Child elements - in order
    val elements: MapQueue[Element] = Queue[Element]()
    // Changes to apply to the current element
    val commands: MapQueue[Command] = Queue[Command]()
  }

  // The state of the PDF generator
  private val stateStack = Stack[State]()
  
  private def call(body: =>Unit): State = {
    val state: State = new State()
    stateStack.push(state)
    body
    stateStack.pop
  }
  
  @inline
  private def enqueueElement(e: Element) {
    if(!stateStack.isEmpty) stateStack.top.elements.enqueue(e)
  }
  
  @inline
  private def enqueueCommand(c: Command) {
    if(!stateStack.isEmpty) stateStack.top.commands.enqueue(c)
  }


}
class PdfException(msg: String) extends RuntimeException(msg)
