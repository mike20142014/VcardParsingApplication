package com.mike.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.database.DatabaseHandler;
import com.mike.database.ModelClass;
import com.mike.vcardparsingapplication.R;

public class HistoryFragment extends Fragment {

	DatabaseHandler mDatabaseHandler;

	ListAdapter mAdapter;
	ListView smsList;
	Context context;
	static ArrayList<String> newSmsList = new ArrayList<String>();
	static ArrayList<String> newSmsListMessages = new ArrayList<String>();
	TextView stubText , refresh;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View webserviceview = inflater.inflate(R.layout.history_list_view,
				container, false);
		
		return webserviceview;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
		Log.d("Data coming ? : ", "true");
		
		smsList = (ListView) getActivity().findViewById(R.id.sms_listView1);
		refresh = (TextView) getActivity().findViewById(R.id.refresh_text_view);				
		
		getSmsData();
		mAdapter = new ListAdapter();
		smsList.setAdapter(mAdapter);
		
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
				getSmsData();
				mAdapter = new ListAdapter();
				smsList.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				
			}
		});
		
		smsList.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		        AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
		        adb.setTitle("Delete?");
		        adb.setMessage("Are you sure you want to delete " + position);
		        final int positionToRemove = position;
		        adb.setNegativeButton("Cancel", null);
		        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            	for(int i = 0; i<100; i++){
							
							mDatabaseHandler.deleteContact(new ModelClass(i));
							mAdapter.notifyDataSetChanged();
														
						}
		            	//newSmsList.remove(positionToRemove);
		            	newSmsList.clear();
		                mAdapter.notifyDataSetChanged();
		            	
		                
		            }});
		        adb.show();
		        }
		    });
		
	}
	
	public void getSmsData(){
		
		newSmsList = new ArrayList<String>();
		newSmsListMessages = new ArrayList<String>();

		/*Collections.sort(newSmsList);
        Collections.sort(newSmsListMessages);*/
        
        Collections.sort(newSmsList, Collections.reverseOrder()); 
        Collections.sort(newSmsListMessages, Collections.reverseOrder()); 
        
		mDatabaseHandler = new DatabaseHandler(getActivity());
		Log.d("Reading: ", "Reading all contacts..");
        List<ModelClass> contacts = mDatabaseHandler.getAllContacts();
        Log.d("Reading Contacts: ", contacts.toString());
    
        String smsMessage = null;
        String smsNumber = null;
        
        if(contacts.size()==0){
        	
        
        	
        }else{
        	
        	for (ModelClass cn : contacts) {
                String log = "Id: "+cn.getID()+" ,Message: " + cn.getName() + " : " + "Phone: " + cn.getMessage() + "Name: " + cn.getPhoneNumber();           
                //Log.d("Data from onCreate() : ", log);
                smsMessage = cn.getName();
                smsNumber = cn.getPhoneNumber();   
                
                newSmsList.add(smsNumber);
                
                newSmsListMessages.add(smsMessage);
                
                Log.d("SMS Message: ", cn.getID() + " : " + newSmsList.toString());
                
            }
        	
            
           /* newSmsList.add(smsNumber);
            
            newSmsListMessages.add(smsMessage);*/
            /*Log.d("SMS String: ", smsMessage);
            Log.d("SMS String 1: ", smsNumber);*/
            //Log.d("SMS Message: ", newSmsList.toString());
            //Log.d("SMS Message New : ", newSmsList.toString());
        }
        
        
        
	}
	
	public class ListAdapter extends BaseAdapter{

		
		
        private ArrayList<String> mainList;
        private ArrayList<String> mainList2;


        public ListAdapter(Context applicationContext,
                ArrayList<String> questionForSliderMenu) {

            super();

            this.mainList = questionForSliderMenu;

        }

        public ListAdapter() {

            super();
            if(newSmsList==null){
         
            	return;
                
            }else{
            	
            	this.mainList = newSmsList;
                this.mainList2 = newSmsListMessages;
            	
            }
            

        }

        @Override
        public int getCount() {
        	
        			return mainList.size();
        		
        	
            
        }

        @Override
        public Object getItem(int position) {

            return mainList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

              	
            if (convertView == null) {
            	
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.history_row, null);
            }

            TextView tv1 = (TextView) convertView
                    .findViewById(R.id.row_sms_number);
            TextView tv2 = (TextView) convertView
                    .findViewById(R.id.row_sms_body);

            try {
            	
            	if(tv1==null||tv2==null){
            		
            		
            		
            	}else{
            		
            		tv1.setText(mainList2.get(position));
                    notifyDataSetChanged();
                    tv2.setText(mainList.get(position));
                    notifyDataSetChanged();
            	}
                
                
            } catch (Exception e) {

                e.printStackTrace();
            }

            return convertView;
        }

    }
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//getSmsData();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//getSmsData();
	}
}
