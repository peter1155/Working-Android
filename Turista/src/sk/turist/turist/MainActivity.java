package sk.turist.turist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.turist.R;

import sk.turist.data.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;



/**
 * Class represents initial activity of android application. Allow user to choose
 * region on animated map and find landMark of specific region or to choose googleMap.
 * If user click on specific region on map acording to pixel color of touched region 
 * will be choosen activity to start.
 * @author peter
 *
 */
public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	
	private static String tag = "turist.Main";
	private final static Integer green = Color.rgb(82,206,111);
	private final static Integer red = Color.rgb(206,82,85);
	private final static Integer orange = Color.rgb(255,129,53);
	private final static Integer yellow = Color.rgb(240,199,8);
	private final static Integer blue = Color.rgb(82,181,206);
	private final static Integer white = Color.rgb(255,255,255);
	private final static Integer gray = Color.rgb(75,72,72);
	private final static Integer gray2 = Color.rgb(214,214,214);
	private ListView maListViewPerso;
	public static boolean check[] = new boolean[800];
	public static boolean has_image[] = new boolean[800];
	Activity activity = this;
	
	/**
	 * In onCreate view is set up and is checked if DBS exist if so far dbs wasn't created 
	 * DBS is copied from assets folder. Also listView is set up in this method.
	 * @see android.app.Activity#onCreate
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "Beginning onCreate");
		setContentView(R.layout.main);
		int i;

		for (i = 0; i < 800; i++)
		{
			check[i] = false;
			has_image[i] = false;
		}

		DBAdapter db = new DBAdapter(this);
		try
		{
			db.createDataBase();
		} catch (IOException e)
		{
			if(Log.isLoggable(tag, Log.ERROR))
				Log.e(tag,e.getMessage());
		}
		db.close();
		// ---get all places---
		db.open();
		Cursor c = db.getAllPlaces();
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"All places sucessfully obtained form DBS");
		if (c.moveToFirst())
		{
			do
			{
				if (c.getString(6) != null)
					check[Integer.valueOf(c.getShort(0))] = true;
				if (c.getString(7) != null)
					has_image[Integer.valueOf(c.getShort(0))] = true;
			} while (c.moveToNext());
		}
		db.close();

		WindowManager wm = getWindowManager();
		Display d = wm.getDefaultDisplay();

		maListViewPerso = (ListView) findViewById(R.id.listviewperso);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Creating list view");
		List<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.map));
		if (d.getWidth() > d.getHeight())
			map.put("description", /*
									 * getResources().getString(R.string.
									 * map_description)
									 */"");
		else
			map.put("description",
					getResources().getString(R.string.map_description));
		map.put("img", String.valueOf(R.drawable.ic_action_locate));
		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.statistics));
		if (d.getWidth() > d.getHeight())
			map.put("description", /*
									 * getResources().getString(R.string.
									 * statistics_description)
									 */"");
		else
			map.put("description",
					getResources().getString(R.string.statistics_description));
		map.put("img", String.valueOf(R.drawable.ic_action_paste));
		listItem.add(map);

		SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
				listItem, R.layout.single_item_listview, new String[] { "img",
						"name", "description" }, new int[] { R.id.img,
						R.id.name, R.id.description });
		
		maListViewPerso.setAdapter(mSchedule);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Setting onItemClickListener for listView");
		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// @SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id)
			{
				if(Log.isLoggable(tag, Log.INFO))
					Log.i(tag,"Begining onItemClick");
				// HashMap<String, String> map = (HashMap<String, String>)
				// maListViewPerso.getItemAtPosition(position);
				Intent intent = null;
				switch (position)
				{
				case 0:
					intent = new Intent(getBaseContext(), Progress.class);
					break;
				case 1:
					intent = new Intent(getBaseContext(), StatisticsActivity.class);
					break;
				}
				if(Log.isLoggable(tag, Log.INFO))
					Log.i(tag,"End onItemClick");
				startActivity(intent);
			}
		});
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag, "End onCreate");
	}

	/**
	 * Specify behaviour when user touch specific area of animated map.
	 * @see android.app.Activity#onStart
	 */
	
	public void onStart()
	{
		super.onStart();
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining onStart");
		/*
		 * ImageView img = (ImageView) findViewById(R.id.mapa);
		 * img.setDrawingCacheEnabled(true); //Bitmap
		 * bitmap=Bitmap.createBitmap(img.getDrawingCache()); Bitmap bitmap =
		 * BitmapFactory.decodeResource(getResources(), R.drawable.mapa_front);
		 * final Bitmap hotspots = bitmap; img.setDrawingCacheEnabled(false);
		 */
		ImageView imgMapa = (ImageView) findViewById(R.id.mapa);
		imgMapa.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event)
			{
				if(Log.isLoggable(tag, Log.INFO))
					Log.i(tag,"Begining onTouch");
				Integer color;
				Integer pointer = -1;
				int action = event.getAction();
				color = getColour((int) event.getX(), (int) event.getY(), v);
				// color = hotspots.getPixel((int) event.getX(),(int)
				// event.getY());
				switch (action)
				{
				case MotionEvent.ACTION_DOWN:
					if (color.intValue() == green.intValue())
						pointer = 0;
					else if (color.intValue() == red.intValue())
						pointer = 2;
					else if (color.intValue() == orange.intValue())
						pointer = 4;
					else if (color.intValue() == yellow.intValue())
						pointer = 5;
					else if (color.intValue() == blue.intValue())
						pointer = 6;
					else if (color.intValue() == white.intValue())
						pointer = 3;
					else if (color.intValue() == gray.intValue())
						pointer = 1;
					else if (color.intValue() == gray2.intValue())
						pointer = 7;
					if (pointer != -1)
					{
						switch (pointer)
						{
						case 0: // Zilinsky kraj
							Intent intent_zil = new Intent(activity,
									sk.district.za.HoryVrchy.class);
							startActivity(intent_zil);
							break;
						case 1: // Bratislavsky kraj
							Intent intent_ba = new Intent(activity,
									sk.district.ba.HoryVrchy.class);
							startActivity(intent_ba);
							break;
						case 2: // Presovsky kraj
							Intent intent_pv = new Intent(activity,
									sk.district.pr.HoryVrchy.class);
							startActivity(intent_pv);
							break;
						case 3: // Nitriansky kraj
							Intent intent_nt = new Intent(activity,
									sk.district.nt.HoryVrchy.class);
							startActivity(intent_nt);
							break;
						case 4: // Trnavsky kraj
							Intent intent_ta = new Intent(activity,
									sk.district.tn.HoryVrchy.class);
							startActivity(intent_ta);
							break;
						case 5: // Kosicky kraj
							Intent intent_ko = new Intent(activity,
									sk.district.ko.HoryVrchy.class);
							startActivity(intent_ko);
							break;
						case 6: // Bbystricky kraj
							Intent intent_bb = new Intent(activity,
									sk.district.bb.HoryVrchy.class);
							startActivity(intent_bb);
							break;
						case 7: // Trenciansky kraj
							Intent intent_tr = new Intent(activity,
									sk.district.tr.HoryVrchy.class);
							startActivity(intent_tr);
							break;
						}
					}
				}
				if(Log.isLoggable(tag, Log.INFO))
					Log.i(tag,"End onTouch");
				return true;
			}
		});
	}

	/**
	 * Method returns color of specified pisel(x,y) on View v 
	 * @param x - int x coordinate of pixel
	 * @param y - int y coordinate of pixel
	 * @param v - View where to search for pixel color
	 * @return int Color
	 */
	private int getColour(int x, int y, View v)
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining getColour");
		ImageView img = (ImageView) findViewById(R.id.mapa);
		Drawable d = getResources().getDrawable(R.drawable.mapa_front);
		Bitmap b1 = ((BitmapDrawable) d).getBitmap();
		// scale loaded bitmap to same resolution as visible view
		Bitmap hotspots = Bitmap.createScaledBitmap(b1, img.getWidth(),
				img.getHeight(), false);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Before return getColour");
		return hotspots.getPixel(x, y);
	}

}