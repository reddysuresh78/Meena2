package astrologytraining.com.horoscopegenerator;

public class PlanetInfo {
	
	public int index = 0;
	
	private Planet planet;
	
	public double longitude;
	
	private Star planetStar;
	
	public boolean isRetrograde;

	private House house;
	
	private boolean hasDikbala;
	
	private int pada;

	private boolean inYkStar;
	
	private String udStatus;
	
	private String gunas;
	
	private Planet jeeva;
	
	private Planet sareera;
	
	private Planet kalamsaLord;
	
	
	
	
	public Planet getKalamsaLord() {
		return kalamsaLord;
	}




	public void setKalamsaLord(Planet kalamsaLord) {
		this.kalamsaLord = kalamsaLord;
	}




	public Planet getJeeva() {
		return jeeva;
	}




	public void setJeeva(Planet jeeva) {
		this.jeeva = jeeva;
	}




	public Planet getSareera() {
		return sareera;
	}




	public void setSareera(Planet sareera) {
		this.sareera = sareera;
	}




	public PlanetInfo() {
		 
	}
	  
	
	
	
 	public String getGunas() {
		return gunas;
	}




	public void setGunas(String gunas) {
		this.gunas = gunas;
	}




 



	public String getUdStatus() {
		return udStatus;
	}






	public void setUdStatus(String udStatus) {
		this.udStatus = udStatus;
	}






	public boolean isInYkStar() {
		return inYkStar;
	}




	public void setInYkStar(boolean inYkStar) {
		this.inYkStar = inYkStar;
	}




	public int getPada() {
		return pada;
	}



	public void setPada(int pada) {
		this.pada = pada;
	}



	public boolean isHasDikbala() {
		return hasDikbala;
	}



	public void setHasDikbala(boolean hasDikbala) {
		this.hasDikbala = hasDikbala;
	}



	public House getHouse() {
		return house;
	}
 
	public void setHouse(House house) {
		this.house = house;
	}
  
	public PlanetInfo(Planet planet, double longitude) {
		this.planet = planet;
		this.longitude = longitude;
	}
	  

	public int getIndex() {
		return index;
	} 
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isRetrograde() {
		return isRetrograde;
	}
 
	public void setRetrograde(boolean isRetrograde) {
		this.isRetrograde = isRetrograde;
	}
 
	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}
 

	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public Star getPlanetStar() {
		return planetStar;
	}

	public void setPlanetStar(Star planetStar) {
		this.planetStar = planetStar;
	}
   
	@Override
	public String toString() {
		return "PlanetInfo [index=" + index + ", planet=" + planet
				+ ", longitude=" + longitude + ", planetStar=" + planetStar
				+ ", isRetrograde=" + isRetrograde + "]";
	}
  	
}
