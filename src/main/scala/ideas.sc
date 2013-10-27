object ideas {
  import ca.vorona.iext.dsl.PDF
  
  new PDF {
    val red = rgb"0xff6633"
  }.red                                           //> res0: java.awt.Color = java.awt.Color[r=255,g=102,b=51]
  
  new PDF {
    font(10)
    state.font.map(println)
    state.font.map(println)
  }.state.font                                    //> com.lowagie.text.Font@1958e4ee
                                                  //| res1: ca.vorona.iext.dsl.PDF.OneTimeOption[com.lowagie.text.Font] = ca.voron
                                                  //| a.iext.dsl.PDF$OneTimeOption@42d6f8f6
}