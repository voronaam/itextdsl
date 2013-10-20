import ca.vorona.iext.dsl.PDF
import java.util.Date

object DslTest extends App {

  import com.lowagie.text.Document
  import com.lowagie.text.pdf.PdfWriter
  import com.lowagie.text.Paragraph
  import java.io.FileOutputStream
  
  val p = new Paragraph(s"Hello World ${new Date()}")
  PDF.file("/tmp/HelloWorld.pdf")(p)
}