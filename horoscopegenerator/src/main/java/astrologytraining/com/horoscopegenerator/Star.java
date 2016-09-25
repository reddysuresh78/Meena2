package astrologytraining.com.horoscopegenerator;

public enum Star {
	
	ASWINI(Planet.KETU, 1,0,2,3),
	BHARANI(Planet.VENUS, 0,0,0,0),
	KRITIKA(Planet.SUN , 0, 0,0,0 ),
	ROHINI(Planet.MOON, 2, 0,1,3),
	MRIGASIRA(Planet.MARS, 0,0,0,0),
	ARUDRA(Planet.RAHU, 0,0,0,0),
 	PUNARVASU(Planet.JUPITER,3,4,1,2),
	PUSHYAMI(Planet.SATURN, 0,0,1,2),
	ASLESHA(Planet.MERCURY, 0,0,0,0),
	MAKHA(Planet.KETU, 0,0,0,4),
	PUBBA(Planet.VENUS, 1,0,2,0),
	UTTARA(Planet.SUN, 0,0,0,0),
	HASTHA(Planet.MOON, 0,0,0,4),
	CHITTA(Planet.MARS, 2,3,1,4),
	SWATHI(Planet.RAHU, 0,0,1,0),
	VISAKHA(Planet.JUPITER, 0,0,0,0),
	ANURADHA(Planet.SATURN, 4,0,3,0),
	JYESTA(Planet.MERCURY, 0,0,1,0),
	MOOLA(Planet.KETU, 0,0,0,0),
	PURVASHADA(Planet.VENUS, 0,0,3,4),
	UTTARASHADA(Planet.SUN, 1,2,3,4),
	SRAVANAM(Planet.MOON, 0,0,0,0),
	DHANISTA(Planet.MARS, 0,0,0,0),
	SATABHISHAM(Planet.RAHU, 3,0,2,4),
	PURVABHADRA(Planet.JUPITER, 0,0,0,0),
	UTTARABHADRA(Planet.SATURN, 0,0,0,0),
	REVATHI(Planet.MERCURY, 4,0,2,3)
	;
	  
	
	Star(Planet starLord, int vUd1, int vUd2,  int ud1, int ud2) {
		this.starLord = starLord;
		this.vUd1 = vUd1;
		this.vUd2 = vUd2;		
		this.ud1 = ud1;
		this.ud2 = ud2;
		
  	}
	
	private Planet starLord;
	
	private int vUd1;
	private int vUd2;
	private int ud1;
	private int ud2;

	public Planet getStarLord() {
		return starLord;
  	}

	 

	public int getvUd1() {
		return vUd1;
	}



	public int getvUd2() {
		return vUd2;
	}



	public int getUd1() {
		return ud1;
	}

	public int getUd2() {
		return ud2;
	}
 	  
	

}
