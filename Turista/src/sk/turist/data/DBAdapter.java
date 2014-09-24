package sk.turist.data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DBAdapter is database layer class represents interface between SQLiteDBS and
 * application logic.
 * A DBAdapter object encapsulates: 
 * <ul>
 * <li>SQLiteDatabase db
 * <li>DatabaseHelper DBHelper
 * <li>Context my_context
 * </ul>
 * @author      Peter Vrana
 */

public class DBAdapter {
	
	private static String DB_PATH = "/data/data/com.example.turist/databases/";
	
	static final String KEY_ROWID = "id";
	static final String KEY_DATE = "date";
	static final String KEY_DESCRIPTION = "description";
	static final String KEY_NAME= "name";
	static final String KEY_REGION = "region";
	static final String KEY_COORDINATES = "coordinates";
	static final String KEY_PICTURE = "Pict_path";
	static final String KEY_HEIGHT = "height";
	static final String KEY_REGION_ID = "region_id";
	static final String TAG = "DBAdapter";
	static final String DATABASE_NAME = "app_turist";
	static final String DATABASE_TABLE = "places";
	static final int DATABASE_VERSION = 1;
	static final String DATABASE_CREATE =
	"create table places (id integer primary key autoincrement, "
	+ "description text," +
	"name text not null,region text not null,coordinates text not null," +
	"height integer not null,date text,Pict_path text,region_id SMALLINT);" +
	"CREATE TABLE regions (name VARCHAR(16),id SMALLINT);";
	
	final Context my_context;
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	
	public DBAdapter(Context ctx)
	{
		this.my_context = ctx;
		DBHelper = new DatabaseHelper(my_context);
	}
	
	/**
	 * DatabaseHelper private inner class extends SQLiteOpenHelper is used to create database
	 * if not exist in android device
	 * @see android.database.sqlite.SQLiteOpenHelper
	 * @author peter
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		//final Context My_context;
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			//this.My_context = context;
		}
		
		/**
		 * Method creates SQLiteDatabese in android device
		 * @see android.database.sqlite.SQLiteOpenHelper
		 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			if(Log.isLoggable(TAG, Log.INFO))
				Log.i(TAG,"Begininig onCreate");
			try {
			db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				if(Log.isLoggable(TAG, Log.ERROR))
					Log.e(TAG,e.getMessage());
			}
			if(Log.isLoggable(TAG, Log.INFO))
				Log.i(TAG,"End onCreate");
		}
		
		/**
		 * Method is not used
		 * @see android.database.sqlite.SQLiteOpenHelper
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{}
	}
	
	/**
	 * Method creates SQLiteDatabase in android device and copy reqired data
	 * from database saved in assets. Method calls:
	 * <ul> 
	 * <li> DBHelper.getReadableDatabase()
	 * <li> copyDataBase()
	 * </ul>
	 * @throws IOException
	 * @see  android.database.sqlite.SQLiteOpenHelper#getReadableDatabase()
	 */
	public void createDataBase() throws IOException{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG,"Begining createDataBase");
		boolean dbExist = checkDataBase();
    	if(dbExist){
    		//do nothing - database already exist
    		if(Log.isLoggable(TAG, Log.DEBUG))
    			Log.d(TAG,"DBS already exists");
    	}else{
    		if(Log.isLoggable(TAG, Log.DEBUG))
    			Log.d(TAG,"Creating new DBS");
    		//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	DBHelper.getReadableDatabase();
        	try {
    			copyDataBase();
    		} catch (IOException e) {
    			if(Log.isLoggable(TAG, Log.ERROR))
    				Log.e(TAG, e.getMessage());
        		throw new Error("Error copying database");
        	}
    	}
    	Log.i(TAG,"End createDataBase");
    }
	
	/**
	 * Open SQLiteDatabase.
	 * @return {@link DBAdapter}
	 * @throws SQLException
	 */
	
	public DBAdapter open() throws SQLException
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining open");
		db = DBHelper.getWritableDatabase();
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "End open");
		return this;
	}
	
	/**
	 * Close SQLiteDatabase.
	 * @throws SQLException
	 */
	
	public void close()
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining close");
		DBHelper.close();
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "End close");
	}
		
	/**
	 * Insert new record in SQLiteDBS - not used.
	 * @param date - Date of visitation of landmark.
	 * @param description - Description of landmark.
	 * @param name - Name of landmark
	 * @param region - Region where is landmark situated
	 * @param coordinates - GPS coordinates of landmark
	 * @param height - Altitude of landmark
	 * @return Id of new record.
	 * @see android.database.sqlite.SQLiteDatabase#insert(String, String, ContentValues)
	 */
	public long insertPlace(String date, String description, String name,
			String region, String coordinates, int height)
	{
		Log.i(TAG, "Begining insertPlace");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DESCRIPTION, description);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_REGION, region);
		initialValues.put(KEY_COORDINATES, coordinates);
		initialValues.put(KEY_HEIGHT, height);
		initialValues.put(KEY_DATE, date);
		Log.i(TAG, "Before return insertPlace");
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
		
	
	/**
	 * Delete record from SQLiteDBS with id = rowId - not used.
	 * @param rowId
	 * @see android.database.sqlite.SQLiteDatabase#delete(String, String, String[])
	 * @return If any record was deleted method returns true else return false.
	 */
	
	public boolean deletePlace(long rowId)
	{
		Log.i(TAG, "Begining deletePlace");
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
		

	//---retrieves all the contacts---
	
	/** 
	 * Method load all places from SQLiteDBS.
	 * @return
	 * A Cursor object, which is positioned before the first entry. 
	 * Note that Cursors are not synchronized, see the documentation for more details.
	 * @see android.database.sqlite.SQLiteDatabase#query(String, String[], String, String[], String, String, String, String)
	 */
	public Cursor getAllPlaces()
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining getAllPlaces");
		return db.query(DATABASE_TABLE, new String[] {KEY_ROWID,
		KEY_DESCRIPTION,KEY_NAME,KEY_REGION,KEY_COORDINATES,KEY_HEIGHT,KEY_DATE,KEY_PICTURE,KEY_REGION_ID}, 
		null, null, null, null, null);
	}
	
	/**
	 * Method load IDS of landmarks which are situated in Region 
	 * @param region_name - Name of region where are landmarks situated
	 * @return A Cursor object, which is positioned before the first entry. 
	 * Note that Cursors are not synchronized, see the documentation for more details.
	 */
	public Cursor getIDS(String region_name)
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining getIDS");
		Cursor cursor = DBHelper.getReadableDatabase().
				  rawQuery("select places.id from places" +
				  		" join regions on region_id = regions.id " +
				  		" where regions.name = ?", new String[] { region_name }); 
		//ArrayList<String> result = new ArrayList<String>();
		if(cursor == null)
			Log.w(TAG,"Cursor is null");
		cursor.moveToFirst();
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Before end getIDS");
		return cursor;
	}
	
	/**
	 * Method load landmark with id = rowid
	 * @param rowId - id of required landmark
	 * @return  A Cursor object, which is positioned before the first entry. 
	 * Note that Cursors are not synchronized, see the documentation for more details.
	 * @throws SQLException
	 */
    public Cursor getPlace(long rowId) throws SQLException
	{
    	if(Log.isLoggable(TAG, Log.INFO))
    		Log.i(TAG, "Begining getPlace");
		Cursor mCursor =
			db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
					KEY_DESCRIPTION,KEY_NAME,KEY_REGION,KEY_COORDINATES,KEY_HEIGHT,KEY_DATE,KEY_PICTURE,KEY_REGION_ID
					}, KEY_ROWID + "=" + rowId, null,null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			else
			{
				if(Log.isLoggable(TAG, Log.WARN))
					Log.w(TAG, "Cursor is null");
			}
			if(Log.isLoggable(TAG, Log.INFO))	
				Log.i(TAG, "Before return getPlace");
			return mCursor;
	}
	
	/**
	 * Update place with new information, mainly date of visitation and 
	 * absolute path to photo image from trip
	 * @param rowId - id of record in DBS
	 * @param date - date of visitation or null if not visited
	 * @param description - description of landmark
	 * @param name - name of landmark
	 * @param region - district where the landmark is situated
	 * @param coordinates - gps coordiates of landmark
	 * @param height - altitude of landmark
	 * @param path - absolute path to photo image from trip
	 * @param region_id - foreign key to table regions:
	 * <ul>
	 * <li> 1 - Žilinský
	 * <li> 2 - Košický
	 * <li> 3 - Prešovský
	 * <li> 4 - Bratislavský
	 * <li> 5 - Trnavský
	 * <li> 6 - Nitriansky
	 * <li> 7 - Trenèiansky
	 * <li> 8 - Bánsko Bystrický
	 * </ul>
	 * @return The number of rows affected
	 * @see android.database.sqlite.SQLiteDatabase#update(String, ContentValues, String, String[])
	 */
	public boolean updateContact(long rowId,String date,String description,
			String name,String region, String coordinates,String height,String path,int region_id)
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining updateContact");
		ContentValues args = new ContentValues();
		
		args.put(KEY_DESCRIPTION, description);
		args.put(KEY_NAME, name);
		args.put(KEY_REGION, region);
		args.put(KEY_COORDINATES, coordinates);
		args.put(KEY_HEIGHT, height);
		args.put(KEY_DATE, date);
		args.put(KEY_PICTURE,path);
		args.put(KEY_REGION_ID,region_id);
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Before end updateContact");
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}	
	
	/**
	 * Copy SQLiteDatabase from assets to android device.
	 * @throws IOException
	 */
	private void copyDataBase() throws IOException {
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining copyDataBase");
    	//Open your local db as the input stream
    	InputStream myInput;
		try
		{
			myInput = my_context.getAssets().open(DATABASE_NAME);
		} catch (IOException e)
		{
			if(Log.isLoggable(TAG, Log.ERROR))
				Log.e(TAG,e.getMessage());
			throw e;
		}
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DATABASE_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput;
		try
		{
			myOutput = new FileOutputStream(outFileName);
		} catch (FileNotFoundException e)
		{
			if(Log.isLoggable(TAG, Log.ERROR))
				Log.e(TAG, e.getMessage());
			throw e;
		}
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	try
		{
			while ((length = myInput.read(buffer))>0) {
				myOutput.write(buffer, 0, length);
			}
		} catch (IOException e)
		{
			if(Log.isLoggable(TAG, Log.ERROR))
				Log.e(TAG,e.getMessage());
			throw e;
		}
 
    	//Close the streams
    	try
		{
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (IOException e)
		{
			if(Log.isLoggable(TAG, Log.ERROR))
				Log.e(TAG,e.getMessage());
			throw e;
		}
    	if(Log.isLoggable(TAG, Log.INFO))
    		Log.i(TAG, "End copyDataBase");
	}	
	
	/**
	 * Check if database exist in android device.
	 * @return If database exists return true else false.
	 */
	
	private boolean checkDataBase()
	{
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Begining checkDataBase");
		SQLiteDatabase checkDB = null;
		try
		{
			String myPath = DB_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e)
		{
			if(Log.isLoggable(TAG, Log.ERROR))
				Log.e(TAG,e.getMessage());
		}

		if (checkDB != null)
		{
			if(Log.isLoggable(TAG, Log.DEBUG))
				Log.d(TAG, "CheckDB != null");
			checkDB.close();
		}
		else
		{
			if(Log.isLoggable(TAG, Log.DEBUG))
				Log.d(TAG, "CheckDB == null");
		}
		if(Log.isLoggable(TAG, Log.INFO))
			Log.i(TAG, "Before return checkDataBase");
		return checkDB != null ? true : false;
	}
}
