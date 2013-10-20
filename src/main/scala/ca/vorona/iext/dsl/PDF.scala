package ca.vorona.iext.dsl

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.Paragraph
import java.io.FileOutputStream

object PDF {
  import PDF._
  
  def file(path: String) = {
    val pdf = new PDF
    PdfWriter.getInstance(pdf.document, new FileOutputStream(path));
    pdf.document.open()
    pdf
  }
}

class PDF {
  val document = new Document();
  
  /**
   * Create a document of a single paragraph
   */
  def apply(p: Paragraph) {
    document.add(p)
    document.close();
  }

}