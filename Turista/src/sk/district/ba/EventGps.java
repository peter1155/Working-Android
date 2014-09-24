package sk.district.ba;

import sk.turist.turist.*;
import android.util.Log;
import android.widget.ImageView;

import com.example.turist.R;
/**
 *  EventGps is class which sets up gps event screen for Bratislava region
 *  
 */
public class EventGps extends EventsGps {
	private static String tag = "ba.EventGps";
	
	
	/** Sets up Bratislava region map as image in imageView
	 * @see sk.turist.turist.EventsGps#setDefaultImg()
	 */
	@Override 
	public void setDefaultImg() {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining setDefaultImg");
		ImageView imageView = (ImageView) findViewById(R.id.gps_event_img);
        imageView.setImageResource(getResources().getIdentifier("ba", 
        		"drawable", this.getPackageName()));
        if(Log.isLoggable(tag, Log.INFO))
        	Log.i(tag, "End setDefaultImg");
	}
	
	// Now I working hard on my project doing some misstakes
}
