package br.puc.rio.lac.course.android.scheduleSms;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import br.puc.rio.lac.course.android.model.Message;
import br.puc.rio.lac.course.android.model.MessageDataBaseHandler;

public class NewScheduleActivity extends Activity implements OnClickListener
{
	private static int PICK_CONTACT = 1;
	private static int PICK_PHONE = 2;
	private static String SH_PR_NAME = "nameField";
	private static String SH_PR_PHONE = "phoneField";
	private static String SH_PR_MESSAGE = "messageField";
	private EditText name;
	private EditText phone;
	private DatePicker date;
	private TimePicker time;
	private EditText message;
	private Button findContact;
	private Button addSchedule;
	private MessageDataBaseHandler dbHandler;
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onClick(View v)
	{
		if ( v.findViewById(v.getId()).equals(this.findContact) )
		{
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, NewScheduleActivity.PICK_CONTACT);
		}
		else if ( v.findViewById(v.getId()).equals(this.addSchedule) )
		{
			String error = this.verifyFields();
			
			if ( error != null )
				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			else
			{	
				// add new message on database and return a toast
				dbHandler = new MessageDataBaseHandler(this);
				String day, month, year, hour, minute;
				
				day = Integer.toString(this.date.getDayOfMonth());
				month = Integer.toString((this.date.getMonth() + 1));
				year = Integer.toString(this.date.getYear());
				hour = Integer.toString(this.time.getCurrentHour());
				minute = Integer.toString(this.time.getCurrentMinute());
				
				if ( day.length() < 2 ) day = "0" + day;
				if ( month.length() < 2 ) month = "0" + month;
				if ( year.length() < 2 ) year = "0" + year;
				if ( hour.length() < 2 ) hour = "0" + hour;
				if ( minute.length() < 2 ) minute = "0" + minute;
				
				String date = 	day + "/" + month + "/" + year;
				String time =	hour + ":" + minute;
								;
				Message m = new Message	(
										this.name.getText().toString(), 
										this.phone.getText().toString(), 
										date, 
										time, 
										this.message.getText().toString(), 
										false
										);
				dbHandler.addMessage(m);

				Toast.makeText(this, "Message successfully stored", Toast.LENGTH_LONG).show();
				
				Intent i = new Intent(NewScheduleActivity.this, MainActivity.class);
				startActivity(i);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_schedule);
		this.name = (EditText) findViewById(R.id.nameTyped);
		this.phone = (EditText) findViewById(R.id.phoneTyped);
		this.date = (DatePicker) findViewById(R.id.datePicker);
		this.time = (TimePicker) findViewById(R.id.timePicker);
		this.message = (EditText) findViewById(R.id.messageTyped);
		this.findContact = (Button) findViewById(R.id.findContact);
		this.addSchedule = (Button) findViewById(R.id.addSchedule);
		this.findContact.setOnClickListener(this);
		this.addSchedule.setOnClickListener(this);
		// Getting area to recovery values from possible interruptions
		this.sharedPreferences = getSharedPreferences("NewMessageFields", MODE_PRIVATE);
		// Recovering values from SharedPreferences
		this.name.setText(this.sharedPreferences.getString(SH_PR_NAME, ""));
		this.phone.setText(this.sharedPreferences.getString(SH_PR_PHONE, ""));
		this.message.setText(this.sharedPreferences.getString(SH_PR_MESSAGE, ""));
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		// Creating area to recovery values from possible interruptions
		SharedPreferences.Editor spEditor = this.sharedPreferences.edit();
		// Setting values into SharedPreferences
		spEditor.putString(SH_PR_NAME, this.name.getText().toString());
		spEditor.putString(SH_PR_PHONE, this.phone.getText().toString());
		spEditor.putString(SH_PR_MESSAGE, this.message.getText().toString());
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		// Creating area to recovery values from possible interruptions
		SharedPreferences.Editor spEditor = this.sharedPreferences.edit();
		// Setting values into SharedPreferences
		spEditor.putString(SH_PR_NAME, this.name.getText().toString());
		spEditor.putString(SH_PR_PHONE, this.phone.getText().toString());
		spEditor.putString(SH_PR_MESSAGE, this.message.getText().toString());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if ( requestCode == NewScheduleActivity.PICK_CONTACT )
		{
			if ( resultCode != Activity.RESULT_CANCELED)
			{
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				c.moveToFirst();
				String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				ArrayList<String> phoneNumbers = new ArrayList<String>();
				
				if ( hasPhone.equalsIgnoreCase("1") )
					hasPhone = "true";
				else
					hasPhone = "false" ;
				
				if ( Boolean.parseBoolean(hasPhone) ) 
				{
					Cursor phones = getContentResolver().query(	ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
																null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + 
																" = " + contactId, null, null);
					while (phones.moveToNext()) 
						phoneNumbers.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					
					phones.close();
				}
					
				if ( phoneNumbers.size() == 0 )
				{
					// selected contact does not have phone number
					Toast msg = Toast.makeText(	this.getApplicationContext(), 
												"Contact selected does not have a phone number", 
												Toast.LENGTH_LONG);
					msg.setGravity(Gravity.TOP, 0, 100);
					msg.show();
					// populate fields with empty values
					this.name.setText("");
					this.phone.setText("");
				}
				else if ( phoneNumbers.size() == 1 )
				{
					// selected contact only has one phone number
					this.name.setText(name);
					this.phone.setText(phoneNumbers.get(0));
				}
				else
				{
					// selected contact has more than one phone number
					// open screen with phone options for the users choice
					Intent intent = new Intent(NewScheduleActivity.this, ListPhonesListActivity.class);
					intent.putExtra("phoneOptions", phoneNumbers);
					intent.putExtra("contactName", name);
					startActivityForResult(intent, NewScheduleActivity.PICK_PHONE);
				}
			}
		}
		else if ( requestCode == NewScheduleActivity.PICK_PHONE )
		{
			if ( resultCode == Activity.RESULT_OK )
			{
					this.phone.setText(data.getStringExtra("phoneNumber"));
					this.name.setText(data.getStringExtra("name"));
			}
		}
		else
		{
			Log.e("DEFAULT", "Recebeu uma chave que não esperava!");
		}
	}
	
	private String verifyFields ()
	{
		if ( this.name.getText().length() == 0 )
			return "Please, fill in the Name field";
		else if ( this.phone.getText().length() == 0 )
			return "Please, fill in the Phone field";
		else if ( this.message.getText().length() == 0 )
			return "Please, fill in the Message field";
		else
			return null;
	}
}