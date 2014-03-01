package com.mike.vcardparsingapplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.utils.Utils;

public class ContactsBook extends Activity implements OnItemClickListener{
	
	ListView contacts_list;
	private String mSearchTerm; 
	private ItemAdapter mItemAdapter;
	private ArrayList<String> contactName = new ArrayList<String>();
	private ArrayList<String> contactNumber = new ArrayList<String>();
	private Cursor cursor;
	private String vCard;
	private int lastPosition = -1;
	private static String DisplayName;
	private static String DisplayNumber;
	private static String PreferedDisplayName;
	private static String DisplayAddress;
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list_view_main);
		//setContentView(R.layout.main_activity);
		
		
		cursor = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
						null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
								+ " COLLATE LOCALIZED ASC");
				
		contacts_list = (ListView)findViewById(R.id.contact_list_view);
		mItemAdapter = new ItemAdapter(ContactsBook.this, cursor);
		contacts_list.setAdapter(mItemAdapter);
		//getAllContacts();
		
		contacts_list.setOnItemClickListener(this);
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getAllContacts() {
		Cursor cursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");

		// Looping through the contacts and adding them to arrayList
		while (cursor.moveToNext()) {
			String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			

			contactName.add(name);
			Log.i("Contact Name","------"+name);
			contactNumber.add(phoneNumber);
		}
		
		cursor.close();
	}
	
	
	public class ItemAdapter extends CursorAdapter{
		
		private AlphabetIndexer mAlphabetIndexer;
		 private TextAppearanceSpan highlightTextSpan;;

		private ArrayList<String> names;
		private ArrayList<String> numbers;
		private  String destinationNumber;

		public ItemAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}
		public ItemAdapter(Context context,Cursor c, String destinationNumber){
			
			super(context, c);
			this.destinationNumber = destinationNumber;
			
		}
		

		public ItemAdapter(Context context, Cursor c, ArrayList<String> names,
				ArrayList<String> numbers) {
			super(context, c);
			this.names = names;
			this.numbers = numbers;
			final String alphabet = context.getString(R.string.alphabet);
			mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);
			highlightTextSpan = new TextAppearanceSpan(getApplicationContext(), R.style.searchTextHiglight);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return super.getCount();
		}


		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return super.getItemId(position);
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());

			
		    View v = inflater.inflate(R.layout.contact_list_row, parent, false);
		            
		           
		            final ViewHolder holder = new ViewHolder();
					holder.text1 = (TextView) v.findViewById(R.id.contact_name_row_id);
			        holder.text2 = (TextView) v.findViewById(R.id.contact_number_row_id);
			        
			        v.setTag(holder);
			        
		            return v;
			
		}
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			
			   Log.d("TAG","CursorAdapter BindView");
			   String name = null;
			   String number = null;
			   if(cursor!=null){
			    Log.d("TAG","CursorAdapter BindView:Cursor not null");
			    name=cursor.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			    number = cursor.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			   }
			   
			   //For Animation
			   /*Animation out = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in);
				contacts_list.startAnimation(out);
				contacts_list.setVisibility(View.INVISIBLE);
				
				if(contacts_list.getVisibility()==View.INVISIBLE){
					
					contacts_list.setVisibility(View.VISIBLE);
					
				}*/
			   Animation animation = AnimationUtils.loadAnimation(context, (mItemAdapter.getViewTypeCount()> lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
			    view.startAnimation(animation);
			    lastPosition = mItemAdapter.getViewTypeCount();
			/*int nameInt = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			int numberInt = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);*/
			final ViewHolder holder = (ViewHolder) view.getTag();
			holder.text1.setText(name);
	        holder.text2.setText(number);
	        
			 
		}
		 private class ViewHolder {
	            TextView text1;
	            TextView text2;
	            QuickContactBadge icon;
	        }
		
	}
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			
			
			if(contacts_list.getVisibility()==View.VISIBLE){
				
				Animation animation = AnimationUtils.loadAnimation(this, (mItemAdapter.getViewTypeCount()> lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
			    view.startAnimation(animation);
			    lastPosition = mItemAdapter.getViewTypeCount();
				
			}
			else{
				
				Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
				contacts_list.startAnimation(in);
				contacts_list.setVisibility(View.VISIBLE);
			}
			
			
			final Cursor cursor = mItemAdapter.getCursor();
			cursor.moveToPosition(position);
			String lookupKey = cursor.getString(cursor
	                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
			Uri uri = Uri.withAppendedPath(
	                ContactsContract.Contacts.CONTENT_VCARD_URI,
	                lookupKey);
			
			DisplayName=cursor.getString(cursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			DisplayNumber = cursor.getString(cursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			
			AssetFileDescriptor fd;
			try {
	            fd = getApplicationContext().getContentResolver()
	                    .openAssetFileDescriptor(uri, "r");
	            FileInputStream fis = fd.createInputStream();
	            byte[] b = new byte[(int) fd.getDeclaredLength()];
	            fis.read(b);
	            vCard = new String(b);
	            Log.i("TAG", vCard);
	        }catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
			
			//Toast.makeText(getApplicationContext(), "Name:" + "----"+ DisplayName + "\t" + "Number:" + "----" + DisplayNumber, Toast.LENGTH_SHORT).show();
			String outlet_no = DisplayNumber.toString();
			String name = DisplayName.toString();
			String number = DisplayNumber.toString();
			
		    System.out.println(outlet_no);

		    
		    
		    Intent myIntent = new Intent(ContactsBook.this, MainActivity.class);  
		   
		    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    startActivity(myIntent);

		    SharedPreferences prefs = getSharedPreferences("your_file_name", MODE_PRIVATE);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString("yourStringName", DisplayNumber);
		    editor.putString("nameDisplay", DisplayName);
		    editor.putString("vcard", vCard);
		    editor.commit();
			
		}
		
		
		
		public interface ContactsQuery {

	        // An identifier for the loader
	        final static int QUERY_ID = 1;

	        // A content URI for the Contacts table
	        final static Uri CONTENT_URI = Contacts.CONTENT_URI;

	        // The search/filter query Uri
	        final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

	        // The selection clause for the CursorLoader query. The search criteria defined here
	        // restrict results to contacts that have a display name and are linked to visible groups.
	        // Notice that the search on the string provided by the user is implemented by appending
	        // the search string to CONTENT_FILTER_URI.
	        @SuppressLint("InlinedApi")
	        final static String SELECTION =
	                (Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) +
	                "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

	        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
	        // sort key allows for localization. In earlier versions. use the display name as the sort
	        // key.
	        @SuppressLint("InlinedApi")
	        final static String SORT_ORDER =
	                Utils.hasHoneycomb() ? Contacts.SORT_KEY_PRIMARY : Contacts.DISPLAY_NAME;

	        // The projection for the CursorLoader query. This is a list of columns that the Contacts
	        // Provider should return in the Cursor.
	        @SuppressLint("InlinedApi")
	        final static String[] PROJECTION = {

	                // The contact's row id
	                Contacts._ID,

	                // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
	                // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
	                // a "permanent" contact URI.
	                Contacts.LOOKUP_KEY,

	                // In platform version 3.0 and later, the Contacts table contains
	                // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
	                // some other useful identifier such as an email address. This column isn't
	                // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
	                // instead.
	                Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,

	                // In Android 3.0 and later, the thumbnail image is pointed to by
	                // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
	                // you generate the pointer from the contact's ID value and constants defined in
	                // android.provider.ContactsContract.Contacts.
	                Utils.hasHoneycomb() ? Contacts.PHOTO_THUMBNAIL_URI : Contacts._ID,

	                // The sort order column for the returned Cursor, used by the AlphabetIndexer
	                SORT_ORDER,
	        };

	        // The query column numbers which map to each value in the projection
	        final static int ID = 0;
	        final static int LOOKUP_KEY = 1;
	        final static int DISPLAY_NAME = 2;
	        final static int PHOTO_THUMBNAIL_DATA = 3;
	        final static int SORT_KEY = 4;
	    }		

}
