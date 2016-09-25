package astrologytraining.com.horoscopegenerator;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import swisseph.SweDate;
import swisseph.SwissEph;

public class HoraGenerator 
{
  static String EPHEMERIS_PATH = "";
  
  static boolean IS_DEBUG_MODE = false;
  static boolean USE_PLANET_ICONS = false;
  static boolean COLOUR_TABLE_HEADERS = false;
  static String INDENTATION = "          ";
  static DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");
  static float RESULTS_IMAGE_WIDTH = 490.0F;

  int[] PLANETS = { 0, 1, 4, 2, 5, 3, 6, 10 }; //Here the last argument 10 means it is mean node.
    
  int[] DASA_LENGTHS = { 7, 20, 6, 10, 7, 18, 16, 19, 17 };
  long[] DASA_LENGTHS_IN_MILLIS = new long[9];
 
  static ArrayList<TimeZone> timeZones = new ArrayList<TimeZone>();
  static ArrayList<String> timeZoneNames = new ArrayList<String>();

  double top_long = 83.299999999999997D;
  double top_lat = 17.699999999999999D;
  Date dateOfBirth;
  Calendar calendarOfBirth = Calendar.getInstance();
  Calendar calendarOfBirthInLocalTime = Calendar.getInstance();
  
  Calendar displayCalendarOfBirthInLocalTime = Calendar.getInstance();
  int jday = 1; int jmon = 8; int jyear = 1977;
  double thour = 18.583333D;
  

  SwissEph sw = new SwissEph();
  SweDate sd = null;
  double sunLong = 0.0D; double moonLong = 0.0D;
  int  nakshatra = 0; int nakshatraLord = 0; 
  double balanceOfNakshatra = 0.0D;
  TempRasi[] rasis = new TempRasi[12];
  TempRasi[] navamsaRasis = new TempRasi[12];

  PlanetInfo[] planets = { new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo() };
  PlanetInfo[] navamsaPlanetInfos = { new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo(), new PlanetInfo() };
  TempRasi lagnaRasi;
  int rahuEquivalent;
  int ketuEquivalent;
  
  BirthChart birthChart;



	String filePath;
	Map<String, byte[]> imageData;

 
  String name = "";
  
  String place = "";
//  String chartDetails = "";
  
  String[][] dasaDetails = { { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, 
    { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, 
    { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "" } };

   String headerSuffix = "";
  String headerSuffixSecondLine = "";
  boolean insertHeader = true;
  
  boolean local = false;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setImageData(Map<String, byte[]> imageData) {
		this.imageData = imageData;
	}

	public boolean isLocal() {
	return local;
}

public void setLocal(boolean local) {
	this.local = local;
}

public HoraGenerator( String nameOfNative, double longitude, double latitude, String timeOfBirth, TimeZone timeZone, String placeName, boolean signsInEnglish)
    throws Exception
  {
    DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    int i = 0;
 
    this.place = placeName;
    this.name = nameOfNative;
    dfmt.setTimeZone(timeZone);
    this.dateOfBirth = dfmt.parse(timeOfBirth);

    this.displayCalendarOfBirthInLocalTime.setTimeZone(timeZone);
    this.displayCalendarOfBirthInLocalTime.setTime(this.dateOfBirth);
  
    Calendar cal = Calendar.getInstance();
	cal.setTime(dateOfBirth);
	
	//For war time, Add 1 hour. For dates war time, add 23 minutes
    if(cal.get(Calendar.YEAR) < 1946 && cal.get(Calendar.YEAR) >=1942) {
    	System.out.println("Old date and time is " + this.dateOfBirth);    	
    	cal.add(Calendar.HOUR, 1);
    	this.dateOfBirth = cal.getTime();
    	System.out.println("New date and time is " + this.dateOfBirth);
    }else if(cal.get(Calendar.YEAR) < 1942) {
    	System.out.println("Old date and time is " + this.dateOfBirth);    	
    	cal.add(Calendar.MINUTE, 23);
    	cal.add(Calendar.SECOND, 15);
    	this.dateOfBirth = cal.getTime();
    	System.out.println("New date and time is " + this.dateOfBirth);
    }

	  //If it is running on android, somehow the GMT date is calculated as IST + 5.30
//	  calendarOfBirth.setTimeInMillis(this.dateOfBirth.getTime() -  19800000);
//	  calendarOfBirth.getTimeInMillis();

//	  calendarOfBirth.setTime(this.dateOfBirth);



	  this.calendarOfBirth.setTimeZone(TimeZone.getTimeZone("GMT"));
	  this.calendarOfBirth.setTime(this.dateOfBirth);
		//Make it GMT
//	  calendarOfBirth.add(Calendar.HOUR_OF_DAY, -5);
//	  calendarOfBirth.add(Calendar.MINUTE, -30);


    this.jday = this.calendarOfBirth.get(5);
    this.jmon = (this.calendarOfBirth.get(2) + 1);
    this.jyear = this.calendarOfBirth.get(1);
    this.thour = (this.calendarOfBirth.get(11) + this.calendarOfBirth.get(12) / 60.0D + this.calendarOfBirth.get(13) / 3600.0D);

    this.calendarOfBirthInLocalTime.setTimeZone(timeZone);
    this.calendarOfBirthInLocalTime.setTime(this.dateOfBirth);
  
    this.top_long = longitude;
    this.top_lat = latitude;
 

    for (i = 0; i < this.rasis.length; i++) {
    	this.rasis[i] = new TempRasi();
      this.rasis[i].index = i;
      this.rasis[i].name = HoraStrings.RASHIS[i];
      
      this.navamsaRasis[i] = new TempRasi();
      this.navamsaRasis[i].index = i;
      this.navamsaRasis[i].name = HoraStrings.RASHIS[i];
    }

    for (i = 0; i < HoraStrings.NAKSHATRA_LORDS.length; i++) {
      GregorianCalendar calendar = new GregorianCalendar();

      long begin = calendar.getTimeInMillis();
      calendar.add(1, this.DASA_LENGTHS[i]);
      long end = calendar.getTimeInMillis();
      this.DASA_LENGTHS_IN_MILLIS[i] = (end - begin);
    }
    
    birthChart = new BirthChart(signsInEnglish);
  }

  public byte[] generate() throws Exception {
    double jut = 0.0D;
    double top_elev = 0.0D;
    double tjd = 2415020.5D;

    double[] x = new double[6];
    double[] cusp = new double[13];
    double[] ascmc = new double[10];
    double ketuLong = 0.0D;
    boolean gregflag = true;
    int iflag = 0;
    int ihsy = 112;
    int sidMode = 1;

    StringBuffer serr = new StringBuffer();
 
    iflag &= -8;
    iflag |= 2;
    iflag |= 65536;
    iflag |= 256;
  
      this.sd = new SweDate(tjd, gregflag);

      this.sw.swe_set_ephe_path(EPHEMERIS_PATH);
      
      //TODO Decide whether this is needed
      this.sw.swe_set_topo(this.top_long, this.top_lat, top_elev);
      this.sw.swe_set_sid_mode(sidMode, 0.0D, 0.0D);

      if (this.jyear * 10000 + this.jmon * 100 + this.jday < 15821015)
        gregflag = false;
      else
        gregflag = true;
      jut = 0.0D;
      this.sd.setDate(this.jyear, this.jmon, this.jday, jut);
      this.sd.setCalendarType(gregflag, true);
      tjd = this.sd.getJulDay();
      tjd += this.thour / 24.0D;
      this.sd.setJulDay(tjd);

      double delt = SweDate.getDeltaT(tjd);
      double te = tjd + delt;

      this.sw.swe_calc(te,  this.PLANETS[0], iflag, x, serr);
      this.sunLong = x[0];
      PlanetInfo planet = new PlanetInfo();
      planet.index = (this.planets[1].index = 0);
      planet.longitude = (this.sunLong - (int)Math.floor(this.sunLong / 30.0D) * 30);
      this.planets[1].longitude = this.sunLong;
      planet.isRetrograde = (this.planets[1].isRetrograde = false);
      this.rasis[((int)Math.floor(this.sunLong / 30.0D))].planets.add(planet);
      debugPrint("Sun: " + getDM(this.sunLong));

      this.sw.swe_calc(te, this.PLANETS[1], iflag, x, serr);
      this.moonLong = x[0];
      planet = new PlanetInfo();
      planet.index = (this.planets[2].index = 1);
      planet.longitude = (this.moonLong - (int)Math.floor(this.moonLong / 30.0D) * 30);  
      this.planets[2].longitude = this.moonLong;
      planet.isRetrograde = (this.planets[2].isRetrograde = false);
      this.rasis[((int)Math.floor(this.moonLong / 30.0D))].planets.add(planet);
      debugPrint("Moon: " + getDM(this.moonLong));

      for (int ipl = 2; ipl < this.PLANETS.length - 1; ipl++) {
        this.sw.swe_calc(te, this.PLANETS[ipl], iflag, x, serr);
        planet = new PlanetInfo();
        planet.index = (this.planets[(ipl + 1)].index = ipl);
        planet.longitude = (x[0] - (int)Math.floor(x[0] / 30.0D) * 30);
        this.planets[(ipl + 1)].longitude = x[0];
        planet.isRetrograde = planets[ipl + 1].isRetrograde = x[3] < 0.0D;
        this.rasis[((int)Math.floor(x[0] / 30.0D))].planets.add(planet);
        debugPrint(this.sw.swe_get_planet_name(this.PLANETS[ipl]) + ": " + getDM(x[0]) + (planet.isRetrograde ? " R" : ""));
      }

      this.sw.swe_calc(te, this.PLANETS[(this.PLANETS.length - 1)], iflag, x, serr);
      planet = new PlanetInfo();
      planet.index = (this.planets[(this.planets.length - 2)].index = this.PLANETS.length - 1);
      planet.longitude = (x[0] - (int)Math.floor(x[0] / 30.0D) * 30);
      this.planets[(this.planets.length - 2)].longitude = x[0];
      planet.isRetrograde = (this.planets[(this.planets.length - 2)].isRetrograde = false);
      this.rasis[((int)Math.floor(x[0] / 30.0D))].planets.add(planet);
      debugPrint("Rahu: " + getDM(x[0]));

      ketuLong = x[0] + 180.0D >= 360.0D ? x[0] + 180.0D - 360.0D : x[0] + 180.0D;
      planet = new PlanetInfo();
      planet.index = (this.planets[(this.planets.length - 1)].index = this.PLANETS.length);
      planet.longitude = (ketuLong - (int)Math.floor(ketuLong / 30.0D) * 30);
      this.planets[(this.planets.length - 1)].longitude = ketuLong;
      planet.isRetrograde = (this.planets[(this.planets.length - 1)].isRetrograde = false);
      this.rasis[((int)Math.floor(ketuLong / 30.0D))].planets.add(planet);
      debugPrint("Ketu: " + getDM(ketuLong));

       
      this.sw.swe_houses(tjd, iflag, this.top_lat, this.top_long, ihsy, cusp, ascmc);
      this.lagnaRasi = this.rasis[((int)Math.floor(ascmc[0] / 30.0D))];
      
      planet = new PlanetInfo();
      planet.index = (this.planets[0].index = -1);
      planet.longitude = (ascmc[0] - (int)Math.floor(ascmc[0] / 30.0D) * 30);
      this.planets[0].longitude = ascmc[0];
      planet.isRetrograde = (this.planets[0].isRetrograde = false);
      this.lagnaRasi.planets.add(planet);
      debugPrint("Ascendant: " + getDM(ascmc[0]));
       
      
      calculatePanchanga();
      
      calculateNavamsa();
      
      mapToModel(rasis, navamsaRasis,  birthChart); 
      
  
      
//      System.out.println(Arrays.toString( this.rasis));

//      System.out.println(Arrays.toString( this.navamsaRasis ));
      
//      System.out.println("Birth Chart: " + birthChart);
      
      calculateDasas();
      
      birthChart.setDasaDetails(this.dasaDetails);
         
      if(local)
    	  return new PdfGenerator().generateLocalPdf(birthChart, filePath, imageData);
      else
    	  return new PdfGenerator().generatePdf(birthChart, "test.pdf");
      
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
	        navamsaPlanet.index = (this.navamsaPlanetInfos[(planetInfo.index + 1)].index = planetInfo.index);
	        navamsaPlanet.longitude = ((planetInfo.longitude - division * divLength) / divLength * 30.0D);
	        navamsaPlanet.isRetrograde = (this.navamsaPlanetInfos[(planetInfo.index + 1)].isRetrograde = planetInfo.isRetrograde);
	        this.navamsaPlanetInfos[(planetInfo.index + 1)].longitude = (index * 30 + navamsaPlanet.longitude);

	        this.navamsaRasis[index].planets.add(navamsaPlanet);
	      }
	    }
	  
	  
  }
     
  private void mapToModel(TempRasi[] rasis, TempRasi[] navamsaRasis,   BirthChart birthChart) {
	  
	  boolean isLagna = false;
	  
	  Chakra raasiChakra = new Chakra();
	  
	  raasiChakra.setChakraName("Raasi") ;
	  
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
	  dasaTime.setTimeInMillis(dasaTime.getTimeInMillis() + DASA_LENGTHS_IN_MILLIS[nakshatraLord]); 
	  
	  int tmp181_180 = 0; 
	  
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

	 antarDasaLengthInMillis = (long) (this.DASA_LENGTHS[dasaLord] / 120.0D * this.DASA_LENGTHS_IN_MILLIS[dasaLord]);
	 dasaDetails[dasaIndex][(antarDasaIndex + 1)] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[dasaLord] + " " + HoraStrings.ANTAR_DASA);
	 antarDasaTime.setTimeInMillis(antarDasaTime.getTimeInMillis() + antarDasaLengthInMillis);
	 calculatePratyantarDasas(dasaLord, antarDasaLengthInMillis, pratyantarDasaTime, dasaIndex, antarDasaIndex);

	 for (i = dasaLord + 1; i < HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES.length; i++) {
		 antarDasaIndex++;
		 antarDasaLengthInMillis = (long)(this.DASA_LENGTHS[i] / 120.0D * this.DASA_LENGTHS_IN_MILLIS[dasaLord]);
		 pratyantarDasaTime = (Calendar)antarDasaTime.clone();
		 dasaDetails[dasaIndex][(antarDasaIndex + 1)] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + HoraStrings.ANTAR_DASA);
		 antarDasaTime.setTimeInMillis(antarDasaTime.getTimeInMillis() + antarDasaLengthInMillis);
		 calculatePratyantarDasas(i, antarDasaLengthInMillis, pratyantarDasaTime, dasaIndex, antarDasaIndex);
	 }

	 for (i = 0; i < dasaLord; i++) {
		 antarDasaIndex++;
		 antarDasaLengthInMillis = (long) (this.DASA_LENGTHS[i] / 120.0D * this.DASA_LENGTHS_IN_MILLIS[dasaLord]);
		 pratyantarDasaTime = (Calendar)antarDasaTime.clone();
		 dasaDetails[dasaIndex][(antarDasaIndex + 1)] = (HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[i] + " " + HoraStrings.ANTAR_DASA);
		 antarDasaTime.setTimeInMillis(antarDasaTime.getTimeInMillis() + antarDasaLengthInMillis);
		 calculatePratyantarDasas(i, antarDasaLengthInMillis, pratyantarDasaTime, dasaIndex, antarDasaIndex);
	 }
	 
}
 
  private void calculatePratyantarDasas(int antarDasaLord, long antarDasaLengthInMillis, Calendar pratyantarDasaTime, int dasaIndex, int antarDasaIndex) {
	  long pratyantarDasaLengthInMillis = 0L;

	  pratyantarDasaLengthInMillis =(long) (this.DASA_LENGTHS[antarDasaLord] / 120.0D * antarDasaLengthInMillis); 
	  
	  int tmp31_30 = (antarDasaIndex + 1); 
	  String[] tmp31_26 = dasaDetails[dasaIndex];
	  tmp31_26[tmp31_30] = (tmp31_26[tmp31_30] + "\n" +HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES[antarDasaLord] + " " + getDDMMYY(pratyantarDasaTime));
	  
	  pratyantarDasaTime.setTimeInMillis(pratyantarDasaTime.getTimeInMillis() + pratyantarDasaLengthInMillis);

	  for (int i = antarDasaLord + 1; i < HoraStrings.NAKSHATRA_LORD_DISPLAY_NAMES.length; i++) {
		  pratyantarDasaLengthInMillis = (long) (this.DASA_LENGTHS[i] / 120.0D * antarDasaLengthInMillis); 
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
    if (IS_DEBUG_MODE)
      System.out.println(message);
  }

  private String getDM(double value) {
    String retVal = "";
    int deg = 0; int min = 0;

    deg = (int)Math.floor(value);
    min = (int)Math.floor(value * 60.0D - deg * 60);

    retVal = deg + "° " + min + "'";

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

   
  private void calculatePanchanga() {  
		 
		String[] weekDays = new DateFormatSymbols().getWeekdays();
	    double adjustedMoonLong = this.moonLong >= this.sunLong ? this.moonLong : this.moonLong + 360.0D;
	    
	    String dob = getDDMMYYYY(this.displayCalendarOfBirthInLocalTime) + 
	  	      "  " + (this.displayCalendarOfBirthInLocalTime.get(11) < 10 ? "0" : "") + this.displayCalendarOfBirthInLocalTime.get(11) + ":" + (this.displayCalendarOfBirthInLocalTime.get(12) < 10 ? "0" : "") 
	  	      		+ this.displayCalendarOfBirthInLocalTime.get(12) + ":" + (this.displayCalendarOfBirthInLocalTime.get(13) < 10 ? "0" : "") + this.displayCalendarOfBirthInLocalTime.get(13) + 
		      " " + this.displayCalendarOfBirthInLocalTime.getTimeZone().getDisplayName(this.displayCalendarOfBirthInLocalTime.getTimeZone().inDaylightTime(new Date()), 0);

	    birthChart.setBirthDetails(dob);
	    birthChart.setNativeName(this.name);
	    birthChart.setPlaceOfBirth(this.place +  " (Long: " + getDM(this.top_long) + " Lat: " + getDM(this.top_lat) + ")");
  	    birthChart.setDay(weekDays[this.displayCalendarOfBirthInLocalTime.get(7)]);
  
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

  public static boolean containsTimeZone(String longName) {
    return timeZoneNames.contains(longName);
  }

  public static void addTimeZone(String longName, TimeZone timeZone) {
    timeZones.add(timeZone);
    timeZoneNames.add(longName);
  }

  public static TimeZone getTimeZone(int index) {
    return (TimeZone)timeZones.get(index);
  }
 
  
  private class TempRasi { 
	  
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
     
  }
 
  
}