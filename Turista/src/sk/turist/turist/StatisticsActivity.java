package sk.turist.turist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sk.turist.data.DBAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.turist.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle.GridStyle;
import com.jjoe64.graphview.LineGraphView;

/**
 * Class create activity which display user activity on graph
 * @author peter
 *
 */
public class StatisticsActivity extends Activity
{
	private static String tag = "StatisticsActiivity";
	
	/**
	 * Graph is created on create data are loaded from DBS
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"Begining onCreate");
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllPlaces();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		ArrayList<Date> list = new ArrayList<Date>();
		if(c.moveToFirst())
		{
			Date record = null;
			do
			{
				if (c.getString(6) != null)
				{
					try
					{
						record = formatter.parse(c.getString(6));
					} catch (ParseException e)
					{
						Log.e(tag,e.getMessage());
					}
					list.add(record);
				}
			}while(c.moveToNext());
		}
		
		ArrayList<GraphViewData> graphData = new ArrayList<GraphView.GraphViewData>();
		Calendar now = Calendar.getInstance();   // This gets the current date and time.
		Integer year = now.get(Calendar.YEAR);     
		int activity[] = new int[12];
		for(int i=0;i<11;i++)
			activity[i]=0;
		for(int i=0;i<list.size();i++)
		{
			try
			{
				if(list.get(i).compareTo(formatter.parse("01.02."+year.toString()))<0)
				{
					activity[0]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.03."+year.toString()))<0)
				{
					activity[1]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.04."+year.toString()))<0)
				{
					activity[2]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.05."+year.toString()))<0)
				{
					activity[3]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.06."+year.toString()))<0)
				{
					activity[4]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.07."+year.toString()))<0)
				{
					activity[5]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.08."+year.toString()))<0)
				{
					activity[6]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.09."+year.toString()))<0)
				{
					activity[7]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.10."+year.toString()))<0)
				{
					activity[8]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.11."+year.toString()))<0)
				{
					activity[9]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.12."+year.toString()))<0)
				{
					activity[10]++;
				}
				else if(list.get(i).compareTo(formatter.parse("01.12."+year.toString()))>=0)
				{
					activity[11]++;
				}
			} catch (ParseException e)
			{
				Log.e(tag,e.getMessage());
			}
		}
		
		// init example series data
		setContentView(R.layout.statistics);
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
		    new GraphViewData(1, 20)
		    , new GraphViewData(2, activity[1])
		    , new GraphViewData(3, activity[2])
		    , new GraphViewData(4, activity[3])
		    , new GraphViewData(5, activity[4])
		    , new GraphViewData(6, activity[5])
		    , new GraphViewData(7, activity[6])
		    , new GraphViewData(8, activity[7])
		    , new GraphViewData(9, activity[8])
		    , new GraphViewData(10,activity[9])
		    , new GraphViewData(11,activity[10])
		    , new GraphViewData(12, activity[11])
		});
		
		for(int i=0;i<12;i++)
			Log.i(tag,"valu of mounth"+activity[i]);
		GraphView graphView = new LineGraphView(
		    this // context
		    , "Aktivita za posledný rok" // heading
		);
		
		graphView.setHorizontalLabels(new String[] {
				"1 ","2 ","3 ","4 ","5 ","6 ","7 ","8 ","9 ","10","11","12",});
		graphView.setVerticalLabels(new String[] {""});
		graphView.setManualYMaxBound(30);
		graphView.getGraphViewStyle().setGridStyle(GridStyle.HORIZONTAL);
		((LineGraphView) graphView).setDrawBackground(true);
		graphView.addSeries(exampleSeries); // data
		graphView.getGraphViewStyle().setNumHorizontalLabels(12);
		LinearLayout layout = (LinearLayout) findViewById(R.id.StatisticsLayout);
		layout.addView(graphView);
		if(Log.isLoggable(tag, Log.INFO))
			Log.i(tag,"End onCreate");
	}
	
	
	
}
