package astrologytraining.com.horoscopegenerator;

import java.util.ArrayList;
import java.util.List;

public class House {
	
	private Raasi raasi;
	 
	private List<PlanetInfo> planets = new ArrayList<PlanetInfo>();
	
	private List<PlanetInfo> aspectedPlanets = new ArrayList<PlanetInfo>();

	private int assignedHouseNo;
	 
	public int getAssignedHouseNo() {
		return assignedHouseNo;
	}

	public void setAssignedHouseNo(int assignedHouseNo) {
		this.assignedHouseNo = assignedHouseNo;
	}

	public Raasi getRaasi() {
		return raasi;
	}

	public void setRaasi(Raasi raasi) {
		this.raasi = raasi;
	}

	public List<PlanetInfo> getPlanets() {
		return planets;
	}

	public void setPlanets(List<PlanetInfo> planets) {
		this.planets = planets;
	}
	 

	public List<PlanetInfo> getAspectedPlanets() {
		return aspectedPlanets;
	}

	public void setAspectedPlanets(List<PlanetInfo> aspectedPlanets) {
		this.aspectedPlanets = aspectedPlanets;
	}

	@Override
	public String toString() {
		return "House [raasi=" + raasi + ", planets=" + planets
				+ ", aspectedPlanets=" + aspectedPlanets + ", assignedHouseNo="
				+ assignedHouseNo + "]";
	}

 
	

 
}
