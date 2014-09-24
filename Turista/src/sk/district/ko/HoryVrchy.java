package sk.district.ko;

import java.util.ArrayList;

import sk.turist.data.DBAdapter;
import sk.turist.turist.CustomGrid;
import sk.turist.turist.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turist.R;

/**
 * Class which creates gridView with buttons. Each button represents
 * a landmark, after click on button new activity is started. If button represents
 * landmark which was visited than button has transparent background. There is texture image
 * after visiting a lot of places texture can be visible better.
 * @author peter
 * @see android.app.Activity
 */
public class HoryVrchy extends Activity
{
	GridView grid;
	private final String TAG = "ko.HoryVrchy";
	int Number_of_items;
	DBAdapter adapter = new DBAdapter(this);
	String[] web = new String[300];
	private ArrayList<Integer> on_screen = new ArrayList<Integer>();

	/**
	 * Private inner class My_grid extends CustomGrid
	 * there is overrided set_up method.
	 * @author peter
	 * @see CustomGrid
	 */
	private class My_grid extends CustomGrid
	{
		public My_grid(Context c, String[] web)
		{
			super(c, web);
		}

		/** set_up() determines if button of background should be
		 * transparent or not, according to state of landmark represented
		 * by button (visited/not visited).
		 * @see CustomGrid
		 *
		 */
		public void set_up(View grid, int position)
		{
			if(Log.isLoggable(TAG, Log.INFO))
				Log.i(TAG,"Begining set up");
			if (position < Number_of_items)
				// if (MainActivity.check[on_screen[position]])
				if (MainActivity.check[on_screen.get(position)])
					grid.setBackgroundResource(R.drawable.button_style2);
			if(Log.isLoggable(TAG, Log.INFO))
				Log.i(TAG,"End set_up");
		}
	}

	/**Creating GridView with Buttons, setting up progress of progress bar.
	 * Reading some ids of Kosice region landmarks from DBS.
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"Begining onCreate");
		setContentView(R.layout.default_buttons);
		int i, counter = 0;
		adapter = new DBAdapter(this);
		Cursor cursor = adapter.getIDS("Košický");
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"IDS sucessfully loaded from DBS");
		if (cursor.moveToFirst())
		{
			do
			{
				on_screen.add(Integer.valueOf(cursor.getString(0)));
			} while (cursor.moveToNext());
			cursor.close();
			if(Log.isLoggable(TAG, Log.INFO))
				Log.i(TAG,"Cursor closed");
		}
		Number_of_items = on_screen.size();
		if(Log.isLoggable(TAG, Log.DEBUG))
			Log.d(TAG, String.valueOf("Number of buttons:" + Number_of_items));

		ProgressBar my_bar = (ProgressBar) findViewById(R.id.progressBar1);
		for (i = 0; i < Number_of_items; i++)
			// if (MainActivity.check[on_screen[i]])
			if (MainActivity.check[on_screen.get(i)])
			{
				counter++;
			}
		my_bar.setProgress(counter * 100 / Number_of_items);
		TextView my_textv = (TextView) findViewById(R.id.textView1);
		my_textv.setText(String.valueOf(counter * 100 / Number_of_items) + "%");
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll_in_hory);
		ll.setBackgroundResource(R.drawable.rohace);
		for (i = 0; i < Number_of_items; i++)
		{
			web[i] = String.valueOf(i);
		}
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"Creating grid adapter");
		My_grid adapter = new My_grid(HoryVrchy.this, web);
		grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/** Start Activity EventGps and send Landmark id (which represents
			 * clicked button) to this activity.
			 * 
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if(Log.isLoggable(TAG, Log.INFO))
					Log.i(TAG,"Begining onItemClick");
				Toast.makeText(getBaseContext(),
						"You Clicked at " + web[position], Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent(getBaseContext(), EventGps.class);
				intent.putExtra("id", on_screen.get(position));
				if(Log.isLoggable(TAG, Log.INFO))
					Log.i(TAG,"End onItemClick");
				startActivity(intent);
			}
		});
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"End onCreate");
	}

	/**Creating GridView with Buttons, setting up progress of progress bar.
	 * If visitation of new place was confirmed than background of this button 
	 * is set up transparent.
	 * @see android.app.Activity#onResume(android.os.Bundle)
	 */
	public void onResume()
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"Begining onResume");
		super.onResume();
		int i, counter = 0;
		ProgressBar my_bar = (ProgressBar) findViewById(R.id.progressBar1);
		for (i = 0; i < Number_of_items; i++)
			if (MainActivity.check[on_screen.get(i)])
			{
				counter++;
			}
		my_bar.setProgress(counter * 100 / Number_of_items);
		TextView my_textv = (TextView) findViewById(R.id.textView1);
		my_textv.setText(String.valueOf(counter * 100 / Number_of_items) + "%");
		My_grid adapter = new My_grid(HoryVrchy.this, web);
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"Creating grid adapter");
		grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/** Start Activity EventGps and send Landmark id (which represents
			 * clicked button) to this activity.
			 * 
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if(Log.isLoggable(TAG, Log.INFO))
					Log.i(TAG,"Begining onItemClick");
				Intent intent = new Intent(getBaseContext(), EventGps.class);
				intent.putExtra("id", on_screen.get(position));
				if(Log.isLoggable(TAG, Log.INFO))
					Log.i(TAG,"End onItemClick");
				startActivity(intent);
			}
		});
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"End onResume");
	}
}