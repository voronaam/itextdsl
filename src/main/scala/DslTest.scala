import ca.vorona.iext.dsl.PDF
import java.util.Date

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
            color = rgb"929083"
          )
          background(rgb"ffe400")
          "testing text element "
        }
        "This is initial text. "
      }
    }
    close()
  }
}