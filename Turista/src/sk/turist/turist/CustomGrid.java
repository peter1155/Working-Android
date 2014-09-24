package sk.turist.turist;

import com.example.turist.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Base abstract class for crating gridView
 * @author peter
 *
 */
public abstract class CustomGrid extends BaseAdapter
{
	private Context mContext;
	private static String tag = "turist.CustomGrid";
	private String[] webs = new String[300];

	public CustomGrid(Context c, String[] webs)
	{
		mContext = c;
		this.webs = webs;
	}

	/**
	 * Get count of gridView items
	 * @return count of gridView items
	 */
	
	@Override
	public int getCount()
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining getCount");
		int i = 0;
		while (i < 300 && webs[i] != null)
			i++;
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Before end getCount");
		return i;
	}

	/**
	 * Not implemented
	 */
	@Override
	public Object getItem(int position)
	{
		return null;
	}

	/**
	 * Not implemented
	 */
	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	
	/**
	 * Set up base gridView
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining getView");
		View grid;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		grid = new View(mContext);
		grid = inflater.inflate(R.layout.grid_single, null);
		TextView textView = (TextView) grid.findViewById(R.id.grid_text);
		textView.setText(String.valueOf(webs[position]));
		set_up(grid, position);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Before return getView");
		return grid;
	}

	/**
	 * Abstract method is overrided in classes which extends CustomGrid
	 * set up some items on gridView
	 * @param grid - gridView to setUp
	 * @param position -  position of item
	 */
	public abstract void set_up(View grid, int position);
}