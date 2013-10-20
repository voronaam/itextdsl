package ca.vorona.iext.dsl

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.Paragraph
import java.io.FileOutputStream
import com.lowagie.text.Phrase
import com.lowagie.text.Chunk
import java.awt.Color

abstract class PDF extends Document {

  def file(path: String) {
    PdfWriter.getInstance(this, new FileOutputStream(path));
    open()
  }
  
  def paragraph(contents: String) {
    add(new Paragraph(contents))
  }

  def paragraph(contents: Phrase) {
    add(new Paragraph(contents))
  }

  def phrase(body: String) = {
    new Phrase(body)
  }
  
  def chunk(body: String) = {
    new Chunk
  }

  def font(size: Int = 0, family: Int = 0, style: Int = 0, color: Color = null) = {
    
  }
  
  def background(color:Color) = {
    
  }

}