package com.mike.vcardparsingapplication;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.utils.IcVCardBuilder;
import com.mike.vcardparsingapplication.ContactsBook.ItemAdapter;

public class MainActivity extends Activity implements View.OnClickListener {

	private EditText destination_number, contact_list_phonenumber,
			contact_list_name, contact_list_address, edited_name,
			edited_number, edited_address;

	private TextView send_message_for_contactbook, send_message_for_vcard,
			send_message_for_edited_info, select_contactfor_destination,
			select_for_contactlist, extractedvCard;
	private ContactsBook mContactsBook;
	private ItemAdapter mItemAdapter;
	private SharedPreferences mPreferences;
	private SharedPreferences prefs;
	private String number;
	private String name;
	private String contact_name;
	private String destNumber;
	private String vcard;
	private String addedText;
	private String addedTextVcard;
	private String myPhoneNumber;

	public final static int RESULT_PICK_CONTACT_CONTACTBOOK = 1;
	public final static int RESULT_PICK_CONTACT_DESTINATIONBOOK = 2;
	public final static int SET_RESULTS_FOR_CONTACT_CONTACTBOOK = 3;
	public final static int SEND_VCARD = 4;

	final static String VCARDTYPE = "text/x-vcard";

	public static final String MyPREFERENCES = "your_file_name";
	public static final String Name = "contact_name";
	public static final String Phone = "destNumber";
	public static final String Email = "emailKey";

	private Intent mSendIntent;
	private Intent mDeliveryIntent;
	private static final String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
	private static final String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		init();

		if (destination_number.getText().toString().matches("")) {

			getDest();

		}
		TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneNumber = "+" + telemamanger.getLine1Number().toString();

		Log.i("My Number", myPhoneNumber.toString());

		if (contact_list_phonenumber.getText().toString() != null) {

			setFieldsForContactBookSelected();
			setVcard();

		} else {

			contact_list_phonenumber.setText("Same Value");

		}

		// To set destination contact in edittext
		/*
		 * mPreferences = getSharedPreferences("dest", Context.MODE_PRIVATE);
		 * destNumber = mPreferences.getString("number", destNumber); name =
		 * mPreferences.getString("name", name);
		 */

		addedText = myPhoneNumber + "\t" + "has sent you this information:"
				+ "\n" + "\n" + contact_name + "\n" + number + "\n" + "\n"
				+ "Thank You";

		addedText = myPhoneNumber + "\t" + "has sent you this information:"
				+ "\n" + "\n" + contact_list_name.getText().toString() + "\n"
				+ contact_list_phonenumber.getText().toString() + "\n" + "\n"
				+ "Thank you";
		addedTextVcard = myPhoneNumber + "\t"
				+ "has sent you this information:" + "\n" + "\n"
				+ contact_list_name.getText().toString() + "\n"
				+ extractedvCard.getText().toString() + "\n" + "\n"
				+ "Thank you";

	}

	private void TestCommit() {

		System.out.print("Test Commit");
		System.out.print("Test Commit 2");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("on result:", "onActivityResult:" + resultCode + " request:"
				+ requestCode);

		switch (requestCode) {
		case RESULT_PICK_CONTACT_DESTINATIONBOOK:

			// getActualVcard(data);

			// getContactForDestination(data);

			break;

		case RESULT_PICK_CONTACT_CONTACTBOOK:

			// getContactInfo(data);

			break;

		case SET_RESULTS_FOR_CONTACT_CONTACTBOOK:

			break;

		case SEND_VCARD:

			break;
		}

		// Finish the activity since we have what we need
		finish();
	}

	private void performContact() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(intent, RESULT_PICK_CONTACT_DESTINATIONBOOK);
	}

	private void getDest() {

		// To set destination contact in edittext
		mPreferences = getSharedPreferences("dest", Context.MODE_PRIVATE);
		destNumber = mPreferences.getString("number", destNumber);
		name = mPreferences.getString("name", name);
		destination_number.setText(destNumber);

	}

	private void SelectContactForDestination() {

		Intent selectContact = new Intent(this, DestinationBook.class);
		startActivityForResult(selectContact,
				RESULT_PICK_CONTACT_DESTINATIONBOOK);

	}

	private void SelectContactFromContactBook() {

		Intent selectContact_fromcontactbook = new Intent(MainActivity.this,
				ContactsBook.class);
		selectContact_fromcontactbook.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivityForResult(selectContact_fromcontactbook,
				RESULT_PICK_CONTACT_CONTACTBOOK);

	}

	private void getContactInfo(Intent data) {

		mPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		if (mPreferences.getBoolean("firstrun", true)) {
			mPreferences.edit().putBoolean("firstrun", false).commit();
		} else {

			contact_name = mPreferences.getString("nameDisplay", contact_name);
			number = mPreferences.getString("yourStringName", number);

			if (number == null) {
				return;
			}
			contact_list_name.setText(contact_name);
			contact_list_phonenumber.setText(number);

		}

	}

	private void getContactForDestination(Intent data) {

		String result = data.getStringExtra("destno");
		Log.i("RESULT FROM ACTIVITY: ", result);

	}

	private void getActualVcard(Intent data) {

		Uri contactData = data.getData();
		String vCardString = IcVCardBuilder
				.createVCardString(this, contactData);
		String getContactName = data.getDataString();
		String tempVcardString = null;
		String phoneNumber;
		String phoneDisplay = null;
		String placeHolderNumber = "7176839270";

		Cursor cursor = getContentResolver().query(contactData, null, null,
				null, null);
		if (cursor.moveToNext()) {

			phoneDisplay = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			Log.i("Vcard Name:", phoneDisplay + placeHolderNumber);
		}
		cursor.close();

		if (vCardString != null) {

			Log.d("TAG", "file not null");
			tempVcardString = vCardString;
			Log.d("TAG", tempVcardString + "\n" + "\n" + getContactName);

			/*
			 * sendMMStoHTCOne(placeHolderNumber, tempVcardString, phoneDisplay,
			 * VCARDTYPE);
			 */
			destination_number.setText(placeHolderNumber);

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mPreferences = getSharedPreferences("dest", 0);
		mPreferences.edit().remove("number").commit();
	}
	
	/*@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}*/

	public void destinationNumber() {

		mPreferences = getSharedPreferences("dest", Context.MODE_PRIVATE);

		if (mPreferences.getBoolean("firstrun", true)) {
			mPreferences.edit().putBoolean("firstrun", false).commit();
		} else {

			number = mPreferences.getString("number", number);

			if (number == null) {
				return;
			}

		}

	}

	public void sendMessageForContactPhoneBook() {

		if (!destination_number.getText().toString().matches("")) {

			if (destination_number.getText().toString().startsWith("7")
					|| destination_number.getText().toString().startsWith("+1")
					|| destination_number.getText().toString()
							.startsWith("+17")) {

				mSendIntent = new Intent(SMS_SEND_ACTION);
				mDeliveryIntent = new Intent(SMS_DELIVERY_ACTION);
				SmsManager sms = SmsManager.getDefault();
				ArrayList<String> parts = sms.divideMessage(addedText);
				int numParts = parts.size();

				ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
				ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

				for (int i = 0; i < numParts; i++) {
					sentIntents.add(PendingIntent.getBroadcast(
							getApplicationContext(), 0, mSendIntent, 0));
					deliveryIntents.add(PendingIntent.getBroadcast(
							getApplicationContext(), 0, mDeliveryIntent, 0));
				}
				Toast.makeText(getApplicationContext(), "Message Sent",
						Toast.LENGTH_LONG).show();
				sms.sendMultipartTextMessage(destination_number.getText()
						.toString(), null, parts, sentIntents, deliveryIntents);
				Toast.makeText(getApplicationContext(), "Message Sent",
						Toast.LENGTH_LONG).show();

			}else{
				
				Toast.makeText(getApplicationContext(),
						"Message not Sent, serice only available for US.",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");
				
			}
			
			if(!(destination_number.getText().toString().length() == 10||destination_number.getText().toString().length()==11||destination_number.getText().toString().length()==12)){
				
				Toast.makeText(getApplicationContext(),
						"Please enter a valid destination number.",
						Toast.LENGTH_LONG).show();
				//destination_number.setText("");
			}
			if (destination_number.getText().toString().startsWith("1800")) {

				Toast.makeText(
						getApplicationContext(),
						"Message not Sent, " + "\t"
								+ "1800 - Toll Free numbers not supported",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

		} else {

			
			Toast.makeText(getApplicationContext(),
					"Message not Sent, Destination number is null.",
					Toast.LENGTH_LONG).show();

		}
		
	}

	public void sendMessageForVcard() {

		if (destination_number.getText().toString().matches("")
				&& extractedvCard != null) {

			Toast.makeText(getApplicationContext(), "Message not Sent",
					Toast.LENGTH_LONG).show();

		} else {

			mSendIntent = new Intent(SMS_SEND_ACTION);
			mDeliveryIntent = new Intent(SMS_DELIVERY_ACTION);
			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> parts = sms.divideMessage(addedTextVcard);
			int numParts = parts.size();

			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

			for (int i = 0; i < numParts; i++) {
				sentIntents.add(PendingIntent.getBroadcast(
						getApplicationContext(), 0, mSendIntent, 0));
				deliveryIntents.add(PendingIntent.getBroadcast(
						getApplicationContext(), 0, mDeliveryIntent, 0));
			}
			Toast.makeText(getApplicationContext(), "Message Sent",
					Toast.LENGTH_LONG).show();
			sms.sendMultipartTextMessage(destination_number.getText()
					.toString(), null, parts, sentIntents, deliveryIntents);
			Toast.makeText(getApplicationContext(), "Message Sent",
					Toast.LENGTH_LONG).show();

		}

	}

	public void setFieldsForContactBookSelected() {

		mPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		if (mPreferences.getBoolean("firstrun", true)) {
			mPreferences.edit().putBoolean("firstrun", false).commit();
		} else {

			contact_name = mPreferences.getString("nameDisplay", contact_name);
			number = mPreferences.getString("yourStringName", number);

			if (number == null) {
				return;
			}
			contact_list_name.setText(contact_name);
			contact_list_phonenumber.setText(number);
			edited_name.setText(contact_name);
			edited_number.setText(number);

		}

	}

	public void setVcard() {

		mPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		if (mPreferences.getBoolean("firstrun", true)) {
			mPreferences.edit().putBoolean("firstrun", false).commit();
		} else {

			vcard = mPreferences.getString("vcard", vcard);
			if (vcard == null) {

				return;

			}
			extractedvCard.setText(vcard);

		}
	}

	private void init() {

		destination_number = (EditText) findViewById(R.id.destination_edit_text);
		contact_list_phonenumber = (EditText) findViewById(R.id.edit_text_contact_phonebook__number);
		contact_list_name = (EditText) findViewById(R.id.edit_text_contact_phonebook__name);
		contact_list_address = (EditText) findViewById(R.id.edit_text_contact_address);
		edited_name = (EditText) findViewById(R.id.edit_text_edited_contact_name);
		edited_number = (EditText) findViewById(R.id.edit_text_edited_contact_number);
		edited_address = (EditText) findViewById(R.id.edit_text_edited_contact_address);

		select_for_contactlist = (TextView) findViewById(R.id.select_contact_list);
		send_message_for_contactbook = (TextView) findViewById(R.id.send_message_with_contact_selected);
		send_message_for_vcard = (TextView) findViewById(R.id.send_message_with_vcard);
		send_message_for_edited_info = (TextView) findViewById(R.id.send_message_with_edited_information);

		// For Vcard
		extractedvCard = (TextView) findViewById(R.id.extracted_vCard);

		select_contactfor_destination = (TextView) findViewById(R.id.select_contact_listfor_destination);

		send_message_for_contactbook.setOnClickListener(this);
		send_message_for_vcard.setOnClickListener(this);
		send_message_for_edited_info.setOnClickListener(this);
		select_contactfor_destination.setOnClickListener(this);
		select_for_contactlist.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.select_contact_listfor_destination:

			SelectContactForDestination();

			// performContact();

			break;

		case R.id.select_contact_list:

			SelectContactFromContactBook();

			break;

		case R.id.send_message_with_contact_selected:

			// sendMessageForContactPhoneBook(destNumber, addedText);
			sendMessageForContactPhoneBook();

		default:
			break;

		case R.id.send_message_with_vcard:

			sendMessageForVcard();

		}

	}

	private void sendMMStoHTCOne(String phone, String vCardString,
			String vCardName, String mediaType) {

		sendMMStoHTCOne(phone, vCardString, vCardName, mediaType, null);
	}

	@SuppressLint("NewApi")
	private void sendMMStoHTCOne(String phone, String vCardString,
			String vCardName, String mediaType, String msg) {

		Intent sendIntent = new Intent(
				"com.htc.intent.action.LAUNCH_MSG_COMPOSE");
		sendIntent.setData(Uri.parse(vCardString));
		sendIntent.setType("text/x-vCard");
		sendIntent.putExtra(Intent.EXTRA_TEXT, vCardString);
		sendIntent.putExtra("address", phone);
		sendIntent.putExtra("name", vCardName);
		startActivity(sendIntent);
	}

}
