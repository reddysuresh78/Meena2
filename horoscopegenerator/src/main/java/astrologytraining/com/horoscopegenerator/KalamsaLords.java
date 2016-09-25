package astrologytraining.com.horoscopegenerator;

public class KalamsaLords {
	
	public static String[][] kalamsaRanges = { 
		 
		{
 			"00:00:00",
			"00:46:40",
			"03:00:00",
			"03:40:00",
			"04:46:40",
			"05:33:20",
			"07:33:20",
			"09:20:00",
			"11:26:40",
			"13:20:00",
			"15:33:20",
			"16:13:20",
			"17:20:00",
			"18:06:40",
			"20:06:40",
			"21:53:20",
			"24:00:00",
			"25:53:20",
			"26:40:00",
			"27:20:00",
			"28:26:40",
			"29:13:20"
 		} , 
		
		{
 			"00:00:00",
 			"01:13:20",
 			"03:00:00",
 			"05:06:40",
 			"07:00:00",
 			"07:46:40",
 			"10:00:00",
 			"11:06:40",
 			"11:53:20",
 			"13:53:20",
 			"15:40:00",
 			"17:46:40",
 			"19:40:00",
 			"20:26:40",
 			"22:40:00",
 			"23:20:00",
 			"24:06:40",
 			"26:06:40",
 			"27:53:20"
  			
		}, 
		
		{
 			"00:00:00",
			"01:53:20",
			"02:40:00",
			"04:53:20",
			"05:33:20",
			"06:40:00",
			"08:40:00",
			"10:26:40",
			"12:33:20",
			"14:26:40",
			"15:13:20",
			"17:26:40",
			"18:06:40",
			"19:13:20",
			"20:00:00",
			"21:46:40",
			"23:53:20",
			"25:46:40",
			"26:33:20",
			"28:46:40",
			"29:26:40"
  		}, 
		 
		{
  			"00:00:00",
  			"00:33:20",
  			"01:20:00",
  			"03:20:00",
  			"05:26:40",
  			"07:20:00",
  			"08:06:40",
  			"10:20:00",
  			"11:00:00",
  			"12:06:40",
  			"12:53:20",
  			"14:53:20",
  			"16:40:00",
  			"18:33:20",
  			"19:20:00",
  			"21:33:20",
  			"22:13:20",
  			"23:20:00",
  			"24:06:40",
  			"26:06:40",
  			"27:53:20",
		}
		 
		
	};
	
	
	
	public static Planet[][] kalamsaLords = {
		{
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON, 
			Planet.MARS,
			Planet.RAHU,
			Planet.JUPITER, 
			Planet.SATURN, 
			Planet.MERCURY,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON, 
			Planet.MARS,
			Planet.RAHU,
			Planet.JUPITER, 
			Planet.SATURN,
			Planet.MERCURY,
			Planet.KETU,
			Planet.SUN,
			Planet.MOON, 
			Planet.MARS,
			Planet.RAHU
		},
		{
			Planet.RAHU,			
			Planet.JUPITER, 
			Planet.SATURN ,
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.MOON ,
			Planet.MARS,
			Planet.RAHU ,
			Planet.JUPITER, 
			Planet.SATURN ,
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MARS,
			Planet.RAHU ,
			Planet.JUPITER, 
			Planet.SATURN 
 		},
		{
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON, 
			Planet.RAHU ,
			Planet.JUPITER, 
			Planet.SATURN ,
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON, 
			Planet.MARS,
			Planet.JUPITER, 
			Planet.SATURN ,
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON
 		},
 		{
			Planet.MOON,
			Planet.MARS,
			Planet.RAHU ,
			Planet.SATURN, 
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON, 
			Planet.MARS,
			Planet.RAHU,
			Planet.JUPITER, 
			Planet.MERCURY,
			Planet.KETU,
			Planet.VENUS,
			Planet.SUN,
			Planet.MOON, 
			Planet.MARS,
			Planet.RAHU, 
			Planet.JUPITER, 
			Planet.SATURN}
		
   };

	public static void main(String[] args) {
		
		System.out.println(kalamsaLords[3].length);
		System.out.println(kalamsaRanges[3].length);
	}
	
	

}
