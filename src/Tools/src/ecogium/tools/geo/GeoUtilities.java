package ecogium.tools.geo;

public class GeoUtilities {
	
	private static double _earthRadius = 6367445f;
	
	/**
	 * Permet de calculer la distance entre deux points g�ographiques exprim� en radian decimal
	 * @param long1
	 * @param lat1
	 * @param long2
	 * @param lat2
	 * @return
	 */
	public static double getLengthDeg(double long1, double lat1, double long2, double lat2){
		long1 = degree2radian(long1);
		long2 = degree2radian(long2);
		lat1 = degree2radian(lat1);
		lat2 = degree2radian(lat2);
		return getLengthRad(long1, lat1, long2, lat2);
	}
	
	/**
	 * Permet de calculer la distance entre deux points g�ographiques exprim� en radian decimal
	 * @param long1
	 * @param lat1
	 * @param long2
	 * @param lat2
	 * @return
	 */
	public static double getLengthRad(double long1, double lat1, double long2, double lat2){
		return _earthRadius * Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(long1 - long2));
	}
	
	public static double degree2radian(double degree){
		return degree * Math.PI / 180f;
	}
	
	public static double radian2degree(double radian){
		return radian * 180f / Math.PI;
	}
	
	public static double getLatRadFromLength(double length){
		return length/_earthRadius;
	}
	
	/**
	 * Permet de r�cup�rer l'arc en radian en longitude pour une longueur en m�tre // aux longitudes sur la surface de la Terre
	 * @param length Longueur // aux longitudes � la surface de la Terre
	 * @param latRad Latitude en radian consid�r�e
	 * @return L'arc en radian de la longitude
	 */
	public static double getLongRadFromLength(double length, double latRad){
		return length / (_earthRadius * Math.cos(latRad));
	}
	
	/**
	 * Permet de r�cup�rer l'arc longitudinal � partir d'une origine et d'un longitude
	 * @param longO Longitude de l'origine
	 * @param latO Latitude de l'origine
	 * @param longM Longitude de M
	 * @return L'arc longitudinal et son signe
	 */
	public static double getArcFromLongi(double longO, double latO, double longM){
		return (longM - longO) * _earthRadius * Math.cos(latO);
	}
	
	/**
	 * Permet de r�cup�rer l'arc de latitude � partir d'une origine et d'une latitude
	 * @param latO Latitude d'origine
	 * @param latM Latitude quelconque
	 * @return L'arc latitudinal et sens signe
	 */
	public static double getArcFromLati(double latO, double latM){
		return (latM - latO) * _earthRadius;
	}

	/**
	 * Permet de calculer le cap � partir de 2 points g�o. Cette m�thode a �t� v�rifi�e.
	 * @param LaA Latitude du point A en radian
	 * @param LonA Longitude du pont A en radian
	 * @param LaB Latitude du point B en radian
	 * @param LonB Longitude du point B en radian
	 * @return Le bearing en radian de A vers B. 0 correspond au nord g�o.
	 */
	public static double getBearing(double LaA, double LonA, double LaB, double LonB) {
		double P = LonB - LonA;

		double m = Math.acos(Math.sin(LaA) * Math.sin(LaB) * Math.cos(LonB - LonA) + Math.cos(LaA) * Math.cos(LaB));

		double brg = Math.acos(Math.sin(LaB) / (Math.sin(m) * Math.cos(LaA)) - Math.tan(LaA) / Math.tan(m));

		// Bearing = Acos((Sin(LaB) - Sin(LaA) * Cos(m)) / (Sin(m) * Cos(LaA)))

		if(P < 0){
			brg = 2 * Math.PI - brg;
		}
		
		return brg;
	}
	
}
