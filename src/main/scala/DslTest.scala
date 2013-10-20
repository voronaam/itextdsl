import ca.vorona.iext.dsl.PDF
import java.util.Date
import java.awt.Color

object DslTest extends App {

  import com.lowagie.text.Font;

  new PDF {
    file("/tmp/HelloWorld.pdf")
    paragraph(s"Hello World ${new Date()}")
    
    paragraph {
      phrase {
        chunk {
          font (
            family = Font.COURIER,
            size = 10,
            style = Font.BOLD,
            color = new Color(0x92, 0x90, 0x83) // TODO: rgb"929083"
          )
          background(new Color(0xff, 0xe4, 0x00)) // TODO: rgb"ffe400"
          "testing text element "
        }
        "This is initial text. "
      }
    }
    close()
  }
}