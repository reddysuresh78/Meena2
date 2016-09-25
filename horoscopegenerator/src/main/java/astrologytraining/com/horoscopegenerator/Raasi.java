package astrologytraining.com.horoscopegenerator;

public enum Raasi {

	MESHAM(0,Planet.MARS, Swabhava.CHARA, Tatwa.AGNI_TATWA, Gender.BESI, "ARIES",  Planet.SUN, Planet.MOON, Planet.JUPITER), 
	VRISHABHAM(1,Planet.VENUS, Swabhava.STHIRA, Tatwa.BHU_TATWA, Gender.SARI, "TAURUS", Planet.MERCURY), 
	MIDHUNAM(2, Planet.MERCURY, Swabhava.DWI_SWABHAVA, Tatwa.VAYU_TATWA,Gender.BESI, "GEMINI", Planet.VENUS), 
	KARKATAKAM(3, Planet.MOON, Swabhava.CHARA, Tatwa.JALA_TATWA,Gender.SARI, "CANCER", Planet.JUPITER), 
	SIMHAM(4, Planet.SUN, Swabhava.STHIRA, Tatwa.AGNI_TATWA,Gender.BESI, "LEO", Planet.JUPITER), 
	KANYA(5, Planet.MERCURY, Swabhava.DWI_SWABHAVA, Tatwa.BHU_TATWA,Gender.SARI, "VIRGO", Planet.VENUS), 
	THULA(6, Planet.VENUS, Swabhava.CHARA, Tatwa.VAYU_TATWA,Gender.BESI, "LIBRA", Planet.MERCURY), 
	VRUSCHIKA(7, Planet.MARS, Swabhava.STHIRA, Tatwa.JALA_TATWA,Gender.SARI, "SCORPIO",  Planet.SUN, Planet.MOON, Planet.JUPITER), 
	DHANUS(8,Planet.JUPITER,Swabhava.DWI_SWABHAVA, Tatwa.AGNI_TATWA,Gender.BESI, "SAGITTARIUS", Planet.SUN), 
	MAKARAM(9, Planet.SATURN, Swabhava.CHARA, Tatwa.BHU_TATWA,Gender.SARI, "CAPRICORN", Planet.VENUS, Planet.MERCURY), 
	KUMBHAM(10, Planet.SATURN, Swabhava.STHIRA, Tatwa.VAYU_TATWA,Gender.BESI, "AQUARIUS",  Planet.VENUS, Planet.MERCURY), 
	MEENAM(11, Planet.JUPITER, Swabhava.DWI_SWABHAVA, Tatwa.JALA_TATWA,Gender.SARI, "PISCES", Planet.MOON);
  	
	Raasi(int raasiNo, Planet raasiLord, Swabhava swabhava, Tatwa tatwa, Gender gender, String englishName,  Planet... yogakarakas) {
		this.raasiNo = raasiNo;
		this.raasiLord = raasiLord;
		this.swabhava = swabhava;
		this.tatwa = tatwa;     
		this.gender = gender;
		this.yogaKarakas = yogakarakas;
		this.englishName = englishName;
	}
	
	private String englishName;
	
	private int raasiNo;

	private Planet raasiLord;

	private Swabhava swabhava;

	private Tatwa tatwa;

	private Gender gender;
	
	private Planet[] yogaKarakas;

	public Planet getRaasiLord() {
		return raasiLord;
	}

	public Swabhava getSwabhava() {
		return swabhava;
	}
   
	public Tatwa getTatwa() {
		return tatwa;
	}

	 
	public Gender getGender() {
		return gender;
	}
	 
  
	public int getRaasiNo() {
		return raasiNo;
	}
	
	

	public Planet[] getYogaKarakas() {
		return yogaKarakas;
	}

	public void setYogaKarakas(Planet[] yogaKarakas) {
		this.yogaKarakas = yogaKarakas;
	}

	public static Raasi getRaasi(int index){
  	  for(Raasi raasi : Raasi.values()){
 		  if( raasi.getRaasiNo() == index) {
			  return raasi;
		  }
 	  }
	  return null;
 	}
	
	public String getShortName(){
 		return name().substring(0,3);
  	}
	
	public String getName(){
 		return name();
  	}
	
	public String getEnglishShortName() {
		return englishName.substring(0,3);
	}
	
	public String getEnglishName() {
		return englishName;
	}

	
}
