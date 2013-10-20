import ca.vorona.iext.dsl.PDF
import java.util.Date

object DslTest extends App {

  new PDF { 
    file("/tmp/HelloWorld.pdf")
    paragraph(s"Hello World ${new Date()}")
    close()
  }
}