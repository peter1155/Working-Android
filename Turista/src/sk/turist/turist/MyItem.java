package sk.turist.turist;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * MyItem implements ClusterItem which represents Marker on googleMap.
 * A MyItem object encapsulates required information about landmark. These
 * information include
 * <ul>
 * <li> mPosition - LatLng position of the marker
 * <li> name - String name of landMark
 * <li> visited - boolean (if was visited true)
 * <li> id - row id in DBS
 * </ul>
 * @author peter
 *
 */
public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final String tag = "turist.MyItem";
    private final String name;
    private final boolean visited;
    private final int id;
    

    public MyItem(double lat, double lng, String Name,boolean visit,int Id) {
    	if(Log.isLoggable(tag, Log.DEBUG))
    		Log.d(tag,"Begining MyItem constructor");
        mPosition = new LatLng(lat, lng);
        name = Name;
        visited = visit;
        id = Id;
    	if(Log.isLoggable(tag, Log.DEBUG))
    		Log.d(tag,"End MyItem constructor");
    }
    /**
     * Method returns position of item
     * @return LatLng mPosition
     */
    
	@Override
    public LatLng getPosition() {
		if(Log.isLoggable(tag, Log.DEBUG))
			Log.d(tag,"Begining getPosition");
        return mPosition;
    }

	/**
	 * Return name of item.
	 * @return String name
	 */
	
	public String getName() {
		if(Log.isLoggable(tag, Log.DEBUG))
			Log.d(tag,"Begining getName");
		return name;
	}

	/**
	 * Return information about visitation of landmark.
	 * @return boolean visited
	 */
	public boolean isVisited() {
		if(Log.isLoggable(tag, Log.DEBUG))
			Log.d(tag,"Begining isVisited");
		return visited;
	}

	/**
	 * Return id of item in DBS
	 * @return int id
	 */
	public int getId() {
		if(Log.isLoggable(tag, Log.DEBUG))
			Log.d(tag,"Begining getId");
		return id;
	}
}
