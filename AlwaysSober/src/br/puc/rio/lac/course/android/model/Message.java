package br.puc.rio.lac.course.android.model;

public class Message
{
	private int _id;
	private String contactsName;
	private String phone;
	private String date;
	private String time;
	private String textMessage;
	private boolean flag;
	
	public Message ( String name, String phone, String date, 
					 String time, String message, boolean flag )
	{
		this.contactsName = name;
		this.phone = phone;
		this.date = date;
		this.time = time;
		this.textMessage = message;
		this.flag = flag;
	}
	
	public Message ( int id, String name, String phone, 
					 String date, String time, String message, 
					 boolean flag )
	{
		this._id = id;
		this.contactsName = name;
		this.phone = phone;
		this.date = date;
		this.time = time;
		this.textMessage = message;
		this.flag = flag;
	}

	public int get_id()
	{
		return _id;
	}

	public void set_id(int _id)
	{
		this._id = _id;
	}

	public String getContactsName()
	{
		return contactsName;
	}

	public void setContactsName(String contactsName)
	{
		this.contactsName = contactsName;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}
	
	public String getTextMessage()
	{
		return textMessage;
	}

	public void setTextMessage(String textMessage)
	{
		this.textMessage = textMessage;
	}
	
	public boolean getFlag()
	{
		return flag;
	}

	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
}