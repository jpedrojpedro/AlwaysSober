package br.puc.rio.lac.course.android.service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import br.puc.rio.lac.course.android.model.Message;
import br.puc.rio.lac.course.android.model.MessageDataBaseHandler;

public class VerifyMessages extends Service implements Runnable
{
	
	private ArrayList<Message> pendingMessages;
	private final MessageDataBaseHandler dbHandler = new MessageDataBaseHandler(this);

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		Log.i("ServiceAlwaysSober", "Service was created");
		super.onCreate();
		new Thread(this).start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("ServiceAlwaysSober", "Service is alive");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy()
	{
		Log.i("ServiceAlwaysSober", "Service was destroyed");
		super.onDestroy();
	}

	@Override
	public void run()
	{
		while ( true )
		{
			Log.i("ThreadAlwaysSober", "Check Delivery");
			this.pendingMessages = this.dbHandler.getMessages();
			for ( int i = 0; i < this.pendingMessages.size(); i++ )
			{
				int respDate, respTime;
				respDate = this.compareDate(this.pendingMessages.get(i).getDate());
				respTime = this.compareTime(this.pendingMessages.get(i).getTime());
				if ( respDate == 1 || // after scheduled date
					 ( respDate == 0 && respTime == 1) || // same scheduled date and after time
					 ( respDate == 0 && respTime == 0) // same schedule date an time
					)
				{
					Log.i("ThreadAlwaysSober", "Should Deliver: " + 
									this.pendingMessages.get(i).get_id());
					
					SmsManager sms = SmsManager.getDefault();
			        sms.sendTextMessage(
			        					this.pendingMessages.get(i).getPhone(),
			        					null,
			        					this.pendingMessages.get(i).getTextMessage(),
			        					null,
			        					null
			        					);
			        Log.i("FlagAlwaysSober", "Alteração de Flag");
			        dbHandler.setAsSentMessage(this.pendingMessages.get(i).get_id());
				}
			}
			try
			{
				Thread.sleep(300000); // 1000 milliseconds = 1 second
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private int compareDate ( String date )
	{
		if ( date.length() != 10 )
			return -99;
		
		Calendar c = Calendar.getInstance();
		Date d = null;
		Date today = null;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		try
		{
			d = format.parse(date);
			today = format.parse(Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/" + 
								 Integer.toString(c.get(Calendar.MONTH) + 1) + "/" + 
								 Integer.toString(c.get(Calendar.YEAR)));
			Log.i("DateAlwaysSober", "Today: " + today);
			Log.i("DateAlwaysSober", "Schedule Day: " + d);
			
			if ( d.before(today) )
				return 1;
			else if ( d.after(today) )
				return -1;
			else
				return 0;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private int compareTime ( String time )
	{
		if ( time.length() != 5 )
			return -99;
		
		Calendar c = Calendar.getInstance();

		Time now = new Time(c.get(Calendar.HOUR_OF_DAY), 
							c.get(Calendar.MINUTE), 
							0
							);
		
		Log.i("DateAlwaysSober", "Now: " + now);
		
		String[] splitDate = time.split(":");
		
		Time t = new Time(	Integer.parseInt(splitDate[0]), 
							Integer.parseInt(splitDate[1]), 
							0
						 );
		
		Log.i("DateAlwaysSober", "Schedule Time: " + t);
		
		if ( t.before(now) )
			return 1;
		else if ( t.after(now) )
			return -1;
		else
			return 0;
	}
	
}