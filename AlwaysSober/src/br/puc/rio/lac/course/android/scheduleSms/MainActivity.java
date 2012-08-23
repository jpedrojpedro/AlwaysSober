package br.puc.rio.lac.course.android.scheduleSms;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import br.puc.rio.lac.course.android.model.Message;
import br.puc.rio.lac.course.android.model.MessageDataBaseHandler;

public class MainActivity extends Activity implements OnClickListener
{
    private Button newSchedule;
    private ArrayList<Message> messages;
    private MessageDataBaseHandler dbHandler;
    private ListView showMessages;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.showMessages = (ListView) findViewById(R.id.listView);
        this.newSchedule = (Button) findViewById(R.id.newScheduleButton);
        this.newSchedule.setOnClickListener(this);
        
        this.dbHandler = new MessageDataBaseHandler(this);
        this.messages = this.dbHandler.getMessages();
        
        this.populateListView(this.messages);
        
        registerForContextMenu(this.showMessages);
        
        if ( !this.isMyServiceRunning() )
        	startService( new Intent(MainActivity.this, 
        				  br.puc.rio.lac.course.android.service.VerifyMessages.class) );
    }

	@Override
	public void onClick(View v)
	{
		if ( v.findViewById(v.getId()).equals(this.newSchedule) )
		{
			Intent chamadaNewSchedule = new Intent (MainActivity.this, NewScheduleActivity.class);
			startActivity(chamadaNewSchedule);
		}
	}
	
	private void populateListView ( ArrayList<Message> messages )
	{
		if ( this.messages != null )
		{
			MessageArrayAdapter messageAdapter = new MessageArrayAdapter(this, this.messages);
			this.showMessages.setAdapter(messageAdapter);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("What do you want to do?");
		menu.add("Edit");
		menu.add("Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		if ( item.getTitle().equals("Edit") )
			Toast.makeText(this, "Maybe on Next Version!", Toast.LENGTH_LONG).show();
		else if ( item.getTitle().equals("Delete") )
		{
			dbHandler.removeMessage(this.messages.get(info.position));
			this.messages = this.dbHandler.getMessages();
			this.populateListView(this.messages);
			Toast.makeText(	this, "Message successfully deleted", 
							Toast.LENGTH_LONG).show();
		}
		else
			return false;
		return true;
	}
	
	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Do you really want to exit?")
	        .setCancelable(false)
	        .setPositiveButton("Yes", 
	        new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog,int id)
	            {
	            	Intent i = new Intent(Intent.ACTION_MAIN);
	            	i.addCategory(Intent.CATEGORY_HOME);
	            	startActivity(i);
	            }
	        })
	        .setNegativeButton("No", 
	        new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog,int id)
	            {
	                dialog.cancel();
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
	}
	
	private boolean isMyServiceRunning()
	{
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
	    {
	        if ("br.puc.rio.lac.course.android.VerifyMessages".equals(service.service.getClassName()))
	            return true;
	    }
	    return false;
	}

}