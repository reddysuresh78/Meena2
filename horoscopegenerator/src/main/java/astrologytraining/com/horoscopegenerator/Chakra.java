package astrologytraining.com.horoscopegenerator;

import java.util.ArrayList;
import java.util.List;

public class Chakra {

	private List<House> houses = new ArrayList<House>(12);
	
	private List<House> orderedHouses = new ArrayList<House>(12);
	
	private List<PlanetInfo> planetInfo = new ArrayList<PlanetInfo>(10);
	
	private String chakraName;
	
	private House lagna;
	 
	public List<House> getOrderedHouses() {
		return orderedHouses;
	}

	public void setOrderedHouses(List<House> orderedHouses) {
		this.orderedHouses = orderedHouses;
	}

	public List<PlanetInfo> getPlanetInfo() {
		return planetInfo;
	}

	public void setPlanetInfo(List<PlanetInfo> planetInfo) {
		this.planetInfo = planetInfo;
	}

	public House getLagna() {
		return lagna;
	}

	public void setLagna(House lagna) {
		this.lagna = lagna;
	}

	public List<House> getHouses() {
		return houses;
	}

	public void setHouses(List<House> houses) {
		this.houses = houses;
	}

	public String getChakraName() {
		return chakraName;
	}

	public void setChakraName(String chakraName) {
		this.chakraName = chakraName;
	}

	@Override
	public String toString() {
		return "Chakra [houses=" + houses + ", orderedHouses=" + orderedHouses
				+ ", planetInfo=" + planetInfo + ", chakraName=" + chakraName
				+ ", lagna=" + lagna + "]";
	}
	 
 
	
}
