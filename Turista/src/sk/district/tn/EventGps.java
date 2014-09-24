package sk.district.tn;

import sk.turist.turist.*;
import android.util.Log;
import android.widget.ImageView;

import com.example.turist.R;
/**
 *  EventGps is class which sets up gps event screen for Trnava region
 *  
 */
public class EventGps extends EventsGps {
	private static String tag = "tn.EventGps";
	
	
	/** Sets up Trnava region map as image in imageView
	 * @see sk.turist.turist.EventsGps#setDefaultImg()
	 */
	@Override 
	public void setDefaultImg() {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining setDefaultImg");
		ImageView imageView = (ImageView) findViewById(R.id.gps_event_img);
        imageView.setImageResource(getResources().getIdentifier("tn", 
        		"drawable", this.getPackageName()));
        if(Log.isLoggable(tag, Log.INFO))
        	Log.i(tag, "End setDefaultImg");
	}
}
