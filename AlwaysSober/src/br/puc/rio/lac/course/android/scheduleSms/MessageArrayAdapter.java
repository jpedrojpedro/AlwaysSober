package br.puc.rio.lac.course.android.scheduleSms;

import java.util.List;

import br.puc.rio.lac.course.android.model.Message;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageArrayAdapter extends ArrayAdapter<Message> 
{
	private final Context context;
	private final List<Message> messages;
	
	public MessageArrayAdapter ( Context context, List<Message> messages )
	{
		super ( context, R.layout.message_row, messages);
		this.context = context;
		this.messages = messages;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = 	(LayoutInflater) context.
									getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.message_row, parent, false);
		
		Message m = messages.get(position);
		
		TextView tvId = (TextView) rowView.findViewById(R.id.messageId);
		TextView tvName = (TextView) rowView.findViewById(R.id.contactName);
		TextView tvPhone = (TextView) rowView.findViewById(R.id.contactPhone);
		
		tvId.setText(String.valueOf(m.get_id()));
		tvName.setText(m.getContactsName());
		tvPhone.setText(m.getPhone());
		
		return rowView;
	}
}