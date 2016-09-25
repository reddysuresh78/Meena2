package astrologytraining.com.horoscopegenerator;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

import static astrologytraining.com.horoscopegenerator.CommonUtils.toDMS;

public class SwissHoraGenerator{
    
	/**
	* The method to determine ayanamsa value:
	*/
	private static final int SID_METHOD = SweConst.SE_SIDM_LAHIRI;
	private static int[] DASA_LENGTHS = { 7, 20, 6, 10, 7, 18, 16, 19, 17 };
	private static long[] DASA_LENGTHS_IN_MILLIS = new long[9];
	
	private Date dateOfBirth;
	private Calendar calendarOfBirth = Calendar.getInstance();
	private Calendar calendarOfBirthInLocalTime = Calendar.getInstance();
  	
	private int  nakshatra = 0;  
	private double balanceOfNakshatra = 0.0D;
	private int nakshatraLord = 0; 
	
	private double sunLong = 0.0D; 
	private double moonLong = 0.0D;
     
	private BirthChart birthChart; 
 
	
	private String[][] dasaDetails = { { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, 
			    { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, 
			    { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" } };

	
	private double longitude=0;
	private double latitude=0;
  
	private TempRasi[] rasis = new TempRasi[12];
	private TempRasi[] navamsaRasis = new TempRasi[12];
	private boolean localPdfOnly = false;
	
//	private astrologytraining.com.horoscopegenerator.PlanetInfo[] planets = { new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo() };
//	private astrologytraining.com.horoscopegenerator.PlanetInfo[] navamsaPlanetInfos = { new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo(), new astrologytraining.com.horoscopegenerator.PlanetInfo() };
//	  
	
	public boolean isLocalPdfOnly() {
		return localPdfOnly;
	}

	public void setLocalPdfOnly(boolean localPdfOnly) {
		this.localPdfOnly = localPdfOnly;
	}

	public SwissHoraGenerator( String nameOfNative, double longitude, double latitude, String timeOfBirth, TimeZone timeZone, String placeName, boolean signsInEnglish) {
		 
		DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    dfmt.setTimeZone(timeZone);
	    
	    this.longitude = longitude;
	    this.latitude = latitude;
	    
	    try {
			this.dateOfBirth = dfmt.parse(timeOfBirth);
		} catch (ParseException e) {
 			e.printStackTrace();
 			return;
		}

	    this.calendarOfBirth.setTimeZone(TimeZone.getTimeZone("GMT"));
	    this.calendarOfBirth.setTime(this.dateOfBirth);
	    
	    dateOfBirth.getDay();
 	    
	    String dob = getDDMMYYYY(this.calendarOfBirthInLocalTime) + 
		  	      "  " + (this.calendarOfBirthInLocalTime.get(11) < 10 ? "0" : "") + this.calendarOfBirthInLocalTime.get(11) + ":" + (this.calendarOfBirthInLocalTime.get(12) < 10 ? "0" : "") + this.calendarOfBirthInLocalTime.get(12) + ":" + (this.calendarOfBirthInLocalTime.get(13) < 10 ? "0" : "") + this.calendarOfBirthInLocalTime.get(13) + 
			      " " + this.calendarOfBirthInLocalTime.getTimeZone().getDisplayName(this.calendarOfBirthInLocalTime.getTimeZone().inDaylightTime(new Date()), 0);
	    
//	    System.out.println("Date of birth(Calendar): " + dob);
	    
	    birthChart = new BirthChart(signsInEnglish);
	    birthChart.setNativeName(nameOfNative);
		birthChart.setBirthDetails(dob);
		birthChart.setPlaceOfBirth(placeName +  " (Long: " + getDM(longitude) + " Lat: " + getDM(latitude) + ")");
		birthChart.setDay(new DateFormatSymbols().getWeekdays()[this.calendarOfBirthInLocalTime.get(7)]);
   
	    for (int i = 0; i < this.rasis.length; i++) {
	    	this.rasis[i] = new TempRasi();
	    	this.rasis[i].index = i;
	    	this.rasis[i].name = HoraStrings.RASHIS[i];
	      
	    	this.navamsaRasis[i] = new TempRasi();
	    	this.navamsaRasis[i].index = i;
	    	this.navamsaRasis[i].name = HoraStrings.RASHIS[i];
	    }

	    for (int  i = 0; i < HoraStrings.NAKSHATRA_LORDS.length; i++) {
	      GregorianCalendar calendar = new GregorianCalendar();

	      long begin = calendar.getTimeInMillis();
	      calendar.add(1, DASA_LENGTHS[i]);
	      long end = calendar.getTimeInMillis();
	      DASA_LENGTHS_IN_MILLIS[i] = (end - begin);
	    }
	    

	    
	 }
	
	public byte[] generatePDF() throws Exception{
//		 if(isLocalPdfOnly())  
//	    	  return new astrologytraining.com.horoscopegenerator.PdfGenerator().generateLocalPdf(birthChart, "test.pdf");
//	      else
	    	  return new PdfGenerator().generatePdf(birthChart, "test.pdf");
		
	}
	
	private void calculatePlanets(){
		/*Instances of utility classes */
		SwissEph sw = new SwissEph();
		sw.swe_set_ephe_path("");
		SweDate sd = new SweDate( calendarOfBirth.get(Calendar.YEAR), calendarOfBirth.get(Calendar.MONTH)+1, calendarOfBirth.get(Calendar.DAY_OF_MONTH), 
				calendarOfBirth.get(Calendar.HOUR_OF_DAY) +  ( calendarOfBirth.get(Calendar.MINUTE)/60.0) );

		// Set sidereal mode:
		sw.swe_set_sid_mode(SID_METHOD, 0, 0);

		// Some required variables:
		double[] cusps = new double[13];
		double[] acsc = new double[10];
		double[] xp= new double[6];
		StringBuffer serr = new StringBuffer();

		// Print input details:
		System.out.println("Date: " + sd);
		System.out.println("Location: " +
				toDMS(this.longitude) + (this.longitude > 0 ? "E" : "W") +
				" / " +
				toDMS(this.latitude) + (this.latitude > 0 ? "N" : "S"));

		// Get and print ayanamsa value for info:
		double ayanamsa = sw.swe_get_ayanamsa_ut(sd.getJulDay());
		System.out.println("Ayanamsa: " + toDMS(ayanamsa) + " (" + sw.swe_get_ayanamsa_name(SID_METHOD) + ")");

		// Get and print lagna:
		int flags =  SweConst.SEFLG_MOSEPH | SweConst.SEFLG_SIDEREAL;
		int result = sw.swe_houses(sd.getJulDay(),
				flags,
				latitude,
				longitude,
				'P',
				cusps,
				acsc);
		System.out.println("Ascendant: " + toDMS(acsc[0]));

		int ascSign = (int)(acsc[0] / 30) + 1;
   	     
		//Set lagna info
	    PlanetInfo planetInfo = new PlanetInfo();
	    planetInfo.index = -1;
	    planetInfo.longitude =  (acsc[0] - (int)Math.floor(acsc[0] / 30.0D) * 30);
	    planetInfo.isRetrograde = false;
	    this.rasis[ascSign].planets.add(planetInfo);
	    
	    debugPrint("Ascendant: " + getDM(acsc[0]));
	  	
		
		System.out.println("Ascendant Sign: " + ascSign);

		// Calculate all planets:
		int[] planets = { 
				SweConst.SE_SUN,
				SweConst.SE_MOON,
				SweConst.SE_MERCURY,
				SweConst.SE_VENUS,
				SweConst.SE_MARS,
				SweConst.SE_JUPITER,
				SweConst.SE_SATURN,
				SweConst.SE_MEAN_NODE  };	// Some systems prefer SE_MEAN_NODE

		flags = SweConst.SEFLG_MOSEPH |		// fastest method, requires data files
				SweConst.SEFLG_SIDEREAL |	// sidereal zodiac
				SweConst.SEFLG_NONUT |		// will be set automatically for sidereal calculations, if not set here
				SweConst.SEFLG_SPEED;		// to determine retrograde vs. direct motion
		int sign;
		int house;
		boolean retrograde = false;

		for(int p = 0; p < planets.length; p++) {
			int planet = planets[p];
			String planetName = sw.swe_get_planet_name(planet);
			int ret = sw.swe_calc_ut(sd.getJulDay(),
					planet,
					flags,
					xp,
					serr);

			if (ret != flags) {
				if (serr.length() > 0) {
					System.err.println("Warning: " + serr);
				} else {
					System.err.println(
							String.format("Warning, different flags used (0x%x)", ret));
				}
			}

			sign = (int)(xp[0] / 30) + 1;
			house = (sign + 12 - ascSign) % 12 +1;
			retrograde = (xp[3] < 0);

			System.out.printf("%-12s: %s %c; sign: %2d; house %2d; at: %s\n",
					 planetName, toDMS(xp[0]), (retrograde ? 'R' : 'D'), sign, house, toDMS(xp[0] % 30));
			
			
			planetInfo = new PlanetInfo();
			planetInfo.index = p;
			planetInfo.longitude = (xp[0] - (int)Math.floor(xp[0] / 30.0D) * 30); 
			planetInfo.isRetrograde = xp[3] < 0.0D;
			
			//TODO - Fix this
//		    this.rasis[sign].planets.add(planetInfo);
		    
		    if(p==0){
		    	this.sunLong = xp[0];
		    }else if(p==1){
		    	this.moonLong = xp[0];
		    }
		    
		}
		// KETU
		xp[0] = (xp[0] + 180.0) % 360;
		String planetName = "Ketu";

		sign = (int)(xp[0] / 30) + 1;
		house = (sign + 12 - ascSign) % 12 +1;
		
		planetInfo = new PlanetInfo();
		planetInfo.index = 8;
		planetInfo.longitude = xp[0];
		planetInfo.isRetrograde = xp[3] < 0.0D;
	    this.rasis[sign-1].planets.add(planetInfo);

		System.out.printf("%-12s: %s %c; sign: %2d; house %2d; at: %s\n",
				 planetName, toDMS(xp[0]), (retrograde ? 'R' : 'D'), sign, house, toDMS(xp[0] % 30));
		
		
		System.out.println("Values: " + Arrays.toString(this.rasis));
		
	}
	
	public void calculateDetails() throws Exception {
		
		calculatePlanets();
		calculatePanchanga();
	    calculateNavamsa();
	    mapToModel(rasis, navamsaRasis,  birthChart); 	      
//	      System.out.println(Arrays.toString( this.rasis));
//	      System.out.println(Arrays.toString( this.navamsaRasis ));
//	      System.out.println("Birth Chart: " + birthChart);
	      calculateDasas();
	      birthChart.setDasaDetails(this.dasaDetails);
	      
	      Arrays.sort(rasis);
	      System.out.println("Values: " + Arrays.toString(this.rasis));
	}
  
 
  private void calculateNavamsa(){
 	  
	  double divLength = 3.333333333333334D;

	    for (int i = 0; i < this.rasis.length; i++) {
	      TempRasi rasi = this.rasis[i];
	      boolean isMovable = false; boolean isFixed = false;

	      isMovable = (i + 1) % 3 == 1;
	      isFixed = (i + 1) % 3 == 2;

	      for (int j = 0; j < rasi.planets.size(); j++) {
	        PlanetInfo planetInfo = (PlanetInfo)rasi.planets.get(j);
	        PlanetInfo navamsaPlanet = new PlanetInfo();
	        int division = 0;
	        int index = 0;

	        division = (int)Math.floor(planetInfo.longitude / divLength);

	        if (isMovable)
	          index = (i + division) % 12;
	        else if (isFixed)
	          index = (i + division + 8) % 12;
	        else {
	          index = (i + division + 4) % 12;
	        }
	        navamsaPlanet.index  = planetInfo.index;
	        navamsaPlanet.longitude = ((planetInfo.longitude - division * divLength) / divLength * 30.0D);
	        navamsaPlanet.isRetrograde =  planetInfo.isRetrograde ;
  
	        this.navamsaRasis[index].planets.add(navamsaPlanet);
	      }  
	    }
	  
	  
  }
     
  private void mapToModel(TempRasi[] rasis, TempRasi[] navamsaRasis,   BirthChart birthChart) {
	  
	  boolean isLagna = false;
	  
	  Chakra raasiChakra = new Chakra();
	  
	  raasiChakra.setChakraName("astrologytraining.com.horoscopegenerator.Raasi") ;
	  
	  int lagnaNo = 1;
	  
	  for(TempRasi tempRasi: rasis) {
		  
		  House house = new House();
		  
		  Raasi raasi = Raasi.getRaasi(tempRasi.getIndex());
		   
		  house.setRaasi(raasi);
		  
		  for(PlanetInfo planetInfo : tempRasi.getPlanets()) {
 			  planetInfo.setPlanet(Planet.getPlanetByIndex(planetInfo.getIndex()));
 			  planetInfo.setHouse(house);
 			  raasiChakra.getPlanetInfo().add(planetInfo);
 			  if(planetInfo.getIndex() == -1) {
 				  isLagna = true;
 			  }
		  }
		  
		  house.getPlanets().addAll( tempRasi.getPlanets());
   		   
		  raasiChakra.getHouses().add(house);
		   
		  if(isLagna){
			  lagnaNo = raasiChakra.getHouses().size() - 1 ; 
			  house.setAssignedHouseNo(1);
			  raasiChakra.setLagna(house);
			  isLagna = false;
		  }
	  
	  }
	  
	  int i=0;
	  int houseNo = (12 - lagnaNo + 1) % 12;
  	  if(houseNo == 0)
  		  houseNo = 12;
	  
	  for(House house: raasiChakra.getHouses()){
		  house.setAssignedHouseNo(houseNo++ % 13);
		  raasiChakra.getOrderedHouses().add(house);
		  if(houseNo % 13 == 0) {
			  houseNo = 1;
		  }
 	  }
	  
	  for(House house: raasiChakra.getHouses()){
		  raasiChakra.getOrderedHouses().set(house.getAssignedHouseNo() -1 , house);
   	  }
 
	  
	  i=0;
	  for(House house: raasiChakra.getOrderedHouses()){
 		  for(PlanetInfo planetInfo: house.getPlanets()) {
			  switch(planetInfo.getPlanet()){
			  	
			  case JUPITER: //5,7,9
				  raasiChakra.getOrderedHouses().get((i+4) % 12).getAspectedPlanets().add(planetInfo);
				  raasiChakra.getOrderedHouses().get((i+6) % 12).getAspectedPlanets().add(planetInfo);
				  raasiChakra.getOrderedHouses().get((i+8) % 12).getAspectedPlanets().add(planetInfo);
				  break;
			  case MARS: //4,7,8
				  raasiChakra.getOrderedHouses().get((i+3) % 12).getAspectedPlanets().add(planetInfo);
				  raasiChakra.getOrderedHouses().get((i+6) % 12).getAspectedPlanets().add(planetInfo);
				  raasiChakra.getOrderedHouses().get((i+7) % 12).getAspectedPlanets().add(planetInfo);
				  break;
				  
			  case SATURN: //3,7,10
				  raasiChakra.getOrderedHouses().get((i+2) % 12).getAspectedPlanets().add(planetInfo);
				  raasiChakra.getOrderedHouses().get((i+6) % 12).getAspectedPlanets().add(planetInfo);
				  raasiChakra.getOrderedHouses().get((i+9) % 12).getAspectedPlanets().add(planetInfo);
				  break;
 				  
			  case LAGNA: //No aspects
				  break;
			  default: //7 only
				  raasiChakra.getOrderedHouses().get((i+6) % 12).getAspectedPlanets().add(planetInfo);
				  break;
 			  }
 		  }
		  i++;
   	  }
	   
	  Chakra navamsaChakra = new Chakra();
	  
	  navamsaChakra.setChakraName("Navamsa") ;
	  
	  for(TempRasi tempRasi: navamsaRasis) {
		  
		  House house = new House();
		  
		  Raasi raasi = Raasi.getRaasi(tempRasi.getIndex());
		   
		  house.setRaasi(raasi);
		  
		  for(PlanetInfo planetInfo : tempRasi.getPlanets()) {
 			  planetInfo.setPlanet(Planet.getPlanetByIndex(planetInfo.getIndex()));
 			  planetInfo.setHouse(house);
 			 navamsaChakra.getPlanetInfo().add(planetInfo);
 			  if(planetInfo.getIndex() == -1) {
 				  isLagna = true;
 			  }
		  }
		  
		  house.getPlanets().addAll( tempRasi.getPlanets());
 		   
		  navamsaChakra.getHouses().add(house);
		  if(isLagna){
			  lagnaNo = navamsaChakra.getHouses().size() - 1; 
			  house.setAssignedHouseNo(1);
			  navamsaChakra.setLagna(house);
			  isLagna = false;
		  }
	  
	  }
 
	  houseNo = (12 - lagnaNo + 1)%12;
	  if(houseNo == 0)
		  houseNo =12;
  
	  for(House house: navamsaChakra.getHouses()){
		  house.setAssignedHouseNo(houseNo++ % 13);
		  navamsaChakra.getOrderedHouses().add(house);
		  if(houseNo % 13 == 0) {
			  houseNo = 1;
		  }
 	  }
	  
	  for(House house: navamsaChakra.getHouses()){
		  navamsaChakra.getOrderedHouses().set(house.getAssignedHouseNo()-1 , house);
	   }
     
	  birthChart.setRaasiChakra(raasiChakra);
	  birthChart.setNavamsaChakra(navamsaChakra);
 	  
   }
  
  	private void calculatePanchanga() {  
  
	    double adjustedMoonLong = this.moonLong >= this.sunLong ? this.moonLong : this.moonLong + 360.0D;
	    int tithi = ((int)Math.floor((adjustedMoonLong - this.sunLong) / 12.0D) + 1);

	    if (tithi <= 15) {  
	       birthChart.setTithi(HoraStrings.PAKSHAS[0] + " " + HoraStrings.TITHIS[tithi]);
	    }
	    else if (tithi < 30) {
	    	birthChart.setTithi( HoraStrings.PAKSHAS[1] + " " + HoraStrings.TITHIS[(tithi - 15)]);
	    }
	    else {
	    	birthChart.setTithi(HoraStrings.PAKSHAS[1] + " " + HoraStrings.TITHIS[0]);
	    }

	    int nakshatra = ((int)Math.floor(this.moonLong / 13.333333333333334D));
	    double balanceOfNakshatra = (1.0D - (this.moonLong / 13.333333333333334D - nakshatra));
	    int pada = ((int)Math.floor((1.0D - balanceOfNakshatra) / 0.25D) + 1);
	    
	    this.nakshatra = nakshatra;
	    this.balanceOfNakshatra = balanceOfNakshatra;
	     
	    birthChart.setNakshatra(HoraStrings.NAKSHATRAS[nakshatra]);
	    birthChart.setPada(String.valueOf(pada));
	    birthChart.setBalanceNakshtra(Double.valueOf(new DecimalFormat("#.##").format(balanceOfNakshatra * 100.0D)) + "% ");

	  }
  
  private void calculateDasas() throws Exception
  {  
	  
	  int i = 0;int j = 0;int dasaIndex = 0;
	  long elapsedMillis = 0L;
	  Calendar dasaTime = new GregorianCalendar();
 
	  dasaTime.setTimeInMillis(calendarOfBirthInLocalTime.getTimeInMillis());

	  nakshatraLord = (nakshatra % 9);

	  elapsedMillis =(long) ((1.0D - balanceOfNakshatra) * DASA_LENGTHS_IN_MILLIS[nakshatraLord]);
	  dasaTime.setTimeInMillis(dasaTime.getTimeInMillis() - elapsedMillis);
	  Calendar antarDasaTime = (Calendar)dasaTime.clone();
	  dasaDetails[dasaIndex][0] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[nakshatraLord] + " " + HoraStrings.MAHA_DASA + ": " + getDDMMYY(dasaTime) + " to ");
	  dasaTime.setTimeInMillis(dasaTime.getTimeInMillis() + DASA_LENGTHS_IN_MILLIS[nakshatraLord]); int
	  
	  tmp181_180 = 0; 
	  
	  String[] tmp181_179 = dasaDetails[dasaIndex];tmp181_179[tmp181_180] = (tmp181_179[tmp181_180] + getDDMMYY(dasaTime));
	  calculateAntarDasas(nakshatraLord, antarDasaTime, dasaIndex);

	  for (i = nakshatraLord + 1; i < HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES.length; i++) {
		  dasaIndex++;
		  antarDasaTime = (Calendar)dasaTime.clone();
		  dasaDetails[dasaIndex][0] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + HoraStrings.MAHA_DASA + ": " + getDDMMYY(dasaTime) + " to ");
		  dasaTime.setTimeInMillis(dasaTime.getTimeInMillis() + DASA_LENGTHS_IN_MILLIS[i]); 
		  
		  int tmp332_331 = 0; 
		  
		  String[] tmp332_330 = dasaDetails[dasaIndex];
		  
		  tmp332_330[tmp332_331] = (tmp332_330[tmp332_331] + getDDMMYY(dasaTime));
	  	  
		  calculateAntarDasas(i, antarDasaTime, dasaIndex);
	  }
	  for (i = 0; i < nakshatraLord; i++) {
		  dasaIndex++;
		  Calendar tmp332_331 = (Calendar)dasaTime.clone();
		  dasaDetails[dasaIndex][0] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + HoraStrings.MAHA_DASA + ": " + getDDMMYY(dasaTime) + " to ");
		  dasaTime.setTimeInMillis(dasaTime.getTimeInMillis() + DASA_LENGTHS_IN_MILLIS[i]); int
		  tmp490_489 = 0; String[] tmp490_488 = dasaDetails[dasaIndex];tmp490_488[tmp490_489] = (tmp490_488[tmp490_489] + getDDMMYY(dasaTime));
		  calculateAntarDasas(i, tmp332_331, dasaIndex);
	  }

	  for (i = 0; i <= dasaIndex; i++) {
		  for (j = 0; j < dasaDetails[i].length; j++) {
			  debugPrint(dasaDetails[i][j]);
		  }
	  }
	  
  }
 
private void calculateAntarDasas(int dasaLord, Calendar antarDasaTime, int dasaIndex){
	
	 int i = 0;
	 int antarDasaIndex = 0;
	 long antarDasaLengthInMillis = 0L;
	 
	 Calendar pratyantarDasaTime = (Calendar)antarDasaTime.clone();

	 antarDasaLengthInMillis = (long) (DASA_LENGTHS[dasaLord] / 120.0D * DASA_LENGTHS_IN_MILLIS[dasaLord]);
	 dasaDetails[dasaIndex][(antarDasaIndex + 1)] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[dasaLord] + " " + HoraStrings.ANTAR_DASA);
	 antarDasaTime.setTimeInMillis(antarDasaTime.getTimeInMillis() + antarDasaLengthInMillis);
	 calculatePratyantarDasas(dasaLord, antarDasaLengthInMillis, pratyantarDasaTime, dasaIndex, antarDasaIndex);

	 for (i = dasaLord + 1; i < HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES.length; i++) {
		 antarDasaIndex++;
		 antarDasaLengthInMillis = (long)(DASA_LENGTHS[i] / 120.0D * DASA_LENGTHS_IN_MILLIS[dasaLord]);
		 pratyantarDasaTime = (Calendar)antarDasaTime.clone();
		 dasaDetails[dasaIndex][(antarDasaIndex + 1)] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + HoraStrings.ANTAR_DASA);
		 antarDasaTime.setTimeInMillis(antarDasaTime.getTimeInMillis() + antarDasaLengthInMillis);
		 calculatePratyantarDasas(i, antarDasaLengthInMillis, pratyantarDasaTime, dasaIndex, antarDasaIndex);
	 }

	 for (i = 0; i < dasaLord; i++) {
		 antarDasaIndex++;
		 antarDasaLengthInMillis = (long) (DASA_LENGTHS[i] / 120.0D * DASA_LENGTHS_IN_MILLIS[dasaLord]);
		 pratyantarDasaTime = (Calendar)antarDasaTime.clone();
		 dasaDetails[dasaIndex][(antarDasaIndex + 1)] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + HoraStrings.ANTAR_DASA);
		 antarDasaTime.setTimeInMillis(antarDasaTime.getTimeInMillis() + antarDasaLengthInMillis);
		 calculatePratyantarDasas(i, antarDasaLengthInMillis, pratyantarDasaTime, dasaIndex, antarDasaIndex);
	 }
	 
}
 
  private void calculatePratyantarDasas(int antarDasaLord, long antarDasaLengthInMillis, Calendar pratyantarDasaTime, int dasaIndex, int antarDasaIndex) {
	  long pratyantarDasaLengthInMillis = 0L;

	  pratyantarDasaLengthInMillis =(long) (DASA_LENGTHS[antarDasaLord] / 120.0D * antarDasaLengthInMillis); 
	  
	  int tmp31_30 = (antarDasaIndex + 1); 
	  String[] tmp31_26 = dasaDetails[dasaIndex];
	  tmp31_26[tmp31_30] = (tmp31_26[tmp31_30] + "\n" +HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[antarDasaLord] + " " + getDDMMYY(pratyantarDasaTime));
	  
	  pratyantarDasaTime.setTimeInMillis(pratyantarDasaTime.getTimeInMillis() + pratyantarDasaLengthInMillis);

	  for (int i = antarDasaLord + 1; i < HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES.length; i++) {
		  pratyantarDasaLengthInMillis = (long) (DASA_LENGTHS[i] / 120.0D * antarDasaLengthInMillis); 
		  int xyz = (antarDasaIndex + 1); 
		  String[] str = dasaDetails[dasaIndex];
		  str[xyz] = (str[xyz] + "\n" +HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + getDDMMYY(pratyantarDasaTime));
		  pratyantarDasaTime.setTimeInMillis(pratyantarDasaTime.getTimeInMillis() + pratyantarDasaLengthInMillis);
	  }

	  for (int i = 0; i < antarDasaLord; i++) {
		  pratyantarDasaLengthInMillis = (long) (DASA_LENGTHS[i] / 120.0D * antarDasaLengthInMillis); 
		  int tmp246_245 = (antarDasaIndex + 1); 
		  String[] tmp246_241 = dasaDetails[dasaIndex];
		  tmp246_241[tmp246_245] = (tmp246_241[tmp246_245] + "\n" + HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + getDDMMYY(pratyantarDasaTime));
		  pratyantarDasaTime.setTimeInMillis(pratyantarDasaTime.getTimeInMillis() + pratyantarDasaLengthInMillis);
	  }
	  
  }
   

  private void debugPrint(String message)
  {
//      System.out.println(message);
  }

  private String getDM(double value) {
    String retVal = "";
    int deg = 0; int min = 0;

    deg = (int)Math.floor(value);
    min = (int)Math.floor(value * 60.0D - deg * 60);

    retVal = deg + "� " + min + "'";

    return retVal;
  }

  private String getDDMMYY(Calendar calendar)
  {
    String retVal = "";

    String day = Integer.valueOf(calendar.get(5)).toString();
    String month = Integer.valueOf(calendar.get(2) + 1).toString();
    String year = Integer.valueOf(calendar.get(1)).toString();

    retVal = day.length() < 2 ? "0" + day : day;
    retVal = retVal + "-" + (month.length() < 2 ? "0" + month : month);
    retVal = retVal + "-" + (year.length() < 2 ? "0" + year : year.substring(year.length() - 2, year.length()));

    return retVal;
  }

  private String getDDMMYYYY(Calendar calendar) {
    String retVal = "";

    String day = Integer.valueOf(calendar.get(5)).toString();
    String month = Integer.valueOf(calendar.get(2) + 1).toString();
    String year = Integer.valueOf(calendar.get(1)).toString();

    retVal = day.length() < 2 ? "0" + day : day;
    retVal = retVal + "-" + (month.length() < 2 ? "0" + month : month);
    retVal = retVal + "-" + (year.length() < 4 ? "0" + year : year.length() < 3 ? "00" + year : year.length() < 2 ? "000" + year : year);

    return retVal;
  }

   
  
 
  
  private class TempRasi implements Comparable<TempRasi> { 
	  
	private int index = 0;
    private String name = "";
    private List<PlanetInfo> planets = new ArrayList<PlanetInfo>(); 
    
	public int getIndex() {
		return index;
	}
  
	public List<PlanetInfo> getPlanets() {
		return planets;
	}
	
	

	@Override
	public String toString() {
		return "Rasi [index=" + index + ", name=" + name + ", planets="
				+ planets + "]";
	}

	@Override
	public int compareTo(TempRasi tempRasi) {
		
		return this.index - tempRasi.getIndex();
	}
     
  }
  
  
  
 
  
}