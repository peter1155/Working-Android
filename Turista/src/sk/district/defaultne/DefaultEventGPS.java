package sk.district.defaultne;

import sk.turist.turist.*;
import android.util.Log;
import android.widget.ImageView;

import com.example.turist.R;

/**
 *  EventGps is class which sets up gps event screen for All regions
 *  
 */
public class DefaultEventGPS extends EventsGps {
	private static String tag = "DafaultEventGPS";
	
	/** Sets up Slovakia map as image in imageView
	 * @see sk.turist.turist.EventsGps#setDefaultImg()
	 */
	public void setDefaultImg() {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining setDefaultImg");
		ImageView imageView = (ImageView) findViewById(R.id.gps_event_img);
        imageView.setImageResource(getResources().getIdentifier("mapa_front", 
        		"drawable", this.getPackageName()));
        if(Log.isLoggable(tag, Log.INFO))
        	Log.i(tag,"End setDefaultImg");
	}
}