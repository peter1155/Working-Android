package sk.turist.turist;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sk.turist.data.DBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turist.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * EventGps is the abstract base class for all classes which
 * allow confirmation of GPS position 
 * A EventGps object encapsulates the state information needed
 * for the confirmation of reached position This
 * state information includes:
 * <ul>
 * <li>The latitude of landmark
 * <li>The longitude of Landmark
 * <li>A distance from landmark
 * <li>Photo from trip if there is any
 * <li>Gps position of user
 * </ul>
 * <p>
 * Allow user to confirm visitation of a landmark after reaching
 * the gps position which is nearby of this landmark (eg. 200 meters) 
 * <p>
 * @author      Peter Vrana
 */

public abstract class EventsGps extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {
	private  double lat;
	private  double lng;
	private Double distance;
	private DBAdapter db = new DBAdapter(this);
	private Cursor c;
	private int id;
	private int RESULT_LOAD_IMAGE = 1;
	private TextView my_text2;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private final String tag  = "turist.EventGps";
	Bitmap b;

	/**Creating connnection on GooglePlayServices if 
	 * connection cannot be created the method ends with error message
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining of on create method");
		setContentView(R.layout.event_gps_za_hory);
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resp == ConnectionResult.SUCCESS){
			Log.i(tag, "Sucesfully connected on GooglePlayServices");
			locationclient = new LocationClient(this,this,this);
			locationclient.connect();
		}
		else{
			if(Log.isLoggable(tag, Log.WARN))
				Log.w(tag, "Connection on GooglePlayServices failed");
			Toast.makeText(this, getResources().getString(R.string.Error_connectionGPS) + resp, Toast.LENGTH_LONG).show();
			
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End of on create method");
	}
	
	/** setDefaultImg() abstract base method which is overrided 
	 * is used for setting up image resource on imageView
	 *
	 */
	public abstract void setDefaultImg();
	
	/** The locationClient is disconnected onDestroy()
	 *	@see android.app.Activity#onDestroy(android.os.Bundle)
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining of on destroy method");
		if(locationclient!=null)
			locationclient.disconnect();
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "End of on destroy method");
	}
	
	/**
	 * Obtain current location from locationClient and 
	 * call updateWithNewLocation. Also set interval how often 
	 * location should be updated.
	 */
	
	@Override
	public void onConnected(Bundle connectionHint) {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining of on connected method");
		if(locationclient!=null && locationclient.isConnected()){
			Location loc =locationclient.getLastLocation();
			if(Log.isLoggable(tag, Log.INFO))
				Log.i(tag, "last location sucessfuly saved in loc variable");
			updateWithNewLocation(loc,c);
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag, "last location sucessfully updated");
		}
		else
		{
			if(Log.isLoggable(tag, Log.WARN))
				Log.w(tag, "locationClient is null");
		}
		if (locationclient != null && locationclient.isConnected()) {
			locationrequest = LocationRequest.create();
			locationrequest.setInterval(10000);
			locationrequest
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationclient.requestLocationUpdates(locationrequest, this);
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End of onConnected method");
	}
	
	/**
	 * Not implemented
	 */
	@Override
	public void onDisconnected() {
	}
	
	/**
	 * Specify behaviour when connection on GooglePlayServices fails,
	 * logs error message and also make toast of this fail.
	
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(getBaseContext(), 
				getResources().getString(R.string.Error_connectionstatus), 
				Toast.LENGTH_LONG).show();
		if(Log.isLoggable(tag, Log.WARN))
			Log.w(tag, "Connection failed ");
	}
	
	/**
	 * Specify behaviour when location change, New location info
	 * are displayed.
	 */
	public void onLocationChanged(Location location) {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining of onLocationChanged method");
		if(location!=null){
			updateWithNewLocation(location,c);
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End of onLocationChanged method");
	}
	
	/**
	 * If new image was added it will be dispalayed on screen
	 * also if visitation of place was confirmed time stamp 
	 * will be displayed on screen
	 * @see android.app.Activity#onResume(android.os.Bundle)
	 */
	public void onResume()
	{
		super.onResume();
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining of onResume method");
		TextView my_text = (TextView)findViewById(R.id.Gps_za);
		my_text.setMovementMethod(new ScrollingMovementMethod());
		
		TextView header = (TextView)findViewById(R.id.textView_za_hory);
		header.setMovementMethod(new ScrollingMovementMethod());
		
		RatingBar my_bar = (RatingBar)findViewById(R.id.Gps_za_hory_rateing);
		my_text2 = (TextView)findViewById(R.id.textPozicia);
		my_text2.setMovementMethod(new ScrollingMovementMethod());
		
		setDefaultImg();
		
		id = getIntent().getIntExtra("id",0);
		if(Log.isLoggable(tag, Log.DEBUG))
			Log.d(tag,"Opening database");
		db.open();
		c = db.getPlace(Integer.valueOf(id));
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Place sucessfully loaded from database");
		DisplayPlace(c,my_text);
		RatePlace(c,my_bar);
		db.close();
		if(Log.isLoggable(tag, Log.DEBUG))
			Log.d(tag,"Database closed");
		
		header.setText(c.getString(2));
		
		// if there is path in dbs picture will be displayed
		if(MainActivity.has_image[id])
		{
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"Opening database for loading image");
			db.open();
			c = db.getPlace(Integer.valueOf(id));
			if(Log.isLoggable(tag, Log.INFO))
				Log.i(tag,"Place sucessfully loaded from DBS");
			db.close();
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"Database closed");
			b = retBitmap(c.getString(7));
			if(Log.isLoggable(tag, Log.INFO))
				Log.i(tag,"Bitmap created sucessfully");
			ImageView imageView = (ImageView) findViewById(R.id.gps_event_img);
            imageView.setImageBitmap(b);
		}
		
		// if visitation of place was confirmed show timeStamp
		if(c.getString(6)!=null)
		{
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"Setting up stamp");
			TextView my_text3 = (TextView) findViewById(R.id.stamp);
			my_text3.setBackgroundResource(R.drawable.stamp);
			my_text3.setText(c.getString(6));
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"Stamp has been set up");
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End of onResume method");
	}
	
	/**
	 * This method recycle texture bitmap to save some memory.
	 * @see android.app.Activity#onPause(android.os.Bundle)
	 */
	
	public void onPause()
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Beginning of onPause");
		super.onPause();
		if(b!=null)
			b.recycle();
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End of onPause");
	}
	
	/**
	 * This method determines what to do when some button was clicked on screen
	 * If id of button equals: 
	 * <ul>
	 * <li>  R.id.button_gps - show dialog
	 * <li>  R.id.btn_add - add new picture (start activity for result)
	 * <li>  R.id.btn_remove - remove picture
	 * </ul>
	 * @param v - View
	 */
	
	public void onClick(View v) {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining onClick");
		switch (v.getId()) {
		case R.id.button_gps :
			// show dialog
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag, "Showing dialog");
			showDialog(id);
			break;
		case R.id.btn_add :
			if(!MainActivity.check[Integer.valueOf(c.getString(0))])
			{
				if(Log.isLoggable(tag, Log.DEBUG))
					Log.d(tag, "Place has not been visited");
				Toast.makeText(getBaseContext(),
						getResources().getString(R.string.Error_neoverene_miesto)
						, Toast.LENGTH_LONG).show();
			}
			else
			{
				if(Log.isLoggable(tag, Log.DEBUG))
					Log.d(tag, "Starting image picker activity for result");
				Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
			break;
		case R.id.btn_remove:
			db.open();
			db.updateContact(Long.valueOf(c.getString(0)), c.getString(6),
					c.getString(1), c.getString(2), c.getString(3),
					c.getString(4), c.getString(5), null, c.getInt(8));
			db.close();
			MainActivity.has_image[Integer.valueOf(c.getString(0))] = false;
			Intent intent = getIntent();
			finish();
			startActivity(intent);
			break;
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End onClick");
	}
	
	/**
	 * Setting up dialog it's buttons
	 * If button ok is pressed and place has not been visited
	 * and distance from this place is less than 200 meters visitation of the place
	 * will be confirmed. If any of conditions is false than message will be displayed.
	 * If button cancel will be pressed nothing happens.
	 * @see android.app.Activity#onCreateDialog(int, Bundle)
	 */
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Starting onCreate dialog");
		String title = null;
		if (distance != null)
		{
			if (distance.longValue() <= 1)
			{
				if(!MainActivity.check[Integer.valueOf(c.getString(0))])
					title = getResources().getString(R.string.explored_place);
				else
					title = getResources().getString(R.string.place_already_visited);
			}
			else
				title = getResources().getString(R.string.place_canot_explored);
		} else
			title = getResources().getString(R.string.place_canot_explored2);
		return new AlertDialog.Builder(this)
				.setTitle(title)
				.setPositiveButton(getResources().getString(R.string.OKbtn),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								if(Log.isLoggable(tag, Log.INFO))
									Log.i(tag, "Starting onClick of dialog");
								if (distance != null)
								{
									if (distance <= 1000)
									{
										if(MainActivity.check[Integer.valueOf(c.getString(0))])
										{
											Toast.makeText(
													getBaseContext(),
													getResources()
															.getString(
																	R.string.place_already_visited),
												Toast.LENGTH_SHORT).show();
											return;
										}
										String date = updatePlace(c);
										Log.i(tag,
												"Update of place sucessfully done");
										MainActivity.check[Integer.valueOf(c
												.getString(0))] = true;
										TextView my_text3 = (TextView) findViewById(R.id.stamp);
										my_text3.setBackgroundResource(R.drawable.stamp);
										my_text3.setText(date);
										Intent data = new Intent();
										// ---set the data to pass back---
										data.setData(Uri.parse("Ok"));
										setResult(RESULT_OK, data);
									} else
									{
										if(Log.isLoggable(tag, Log.DEBUG))
											Log.d(tag, "Place visited before");
									}
									if(Log.isLoggable(tag, Log.INFO))
										Log.i(tag, "End onClick of dialog");
								}
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.Cancelbtn),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
							{
								if(Log.isLoggable(tag, Log.INFO))
									Log.i(tag,
										"Starting onClick of dialog negative button");
								if(Log.isLoggable(tag, Log.INFO))
									Log.i(tag,
										"End onClick of dialog negative button");
							}
						}).create();
	}
	
	
	/**
	 * Displays information about current location on screen. 
	 * @param location - Location 
	 * @param c - Cursor (information about location of landmark)
	 */
	public void updateWithNewLocation(Location location,Cursor c) {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Starting updateWithNewLocation");
		String latLongString = getResources().getString(R.string.PositionNotFinded);
		int temp;
		
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			
			latLongString = "\n "+getResources().getString(R.string.Latitude)+": " + lat
					+ "\n " +getResources().getString(R.string.Longitude)+": " + lng;	
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d("Geo_Location", "Latitude: " + lat + ", Longitude: " + lng);
			distance = GPSCalculations.getDistance(c.getString(4),lat,lng);
			
			try {
				 temp = ((Number)NumberFormat.getInstance().parse(c.getString(5))).intValue();
				 if(location.getAltitude() == 0)
				 {
					if(Log.isLoggable(tag, Log.DEBUG))
						Log.d(tag,"Altitude == 0");
					my_text2.setText(getResources().getString(R.string.CurrentPosition)+":\n "+
								latLongString +"\n\n" + getResources().getString(R.string.YouAre)
								+":\n "+ distance.toString()
								+" "+getResources().getString(R.string.YouAre_from_place) +": \n "+
								getResources().getString(R.string.gpsOffInfo));
				 }
				 else if(temp < 700)
				 {
					 if(Log.isLoggable(tag, Log.DEBUG))
					 Log.d(tag,"Altitude < 700");
					 my_text2.setText(getResources().getString(R.string.CurrentPosition)+":\n     " +
						latLongString + "\n\n"+getResources().getString(R.string.YouAre)
						+":\n " + distance.toString()
						+ " "+ getResources().getString(R.string.YouAre_from_place)+": \n" +
						" "+(int) location.getAltitude()*100/100 + " "
						+ getResources().getString(R.string.Altitude) + "\n\n" 
						+ getResources().getString(R.string.Altitude2)+ ": \n "
						+ c.getString(5));
				 }
				 else
				 {
					 if(Log.isLoggable(tag, Log.DEBUG))
						 Log.d(tag,"Altitude >= 700");
					 my_text2.setText(getResources().getString(R.string.CurrentPosition)+":\n     " +
								latLongString + "\n\n"+getResources().getString(R.string.YouAre)
								+":\n " + distance.toString()
								+ " "+ getResources().getString(R.string.YouAre_from_place)+": \n" +
								" "+(int) location.getAltitude()*100/100 + " "
								+ getResources().getString(R.string.Altitude) + "\n\n" 
								+ getResources().getString(R.string.Altitude2)+ ": \n" + " "
								+ c.getString(5)
								+ "\n\n"+getResources().getString(R.string.Altitude_difference)
								+": \n "
								+ (int) (temp - location.getAltitude() * 100 / 100)
								+ " "+getResources().getString(R.string.meter));
				}
					 
			} catch (ParseException e) {
				e.printStackTrace();
				Log.e(tag,e.getMessage());
			}
		}
		else
		{
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"Position off");
			my_text2.setText(getResources().getString(R.string.CurrentPosition)+ ":\n\n "+
					latLongString + 
					"\n\n "+getResources().getString(R.string.GPS_info)  );
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End updateWithNewLocation");
	}

	/**
	 * Displays informations about landmark on TextView. This information
	 * are saved in Cursor object.
	 * @param c - Cursor
	 * @param my_text - TextView
	 */
	private void DisplayPlace(Cursor c,TextView my_text)
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining DisplayPlace");
		my_text.setText(
				getResources().getString(R.string.region)+":\n");
		my_text.setText(
				getResources().getString(R.string.region)+":\n"+c.getString(3)+"\n\n" 
				+getResources().getString(R.string.description)+": "+c.getString(1)+"\n\n"
				+getResources().getString(R.string.Altitude_base)+": "+c.getString(5));
		if(c.getString(6)!=null)
		{
			TextView my_text3 = (TextView) findViewById(R.id.stamp);
			my_text3.setBackgroundResource(R.drawable.stamp);
			my_text3.setText(c.getString(6));
		}
		else
		{
			if(Log.isLoggable(tag, Log.WARN))
				Log.w(tag,"Cursor is null");
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "End DisplayPlace");
	}
	
	/**
	 * Rate place according to it's altitude.
	 * @param c - Cursor
	 * @param my_bar - RatingBar
	 */
	private void RatePlace(Cursor c, RatingBar my_bar)
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining of RatePlace");
		try {
			int temp = ((Number)NumberFormat.getInstance().parse(c.getString(5))).intValue();
			my_bar.setRating((float)temp/700);
		} catch (ParseException e) {
			e.printStackTrace();
			if(Log.isLoggable(tag, Log.ERROR))
				Log.e(tag,e.getMessage());
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End of RatePlace");
	}
	
	/**
	 * <p>
	 *  If resultCode = RESULT_OK than the record in database will be updated 
	 *  with information about absolute path to the image which was chooseb by user 
	 *  </p>
	 *  <p>
	 *  If resultCode is different nothing happens.
	 *  </p>
	 *  @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     *	@param resultCode The integer result code returned by the child activity through its setResult().
	 *	@param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	 *
	 * @see  android.app.Activity#onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"Begining onActivityResult");
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            b = retBitmap(picturePath);
            ImageView imageView = (ImageView) findViewById(R.id.gps_event_img);
            imageView.setImageBitmap(b);
            db.open();
            db.updateContact(Long.valueOf(c.getString(0)), c.getString(6), c.getString(1), 
				c.getString(2), c.getString(3), c.getString(4), c.getString(5),
				picturePath,c.getInt(8));
            db.close();
			MainActivity.has_image[Integer.valueOf(c.getString(0))]=true;
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"Place has been updated in DBS");
        }
        else
        {
        	if(Log.isLoggable(tag, Log.WARN))
        		Log.w(tag,"Image has not been choosen");
        }
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"End onActivityResult");

    }
	
	/**
	 * Method crates texture bitmap. If image size is bigger than 1.2 MP 
	 * scaledBitmap of picture will be created.
	 * @param picturePath - absolute path to image which bitmap we want to create
	 * @return
	 */
	private Bitmap retBitmap(String picturePath)
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Begining retBitmap");
		final int IMAGE_MAX_SIZE = 1200000; // 1.2MP

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, o);
		int scale = 1;
		while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE)
		{
			scale++;
		}
		Bitmap b = null;
		if (scale > 1)
		{
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"In scale > 1 block");
			scale--;
			o = new BitmapFactory.Options();
			o.inSampleSize = scale;
			b = BitmapFactory.decodeFile(picturePath, o);
			int height = b.getHeight();
			int width = b.getWidth();
			double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
			double x = (y / height) * width;

			Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
					(int) y, true);
			b.recycle();
			b = scaledBitmap;
		} else
		{
			if(Log.isLoggable(tag, Log.DEBUG))
				Log.d(tag,"In scale == 1 block");
			b = BitmapFactory.decodeFile(picturePath);
		}
		Log.i(tag, "End retBitmap");
		return b;
	}
	
	protected String updatePlace(Cursor c)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(
				"dd.MM.yyyy");
		String date = sdf.format(new Date());
		db.open();
		db.updateContact(
				Long.valueOf(c.getString(0)),
				date, c.getString(1),
				c.getString(2), c.getString(3),
				c.getString(4), c.getString(5),
				c.getString(7), c.getInt(8));
		db.close();
		return date;
	}
}