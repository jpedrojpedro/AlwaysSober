package br.puc.rio.lac.course.android.model;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDataBaseHandler extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AlwaysSober";
	private static final String TABLE_MESSAGE = "Message";
	
	private static final String KEY_ID = "_ID";
	private static final String COLUMN_NAME = "Name";
	private static final String COLUMN_PHONE = "Phone";
	private static final String COLUMN_DATE = "Date";
	private static final String COLUMN_TIME = "Time";
	private static final String COLUMN_MESSAGE = "Text_Message";
	private static final String COLUMN_FLAG = "Flag";

	public MessageDataBaseHandler( Context context )
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String query = 	"CREATE TABLE " + TABLE_MESSAGE + 
						"( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
						COLUMN_NAME + " TEXT, " + COLUMN_PHONE + " TEXT, " +
						COLUMN_DATE + " TEXT, " + COLUMN_TIME + " TEXT, " +
						COLUMN_MESSAGE + " TEXT, " + COLUMN_FLAG + " INTEGER )";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String query = 	"CREATE TABLE IF EXISTS " + TABLE_MESSAGE;
		db.execSQL(query);
		onCreate(db);
	}
	
	public void setAsSentMessage ( int id )
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query =	"_ID = ?";
		String[] whereArgs = {Integer.toString(id)};
		ContentValues args = new ContentValues();
		args.put("flag", 1);
		db.update(TABLE_MESSAGE, args, query, whereArgs);
		db.close();
	}
	
	public void addMessage ( Message message )
	{
		int flag;
		SQLiteDatabase db = this.getWritableDatabase();
		if ( message.getFlag() )
			flag = 1;
		else
			flag = 0;
		
		ContentValues insertValues = new ContentValues();
		insertValues.put(COLUMN_NAME, message.getContactsName());
		insertValues.put(COLUMN_PHONE, message.getPhone());
		insertValues.put(COLUMN_DATE, message.getDate());
		insertValues.put(COLUMN_TIME, message.getTime());
		insertValues.put(COLUMN_MESSAGE, message.getTextMessage());
		insertValues.put(COLUMN_FLAG, flag );
		
		db.insert(TABLE_MESSAGE, null, insertValues);
		db.close();
	}
	
	public void removeMessage ( Message message )
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query =	"_ID = ?";
		String[] whereArgs = {Integer.toString(message.get_id())};
		db.delete(TABLE_MESSAGE, query, whereArgs);
		db.close();
	}
	
	public ArrayList<Message> getMessages ()
	{
		ArrayList<Message> messages = new ArrayList<Message>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query =	"SELECT * FROM " + TABLE_MESSAGE + 
						" WHERE " + COLUMN_FLAG + " = 0";
		Cursor cursor = db.rawQuery(query, null);
		if ( !cursor.moveToFirst() )
		{
			db.close();
			return messages; // lenght will be zero!
		}
		else
		{
			while ( !cursor.isAfterLast() )
			{
				boolean flag;
				if ( Integer.parseInt(cursor.getString(6)) == 1 )
					flag = true;
				else
					flag = false;
				
				Message m = new Message
							(
								Integer.parseInt(cursor.getString(0)), 
								cursor.getString(1), 
								cursor.getString(2), 
								cursor.getString(3), 
								cursor.getString(4), 
								cursor.getString(5), 
								flag
							);
				
				messages.add(m);
				cursor.moveToNext();
			}
		}
		db.close();
		return messages;
	}
}