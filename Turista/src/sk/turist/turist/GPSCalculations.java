package sk.turist.turist;

import android.util.Log;

/**
 * Class is used for different gps callculations.
 * @author peter
 *
 */
public class GPSCalculations {
	public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
	public static String tag = "turist.GPSCalculations";
	public static Double Lat;
	public static Double Lan;
	
	/**
	 * Get distance between Landmark and user position.
	 * @param toParse - string Coordinates of landmark
	 * @param userLat - Latitude of user
	 * @param userLong - Langitude of user
	 * @return Double - distance
	 */
	
	public static double getDistance(String toParse,double userLat,double userLong) {
		//Log.i(tag,"Begining getDistance");
		parse(toParse);
		System.out.println("Latitude is:"+Lat+"\n"+"Langitude is:"+Lan+"\n");
		//Log.i(tag,"Before return getDistance");
		return calculateDistance(userLat,userLong,Lat,Lan);
	}
	
	/**
	 * Calculate distance between Landmark and user position.
	 * @param myLat - Lattitude of user
	 * @param myLng - Langitude of user
	 * @param placeLat - Lattitude of landmark
	 * @param placeLng - Langitude of landmark
	 * @return Double - distance 
	 */
	public static double calculateDistance(double myLat, double myLng,
			double placeLat, double placeLng)
	{
		//Log.i(tag,"Begining calculateDistance");
		double latDistance = Math.toRadians(myLat - placeLat);
		double lngDistance = Math.toRadians(myLng - placeLng);

		double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2))
				+ (Math.cos(Math.toRadians(myLat)))
				* (Math.cos(Math.toRadians(placeLat)))
				* (Math.sin(lngDistance / 2)) * (Math.sin(lngDistance / 2));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		int temp = (int) Math.round(AVERAGE_RADIUS_OF_EARTH * c * 1000);
		//Log.i(tag,"Before return calculateDistance");
		return ((double) temp / 1000);
	}
	
	/**
	 * Parse gps coordinates from String toParse which is loaded from Database.
	 * @param toParse - String to parse
	 */
	protected static void parse(String toParse) {
		//Log.d(tag,"Bagining toParse");
		int i=0;
		double minuty1=0,sekundy_des1=0,sekundy_cele1=0,stupne1=0;
		double minuty2=0,sekundy_des2=0,sekundy_cele2=0,stupne2=0;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			stupne1 = stupne1*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		i++;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			minuty1 = minuty1*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		while(toParse.charAt(i)<'0' || toParse.charAt(i)>'9') i++;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			sekundy_cele1 = sekundy_cele1*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		i++;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			sekundy_des1 = sekundy_des1*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		minuty1 = minuty1*60;
		sekundy_cele1 += sekundy_des1*0.001; 
		Lat = stupne1 + (minuty1+sekundy_cele1)/3600;
		while(toParse.charAt(i)<'0' || toParse.charAt(i)>'9') i++;
		
		//longitude
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			stupne2 = stupne2*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		i++;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			minuty2 = minuty2*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		while(toParse.charAt(i)<'0' || toParse.charAt(i)>'9') i++;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			sekundy_cele2 = sekundy_cele2*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		i++;
		while(toParse.charAt(i)>='0' && toParse.charAt(i)<='9')
		{
			sekundy_des2 = sekundy_des2*10+(int)toParse.charAt(i)-'0';
			i++;
		}
		minuty2 = minuty2*60;
		sekundy_cele2 += sekundy_des2*0.001; 
		Lan = stupne2 + (minuty2+sekundy_cele2)/3600;
		//Log.d(tag,"End toParse");
	}

	/**
	 * Get Lattitude of current processing string  
	 * @return Lattitude - Double
	 */
	
	public static Double getLat()
	{
		//Log.d(tag,"Begining getLat");
		return Lat;
	}

	/**
	 * Get Langittude of current processing string  
	 * @return Langittude - Double
	 */
	
	public static Double getLan()
	{
		//Log.d(tag,"Begining getLan");
		return Lan;
	}
}
