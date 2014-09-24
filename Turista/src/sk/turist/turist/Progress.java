package sk.turist.turist;

import sk.district.defaultne.DefaultEventGPS;
import sk.turist.data.DBAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turist.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Class is used to create googleMap. Class extends FragmentActivity and implements ClusterManager
 * 
 * @author peter
 *
 */
public class Progress extends FragmentActivity implements ClusterManager.OnClusterClickListener<MyItem> {
	
	private SharedPreferences mPrefs;
	private static final LatLng SVK = new LatLng(48.6667, 19.5);
    private static String tag = "turist.Progress";
    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private double Lan,Lat;
    private  DBAdapter db;
    private CheckBox myBox1;
    private CheckBox myBox2;
    private int request_Code = 1;

    /**
     * Private inner class PlaceRender extends DefaultClusterRenderer.
     * Class is used to render markers as clusters.
     * @author peter
     *
     */
    
    private class PlaceRenderer extends DefaultClusterRenderer<MyItem> {
	    public PlaceRenderer() {
	        super(getApplicationContext(), mMap, mClusterManager);
	    }
	    
	    /**
	     * Method specify appearance and behaviour of markers, when they aren't rendered as Cluster.
	     */
	    
	    @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
	    	if(Log.isLoggable(tag, Log.INFO))
	    		Log.i(tag,"Begining onBeforeClusterItemRendered");
            BitmapDescriptor bitmapDescriptor = 
        			BitmapDescriptorFactory.defaultMarker(
        			BitmapDescriptorFactory.HUE_GREEN);
            markerOptions
	            .title(item.getName())
	            .snippet(String.valueOf(item.getId()));
            
            if(!item.isVisited())
            	markerOptions.alpha(0.3f);
             
            if(mMap.getMyLocation()!=null)
            {
	            if(GPSCalculations.calculateDistance(
	            		mMap.getMyLocation().getLatitude(),
	            		mMap.getMyLocation().getLongitude(), 
	            		item.getPosition().latitude, 
	            		item.getPosition().longitude)<10 && !item.isVisited())
	            	markerOptions.icon(bitmapDescriptor);
            }
        	if(Log.isLoggable(tag, Log.INFO))
        		Log.i(tag,"End onBeforeClusterItemRendered");
        }
	    
	    /**
	     * Method specify when should be set of markers rendered as cluster.
	     */
	    
        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
        	if(Log.isLoggable(tag, Log.DEBUG))
        		Log.d(tag,"Begining shouldRenderAsCluster");
            return cluster.getSize() > 1;
        }
    }
	    
    /**
     * Map is set up onCreate also progress bar value is set up.
     * @see setUpMapIfNeeded
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	if(Log.isLoggable(tag, Log.INFO))
        	Log.i(tag,"Begining onCreate");
        
        mPrefs = getSharedPreferences("progressPref", 0); // nacitam user preferencie so suboru progressPref
        
        setContentView(R.layout.progres);
        ProgressBar myBar = (ProgressBar) findViewById(R.id.Main_progressBar);
        TextView myText = (TextView) findViewById(R.id.main_text_progress);
        
        myBox1 = (CheckBox) findViewById(R.id.checkBox1);
        myBox2 = (CheckBox) findViewById(R.id.checkBox2);
        
      
        myBox1.setChecked(mPrefs.getBoolean("box1", true)); // nacitanie hodnoty checkboxu1   
        myBox2.setChecked(mPrefs.getBoolean("box2", true)); // nacitanie hodnoty checkboxu2
       

        myBox2.setOnCheckedChangeListener(new Listener_visited());
        myBox1.setOnCheckedChangeListener(new Listener_Nvisited());
        
		int i,counter=0;
        for(i=0;i<648;i++)
        	if(MainActivity.check[i])
        		counter++;
        
        myText.setText(String.valueOf(counter*100/648)+"%");
        myBar.setProgress(counter*100/648);
        setUpMapIfNeeded();
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"End onCreate");
    }

    /**
     * User preferences are saved
     */
    @Override
	protected void onPause()
	{
		super.onPause();
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putBoolean("box1", myBox1.isChecked());
		ed.putBoolean("box2", myBox2.isChecked());
		ed.commit();
	}
    
    /**
     * Map is set up on resume if needed.
     * @see setUpMapIfNeeded
     */
    @Override
    protected void onResume() {
        super.onResume();
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"Beginig onResume");
        setUpMapIfNeeded();
    }

    /**
     * If mMap object is equal null this method will set up Map view by calling
     * setUpMap.
     * @see com.example.vseobecne.setUpMap
     */
    
    private void setUpMapIfNeeded() {
    	if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Beginig setUpMapIfNeeded");
		if (mMap == null)
		{
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null)
			{
				setUpMap();
			}
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "End setUpMapIfNeeded");
	}

    /**
     * Method set up type of map, default map position and zoom, and
     * add all reqired markers on map.
     */
    private void setUpMap() {
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"Beginig setUpMap");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SVK, 5));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mClusterManager.setRenderer(new PlaceRenderer());
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(new myInfoClickListener());
        mMap.setMyLocationEnabled(true);
        mClusterManager.setOnClusterClickListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        db = new DBAdapter(this);
		addingMarkers(myBox2.isChecked(),myBox1.isChecked());
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End setUpMap");
    }
    
    
    /**
     * Method specify behaviour when user click on Cluster, number
     * of markers in cluster will be shown.
     */
    public boolean onClusterClick(Cluster<MyItem> cluster) {
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"Beginig onClusterClick");
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getName();
        Toast.makeText(this, cluster.getSize() + " (zahàòajúc " + firstName + ")", Toast.LENGTH_SHORT).show();
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"End onClusterClick");
        return true;
    }
   
    /**
     * Method add required markers on google map according to parameters vis,nvis and refresh view.
     * <p>
     * If vis is equal true then all visited landmark positions markers will be added.<br>
     * If nvis is equal true then all not visited landmark positions markers will be added.
     * </p>
     * @param vis - checkBox visited value
     * @param nvis - checkBox not visited value
     */
    private void addingMarkers(boolean vis,boolean nvis)
    {
    	if(Log.isLoggable(tag, Log.INFO))
    		Log.i(tag,"Beginig addingMarkers");
    	db.open();
		Cursor c = db.getAllPlaces();
		if (c.moveToFirst())
		{
			do {
				GPSCalculations.parse(c.getString(4));
				Lat = GPSCalculations.getLat();
				Lan = GPSCalculations.getLan();
				if(c.getString(6) != null && vis)
					mClusterManager.addItem(new MyItem(Lat,Lan,c.getString(2)
							,true,Integer.valueOf(c.getString(0))));
				else if(c.getString(6) == null && nvis)
					mClusterManager.addItem(new MyItem(Lat,Lan,c.getString(2)
						,false,Integer.valueOf(c.getString(0))));
			} while (c.moveToNext());
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End addingMarkers");
		db.close();
    }

    private class Listener_visited implements OnCheckedChangeListener{
		/**
		 * If combo box visited isChecked then method show all visited landmarks.
		 * If combo box visited !isChecked then method not hide visited landmarks.
		 */
    	@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
    		if(Log.isLoggable(tag, Log.INFO))
    			Log.i(tag,"Begining onCheckedChanged visited");
			mClusterManager.clearItems();
			if(!isChecked)
			{
				addingMarkers(false,myBox1.isChecked());
			}
			if(isChecked)
			{
				addingMarkers(true,myBox1.isChecked());
			}
			float temp = mMap.getCameraPosition().zoom;
			if(temp > mMap.getMinZoomLevel())
				mMap.animateCamera(CameraUpdateFactory.zoomOut());
			else
				mMap.animateCamera(CameraUpdateFactory.zoomIn());	
			if(Log.isLoggable(tag, Log.INFO))
				Log.i(tag,"End onCheckedChanged visited");
		}		
	}
    
    private class Listener_Nvisited implements OnCheckedChangeListener{
    	
    	/**
		 * If combo box nvisited isChecked then method show all visited landmarks.
		 * If combo box nvisited !isChecked then method not hide visited landmarks.
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(Log.isLoggable(tag, Log.INFO))
				Log.i(tag,"Begining onCheckedChanged !visited");
			mClusterManager.clearItems();
			if(!isChecked)
			{
					addingMarkers(myBox2.isChecked(),false);
			}
			if(isChecked)
			{
				addingMarkers(myBox2.isChecked(),true);		
			}
			float temp = mMap.getCameraPosition().zoom;
			if(temp > mMap.getMinZoomLevel())
				mMap.animateCamera(CameraUpdateFactory.zoomOut());
			else
				mMap.animateCamera(CameraUpdateFactory.zoomIn());
			if(Log.isLoggable(tag, Log.INFO))
				Log.i(tag,"End onCheckedChanged !visited");
		}		
	}
    /**
     * Specify behaviour after click on info vindow of marker - EventGps activity will start.
     * @author peter
     *
     */
    class myInfoClickListener implements OnInfoWindowClickListener {
    	@Override
    	public void onInfoWindowClick(Marker arg0) {
    		if(Log.isLoggable(tag, Log.INFO))
    			Log.i(tag,"Begining onInfoWindowClick");
    		int id = Integer.valueOf(arg0.getSnippet());
    		Intent intent = new Intent(getBaseContext(),DefaultEventGPS.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, request_Code);
        	if(Log.isLoggable(tag, Log.INFO))
        		Log.i(tag,"End onInfoWindowClick");
    	}
    }
    
    /**
     * Method update view if new place was visited
     */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining onActivityResult");
		if (requestCode == request_Code) {
			if (resultCode == RESULT_OK) {
				mClusterManager.clearItems();
				addingMarkers(myBox2.isChecked(),myBox1.isChecked());
				float temp = mMap.getCameraPosition().zoom;
				if(temp > mMap.getMinZoomLevel())
					mMap.animateCamera(CameraUpdateFactory.zoomOut());
				else
					mMap.animateCamera(CameraUpdateFactory.zoomIn());
				Toast.makeText(this, data.getData().toString(),
						Toast.LENGTH_SHORT).show();
			}
			else 
			{
				if(Log.isLoggable(tag, Log.INFO))
					Log.i(tag,"Place has not been confirmed");
			}
		}
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End onActivityResult");
	}
	
	/**
	 * Map state is saved in this method mainly zoomLevel,Latitude,Longitude.
	 * Also state of checkboxes is saved.
	 */
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  // Save UI state changes to the savedInstanceState.
		  // This bundle will be passed to onCreate if the process is
		  // killed and restarted.
		  if(Log.isLoggable(tag, Log.INFO))
			  Log.i(tag,"Begining onSaveInstanceState");
		  savedInstanceState.putBoolean("Box1",  myBox1.isChecked());
		  savedInstanceState.putBoolean("Box2",  myBox2.isChecked());
		  savedInstanceState.putFloat("ZoomLevel", mMap.getCameraPosition().zoom);
		  savedInstanceState.putDouble("Lat",  mMap.getCameraPosition().target.latitude);
		  savedInstanceState.putDouble("Lng",  mMap.getCameraPosition().target.longitude);
		  
		  if(Log.isLoggable(tag, Log.INFO))
			  Log.i(tag,"End onSaveInstanceState");
		}
	
	/**
	 * This method restore map state and state of checkboxes
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining onRestoreInstanceState");
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		myBox1.setChecked(savedInstanceState.getBoolean("Box1"));
		myBox2.setChecked(savedInstanceState.getBoolean("Box2"));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(savedInstanceState.getDouble("Lat"),
						savedInstanceState.getDouble("Lng")),
				savedInstanceState.getFloat("ZoomLevel")));
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End onRestoreInstanceState");
	}
}


