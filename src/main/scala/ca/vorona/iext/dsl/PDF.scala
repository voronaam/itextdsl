package ca.vorona.iext.dsl

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.Paragraph
import java.io.FileOutputStream

abstract class PDF extends Document {

  def file(path: String) {
    PdfWriter.getInstance(this, new FileOutputStream(path));
    open()
  }
  
  def paragraph(contents: String) {
    add(new Paragraph(contents))
  }
  
}