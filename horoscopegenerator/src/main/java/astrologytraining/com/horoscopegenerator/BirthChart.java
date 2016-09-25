package astrologytraining.com.horoscopegenerator;

public class BirthChart {

	private Chakra raasiChakra;
	
	private Chakra navamsaChakra;
	
	private String nativeName;
	
	private String birthDetails;
	
	private String placeOfBirth;
	
	private String day;
	
	private String tithi;
	
	private String nakshatra;
	
	private String balanceNakshtra;
	
	private String pada;
	 
	private String[][] dasaDetails;
	
	private boolean signsInEnglish;
	
	 
	public BirthChart(boolean signsInEnglish) {
  		this.signsInEnglish = signsInEnglish;
	}

	
	
	public boolean isSignsInEnglish() {
		return signsInEnglish;
	}



	public void setSignsInEnglish(boolean signsInEnglish) {
		this.signsInEnglish = signsInEnglish;
	}



	public String[][] getDasaDetails() {
		return dasaDetails;
	}

	public void setDasaDetails(String[][] dasaDetails) {
		this.dasaDetails = dasaDetails;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getTithi() {
		return tithi;
	}

	public void setTithi(String tithi) {
		this.tithi = tithi;
	}

	public String getNakshatra() {
		return nakshatra;
	}

	public void setNakshatra(String nakshatra) {
		this.nakshatra = nakshatra;
	}

	public String getBalanceNakshtra() {
		return balanceNakshtra;
	}

	public void setBalanceNakshtra(String balanceNakshtra) {
		this.balanceNakshtra = balanceNakshtra;
	}

	public String getPada() {
		return pada;
	}

	public void setPada(String pada) {
		this.pada = pada;
	}

	public Chakra getRaasiChakra() {
		return raasiChakra;
	}

	public void setRaasiChakra(Chakra raasiChakra) {
		this.raasiChakra = raasiChakra;
	}

	public Chakra getNavamsaChakra() {
		return navamsaChakra;
	}

	public void setNavamsaChakra(Chakra navamsaChakra) {
		this.navamsaChakra = navamsaChakra;
	}

	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	public String getBirthDetails() {
		return birthDetails;
	}

	public void setBirthDetails(String birthDetails) {
		this.birthDetails = birthDetails;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	@Override
	public String toString() {
		return "BirthChart [raasiChakra=" + raasiChakra + ", navamsaChakra="
				+ navamsaChakra + ", nativeName=" + nativeName
				+ ", birthDetails=" + birthDetails + ", placeOfBirth="
				+ placeOfBirth + ", day=" + day + ", tithi=" + tithi
				+ ", nakshatra=" + nakshatra + ", balanceNakshtra="
				+ balanceNakshtra + ", pada=" + pada + "]";
	}
 	
}
