import ca.vorona.iext.dsl.PDF
import ca.vorona.iext.dsl.PDF._
import java.util.Date

object DslTest extends App {
  
  new PDF {
    file("/tmp/HelloWorld.pdf")
    paragraph(s"Hello World ${new Date()}!")
    
    paragraph {
      phrase {
        chunk {
          font (style = BOLD, color = rgb"0x929083")
          background(rgb"ffe400")
          "testing text element "
        }
        for(i <- 1 to 10) chunk(s"Chunk number $i ")
        "This is initial text. "
      }
      phrase("Second Phrase. ")
      phrase("Third Phrase. ")
    }
    close()
  }
}