/*package com.mike.customadapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mike.vcardparsingapplication.R;


public class ItemAdapter extends CursorAdapter {
	
	

	private List<String> contactName = new ArrayList<String>();
	private List<String> contactNumber = new ArrayList<String>();
	
    @Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	private LayoutInflater mLayoutInflater;
    private Context mContext;
    public ItemAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context); 
    }
    public ItemAdapter(Context context, Cursor c, List<String> contactName,
			List<String> contactNumber, Context mContext) {
		super(context, c);
		this.contactName = contactName;
		this.contactNumber = contactNumber;
		this.mContext = mContext;
	}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.contact_list_row, parent, false);
        return v;
    }

    *//**
     * @author will
     * 
     * @param   v
     *          The view in which the elements we set up here will be displayed.
     * 
     * @param   context
     *          The running context where this ListView adapter will be active.
     * 
     * @param   c
     *          The Cursor containing the query results we will display.
     *//*

    @Override
    public void bindView(View v, Context context, Cursor c) {
        String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String contactNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
      
        *//**
         * Next set the title of the entry.
         *//*

        TextView contacts = (TextView) v.findViewById(R.id.contact_name_row_id);
        if (contacts != null) {
        	contacts.setText(contactName);
        }

        *//**
         * Set Date
         *//*

        TextView numbers = (TextView) v.findViewById(R.id.contact_number_row_id);
        if (numbers != null) {
        	numbers.setText(contactNumber);
        }       

    }
}*/