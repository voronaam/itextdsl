itextdsl
========

Scala DSL for iText

I have just started this project and so far I have only defined the very basic DSL layout I'd like to get.

See /src/main/scala/DslTest.scala for a usage example.

This project has to be licensed under AGPL, because of iText license.

Important note: this DSL is NOT thread safe. You will not get any exception, but the resulting PDF could be different fomr one execution to another. The reason is that there is an internal state maintained inside the current instance.

Sample PDF definition:

```scala
  import ca.vorona.iext.dsl.PDF
  import ca.vorona.iext.dsl.PDF._
  import java.util.Date

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
    close()
  }
```

Couple of notes:
- You can define colors using RGB in hex: rgb"0xRRGGBB"
- You may NOT specify a semantically incorrect PDF. You will get an exception stating an error if you try, for example, set a background on Phrase (background should be set on Chunk).

```scala
phrase {
  chunk { "chunk 1" }
  background(rgb"0xFF0000") // Thows an exception PdfException: Incorrect location for background()
}
phrase {
  chunk { "chunk 2" }
}
```

There are several way to express the same. There are convinience helper functions. For example, all of the following generate identical PDF:

```scala
    paragraph("text")

    paragraph{"text"}
    
    paragraph {
      chunk("text")
    }
    
    paragraph {
      chunk{"text"}
    }
    
    paragraph {
      chunk { text("text") }
    }
```


- You do not have to specify file() and close() directives. In that case you may use newly constructed PDF instance the same way you would use com.lowagie.text.Document (PDF extends it).

Features I am going to implement in the newar future:
- Support greater subset of iText APIs
- Better support save to stream 
