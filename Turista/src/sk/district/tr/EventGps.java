package sk.district.tr;

import sk.turist.turist.*;
import android.util.Log;
import android.widget.ImageView;

import com.example.turist.R;

public class EventGps extends EventsGps {
	
	public static String tag = "tr.EventGPS";
	public void setDefaultImg() {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining setDefaultImg");
		ImageView imageView = (ImageView) findViewById(R.id.gps_event_img);
        imageView.setImageResource(getResources().getIdentifier("tr", 
        		"drawable", this.getPackageName()));
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining setDefaultImg");
	}
}
