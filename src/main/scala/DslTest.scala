import ca.vorona.iext.dsl.PDF
import ca.vorona.iext.dsl.PDF._
import java.util.Date

object DslTest extends App {

  new PDF {
    file("/tmp/HelloWorld.pdf")
    paragraph(s"Hello World ${new Date()}!")

    paragraph {
      phrase {
        chunk("This is initial text. ")
        chunk {
          text("testing text element ")
          font(style = BOLD, color = rgb"0x929083")
          background(rgb"ffe400")
        }
        for (i <- 1 to 10) chunk(s"Chunk number $i ")
      }
      phrase("Second Phrase. ")
      chunk("Chunk in the middle ")
      phrase("Third Phrase. ")
    }

    paragraph("---")
    paragraph {
      phrase {
        chunk { text("chunk 1");  }
//        background(rgb"0xFF0000") // <-- That will throw an exception now
        
      }
      phrase {
        chunk { "chunk 2" }
      }
    }
    close()
  }
}