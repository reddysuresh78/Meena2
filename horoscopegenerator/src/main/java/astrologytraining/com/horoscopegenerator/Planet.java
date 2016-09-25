package astrologytraining.com.horoscopegenerator;

public enum Planet {
	
	LAGNA(-1, ' ', new String[][]{ 
								   { "","","", "","","", "","",""  }, 
								   { "","","", "","","", "","",""  }, 
								   { "","","", "","","", "","",""  }, 
								   { "","","", "","","", "","",""  }  
								 }),  
	SUN(0, 'R', new String[][]{ { "TRT","RSR","RRS","RRT","TSR","TRS","SRT","TSR","SRS"}, 
								{ "TSR","RTS","RRT", "RSR","TTS","TRT", "SSR","TTS","SRT"  }, 
								{ "TSS","RTR","RRT", "RSS","TTR","TRT", "SSS","TTR","SRT" }, 
								{ "TSR","RRT","RRS", "RSR","TRT","TRS", "SSR","TRT","SRS" }  
							  }),
	MOON(1, 'R',new String[][]{ { "TRT","RTR","RRS", "RRT","TTR","TRS", "SRT","TTR","SRS" }, 
								{ "TRR","RTS","RRT", "RRR","TTS","TRT", "SRR","TTS","SRT" },
								{ "TSS","RTR","RRT", "RSS","TTR","TRT", "SSS","TTR","SRT" }, 
								{ "TSR","RTT","RRS", "RSR","TTT","TRS", "SSR","TTT","SRS" }  
							  }),  
	MARS(2,'T',new String[][]{  { "TST","RSR","RRS", "RST","TSR","TRS", "SST","TSR","SRS" }, 
							    { "TTR","RSS","RRT", "RTR","TSS","TRT", "STR","TSS","SRT" },
							    { "TTS","RSR","RST", "RTS","TSR","TST", "STS","TSR","SST" }, 
							    { "TTR","RST","RSS", "RTR","TST","TSS", "STR","TST","SSS" }  
							  }), 
	MERCURY(3,'S',new String[][]{ 
								{ "TST","RRR","RTS", "RST","TRR","TTS", "SST","TRR","STS" },
								{ "TSR","RRS","RTT", "RSR","TRS","TTT", "SSR","TRS","STT" },
								{ "TSS","RTR","RTT", "RSS","TTR","TTT", "SSS","TTR","STT" },
								{ "TRR","RTT","RTS", "RRR","TTT","TTS", "SRR","TTT","STS" }  
							  }), 
	JUPITER(4,'S',new String[][]{ 
								{ "TRT","RSR","RSS", "RRT","TSR","TSS", "SRT","TSR","SSS" }, 
								{ "TRR","RSS","RTT", "RRR","TSS","TTT", "SRR","TSS","STT" },
								{ "TRS","RSR","RST", "RRS","TSR","TST", "SRS","TSR","SST" },
								{ "TRR","RST","RSS", "RRR","TST","TSS", "SRR","TST","SSS" }  
							 }), 
	VENUS(5,'R',new String[][]{ 
								{ "TST","RTR","RRS", "RST","TTR","TRS", "SST","TTR","SRS" },
								{ "TSR","RTS","RRT", "RSR","TTS","TRT", "SSR","TTS","SRT" } ,
								{ "TTS","RSR","RRT", "RTS","TSR","TRT", "STS","TSR","SRT" },
								{ "TTR","RRT","RRS", "RTR","TRT","TRS", "STR","TRT","SRS" }  
							}), 
	SATURN(6,'T',new String[][]{ 
								{ "TTT","RRR","RSS", "RTT","TRR","TSS" , "STT","TRR","SSS" },
								{ "TRR","RRS","RST", "RRR","TRS","TST", "SRR","TRS","SST" },
								{ "TRS","RRR","RST", "RRS","TRR","TST", "SRS","TRR","SST" }, 
								{ "TRR","RST","RTS", "RRR","TST","TTS", "SRR","TST","STS" }  
							}), 
	RAHU(7,'T',new String[][]{ 
								{ "TTT","RRR","RSS", "RTT","TRR","TSS", "STT","TRR","SSS"  }, 
								{ "TTR","RRS","RST", "RTR","TRS","TST", "STR","TRS","SST" },
								{ "TRS","RRR","RST", "RRS","TRR","TST", "SRS","TRR","SST" }, 
								{ "TRR","RRT","RTS", "RRR","TRT","TTS", "SRR","TRT","STS" }  
							}), 
	KETU(8,'T',new String[][]{ 
								{ "TST","RRR","RSS", "RST","TRR","TSS", "SST","TRR","SSS" },
								{ "TTR","RRS","RST", "RTR","TRS","TST", "STR","TRS","SST" },
								{ "TRS","RRR","RST", "RRS","TRR","TST", "SRS","TRR","SST" }, 
								{ "TRR","RRT","RSS", "RRR","TRT","TSS", "SRR","TRT","SSS" }  
							});
  
	public static void setExalDeli() {
		LAGNA.setExalDeli( null, null);
		SUN.setExalDeli(Raasi.MESHAM, Raasi.THULA); 
		MOON.setExalDeli( Raasi.VRISHABHAM, Raasi.VRUSCHIKA); 
		MARS.setExalDeli( Raasi.MAKARAM, Raasi.KARKATAKAM);
		MERCURY.setExalDeli( Raasi.KANYA, Raasi.MEENAM);
		JUPITER.setExalDeli( Raasi.KARKATAKAM, Raasi.MAKARAM); 
		VENUS.setExalDeli( Raasi.MEENAM, Raasi.KANYA);
		SATURN.setExalDeli( Raasi.THULA, Raasi.MESHAM); 
		//Deviation for Rahu and Ketu from traditional astrology.
		RAHU.setExalDeli( Raasi.VRUSCHIKA, Raasi.VRISHABHAM); 
		KETU.setExalDeli( Raasi.VRUSCHIKA, Raasi.VRISHABHAM);
 	}
	
  Planet(int index, char guna, String[][] threeFoldGunas){
	  this.index = index;
	  this.guna = guna;
	  this.threeFoldGunas = threeFoldGunas;
  }
  
  private int index;
  private Raasi exaltation;
  private Raasi delibitation;
  private char guna;
  private String[][] threeFoldGunas;
  
  public void setExalDeli(Raasi exaltation, Raasi delibitation) {
//	  System.out.println("Setting " + exaltation);
	  this.exaltation = exaltation;
	  this.delibitation = delibitation;
  }
 
  
   public static Planet getPlanetByIndex(int index) {
 	  for(Planet planet: Planet.values()){
 		  if( planet.getIndex() == index) {
			  return planet;
		  }
 	  }
	  return null;
  }

	public int getIndex() {
		return index;
	}

	public Raasi getExaltation() {
		return exaltation;
	}

	public Raasi getDelibitation() {
		return delibitation;
	}

	public char getGuna() {
		return guna;
	}

	public String[][] getThreeFoldGunas() {
		return threeFoldGunas;
	}

	
	public String getShortName(){
 		return name().substring(0,2);
  	}

 
}
