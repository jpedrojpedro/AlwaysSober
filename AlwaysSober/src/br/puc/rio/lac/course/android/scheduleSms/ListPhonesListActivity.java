package br.puc.rio.lac.course.android.scheduleSms;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListPhonesListActivity extends ListActivity implements OnItemClickListener
{
	private String name;
	private ArrayList<String> phones;
	private ListView listPhones;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.name = this.getIntent().getStringExtra("contactName");
		this.phones = this.getIntent().getStringArrayListExtra("phoneOptions");
		if ( this.phones != null )
		{
			String[] phoneNumbers = new String[this.phones.size()];
			for ( int i = 0; i < this.phones.size(); i++ )
				phoneNumbers[i] = this.phones.get(i);
			this.setListAdapter	(new ArrayAdapter<String>(	this, 
															R.layout.list_phones,
															phoneNumbers
														)
								);
		}
		this.listPhones = getListView();
		this.listPhones.setTextFilterEnabled(true);
		this.listPhones.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		String phone = (String) this.listPhones.getItemAtPosition(arg2);
		getIntent().putExtra("phoneNumber", phone);
		getIntent().putExtra("name", this.name);
		setResult(Activity.RESULT_OK, getIntent());
		finish();
	}

}