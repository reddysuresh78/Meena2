package astrologytraining.com.horoscopegenerator;

public class CommonUtils {
	
	public static String toDMS(double d) {
		d += 0.5/3600./10000.;	// round to 1/1000 of a second
		int deg = (int) d;
		d = (d - deg) * 60;
		int min = (int) d;
		d = (d - min) * 60;
		double sec = d;

		return String.format("%3d° %02d' %07.4f\"", deg, min, sec);
	}

}
