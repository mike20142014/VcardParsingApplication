package com.mike.pages;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.utils.MyVcardBuilder;
import com.mike.vcardparsingapplication.ContactsBook;
import com.mike.vcardparsingapplication.DestinationBook;
import com.mike.vcardparsingapplication.R;

public class MainFragment extends Fragment implements OnClickListener {

	private EditText destination_number, contact_list_phonenumber,
			contact_list_name, contact_list_address, edited_name,
			edited_number, edited_address;

	private TextView send_message_for_contactbook, send_message_for_vcard,
			send_message_for_edited_info, select_contactfor_destination,
			select_for_contactlist, extractedvCard;

	private SharedPreferences mPreferences,mPreferencesForDest;

	private String number;
	private String name;
	private String contact_name;
	private String destNumber;
	private String vcard;
	private String addedText;
	private String addedTextEdited;
	private String addedTextVcard;
	private String myPhoneNumber;
	private String editTextValue;
	private Context context;

	String phoneDisplay;
	String uri;
	URI newUriParsable;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View webserviceview = inflater.inflate(R.layout.mainfragment,
				container, false);

		return webserviceview;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
		
		editTextValue = getActivity().getIntent().getStringExtra("valueId");
		
		if (destination_number.getText().toString()!=null) {

			getDest();

		}
		
		TelephonyManager telemamanger = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneNumber = "+" + telemamanger.getLine1Number().toString();

		Log.i("My Number", myPhoneNumber.toString());

		if (contact_list_phonenumber.getText().toString() != null) {

			setFieldsForContactBookSelected();
			setVcard();

		} else {

			contact_list_phonenumber.setText("Same Value");

		}

		addedTextVcard = myPhoneNumber + "\t"
				+ "has sent you this information:" + "\n" + "\n"
				+ contact_list_name.getText().toString() + "\n"
				+ extractedvCard.getText().toString() + "\n" + "\n" +editTextValue + "\n"
				+ "Thank you";
		
	}

	
	private void init() {

		destination_number = (EditText) getActivity().findViewById(
				R.id.destination_edit_text);
		contact_list_phonenumber = (EditText) getActivity().findViewById(
				R.id.edit_text_contact_phonebook__number);
		contact_list_name = (EditText) getActivity().findViewById(
				R.id.edit_text_contact_phonebook__name);
		contact_list_address = (EditText) getActivity().findViewById(
				R.id.edit_text_contact_address);
		edited_name = (EditText) getActivity().findViewById(
				R.id.edit_text_edited_contact_name);
		edited_number = (EditText) getActivity().findViewById(
				R.id.edit_text_edited_contact_number);
		edited_address = (EditText) getActivity().findViewById(
				R.id.edit_text_edited_contact_address);

		select_for_contactlist = (TextView) getActivity().findViewById(
				R.id.select_contact_list);
		send_message_for_contactbook = (TextView) getActivity().findViewById(
				R.id.send_message_with_contact_selected);
		send_message_for_vcard = (TextView) getActivity().findViewById(
				R.id.send_message_with_vcard);
		send_message_for_edited_info = (TextView) getActivity().findViewById(
				R.id.send_message_with_edited_information);

		// For Vcard
		extractedvCard = (TextView) getActivity().findViewById(
				R.id.extracted_vCard);

		select_contactfor_destination = (TextView) getActivity().findViewById(
				R.id.select_contact_listfor_destination);

		send_message_for_contactbook.setOnClickListener(this);
		send_message_for_vcard.setOnClickListener(this);
		send_message_for_edited_info.setOnClickListener(this);
		select_contactfor_destination.setOnClickListener(this);
		select_for_contactlist.setOnClickListener(this);

	}

	private void getDest() {

		// To set destination contact in edittext
		mPreferencesForDest = getActivity().getSharedPreferences("dest", Context.MODE_PRIVATE);
		destNumber = mPreferencesForDest.getString("number", destNumber);
		name = mPreferencesForDest.getString("name", name);
		destination_number.setText(destNumber);

	}
	
	public void setFieldsForContactBookSelected() {

		mPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		if (mPreferences.getBoolean("firstrun", true)) {
			mPreferences.edit().putBoolean("firstrun", false).commit();
		} else {

			contact_name = mPreferences.getString("nameDisplay", contact_name);
			number = mPreferences.getString("yourStringName", number);

			if (number == null) {
				return;
			}
			contact_list_phonenumber.setVisibility(View.VISIBLE);
			contact_list_phonenumber.setTypeface(null, Typeface.BOLD_ITALIC);
			contact_list_name.setVisibility(View.VISIBLE); 
			contact_list_name.setTypeface(null, Typeface.BOLD_ITALIC);
			contact_list_address.setVisibility(View.VISIBLE);
			contact_list_address.setTypeface(null, Typeface.BOLD_ITALIC);
			contact_list_name.setText(contact_name);
			contact_list_phonenumber.setText(number);
			edited_name.setText(contact_name);
			edited_number.setText(number);

		}

	}
	
	public void setVcard() {

		mPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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
	
	private void SelectContactForDestination() {

		Intent selectContact = new Intent(getActivity(),
				DestinationBook.class);
		startActivity(selectContact);

	}

	private void SelectContactFromContactBook() {

		Intent selectContact_fromcontactbook = new Intent(getActivity(), ContactsBook.class);
		selectContact_fromcontactbook.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(selectContact_fromcontactbook);

	}

	public void sendMessageForContactPhoneBook() {

		if (!destination_number.getText().toString().matches("")) {

			if (destination_number.getText().toString().startsWith("7")
					|| destination_number.getText().toString().startsWith("+1")
					|| destination_number.getText().toString().startsWith("4")
					|| destination_number.getText().toString()
							.startsWith("+14")
					|| destination_number.getText().toString()
							.startsWith("+182")
					|| destination_number.getText().toString()
							.startsWith("+17")) {

				addedText = myPhoneNumber + "\t"
						+ "has sent you this information:" + "\n" + "\n"
						+ "Name : " + contact_list_name.getText().toString()
						+ "\n" + "Number : "
						+ contact_list_phonenumber.getText().toString() + "\n"
						+ "Location : "
						+ contact_list_address.getText().toString() + "\n"
						+ "\n" + "Thank you";

				mSendIntent = new Intent(SMS_SEND_ACTION);
				mDeliveryIntent = new Intent(SMS_DELIVERY_ACTION);
				SmsManager sms = SmsManager.getDefault();
				ArrayList<String> parts = sms.divideMessage(addedText);
				int numParts = parts.size();

				ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
				ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

				for (int i = 0; i < numParts; i++) {
					sentIntents
							.add(PendingIntent.getBroadcast(
									getActivity(), 0,
									mSendIntent, 0));
					deliveryIntents.add(PendingIntent.getBroadcast(
							getActivity(), 0,
							mDeliveryIntent, 0));
				}
				Toast.makeText(getActivity(), "Message Sent",
						Toast.LENGTH_LONG).show();
				sms.sendMultipartTextMessage(destination_number.getText()
						.toString(), null, parts, sentIntents, deliveryIntents);
				Toast.makeText(getActivity(), "Message Sent",
						Toast.LENGTH_LONG).show();

			} else {

				Toast.makeText(context.getApplicationContext(),
						"Message not Sent, serice only available for US.",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

			if (destNumber.length() < 10) {

				Toast.makeText(context.getApplicationContext(),
						"Please enter a valid destination number.",
						Toast.LENGTH_LONG).show();
				// destination_number.setText("");
			}
			if (destination_number.getText().toString().startsWith("1800")) {

				Toast.makeText(
						context.getApplicationContext(),
						"Message not Sent, " + "\t"
								+ "1800 - Toll Free numbers not supported",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

		} else {

			Toast.makeText(context.getApplicationContext(),
					"Message not Sent, Destination number is null.",
					Toast.LENGTH_LONG).show();

		}

	}

	// To send VCARD AS MMS
	private void getActualVcard() {

		mPreferences = getActivity().getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		uri = mPreferences.getString("uri", uri);

		Log.i("New Uri: ", uri);

		Intent data = getActivity().getIntent();
		Uri contactData = data.getParcelableExtra("uri");

		Uri newUri = Uri.parse(uri);
		Uri tempUri = null;
		File vCardFile = MyVcardBuilder.createVCard(
				getActivity(), contactData);
		try {
			newUriParsable = URI.create(mPreferences.getString("uri",
					"defaultString"));

			// newUriParsable = data.getParcelableExtra(uri);
			Log.i("New Uri from ContactsBook Parsable: ",
					newUriParsable.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("New Uri from ContactsBook: ", newUri.toString());

		String vCardString = MyVcardBuilder.createVCardString(
				getActivity(), newUri);
		String tempVcardString = null;

		if (vCardString != null && newUri != null) {

			Log.d("TAG", "file not null");
			tempVcardString = vCardString;
			tempUri = Uri.fromFile(vCardFile);

			if (isSmsForHTCone()) {
				sendMMStoHTCOne(destination_number.getText().toString(),
						tempVcardString, phoneDisplay, VCARDTYPE);

			} else {
				sendVcardMMS(destination_number.getText().toString(),
						tempVcardString, phoneDisplay, tempUri, VCARDTYPE);
			}
			// sendMMStoHTCOne(destination_number.getText().toString(),
			// tempVcardString, phoneDisplay,VCARDTYPE);
			// sendVcardMMS(destination_number.getText().toString(),
			// tempVcardString, phoneDisplay,VCARDTYPE);
		}

	}

	public void sendMessageForEditedInfo() {

		if (!destination_number.getText().toString().matches("")) {

			if (destination_number.getText().toString().startsWith("7")
					|| destination_number.getText().toString().startsWith("+1")
					|| destination_number.getText().toString().startsWith("4")
					|| destination_number.getText().toString()
							.startsWith("+14")
					|| destination_number.getText().toString()
							.startsWith("+182")
					|| destination_number.getText().toString()
							.startsWith("+17")) {

				// For Edited Contact info
				addedTextEdited = myPhoneNumber + "\t"
						+ "has sent you this information:" + "\n" + "\n"
						+ "Name : " + edited_name.getText().toString() + "\n"
						+ "Number : " + edited_number.getText().toString()
						+ "\n" + "\n" + "Location : "
						+ edited_address.getText().toString() + "\n" + "\n"
						+ "Thank you";
				mSendIntent = new Intent(SMS_SEND_ACTION);
				mDeliveryIntent = new Intent(SMS_DELIVERY_ACTION);
				SmsManager sms = SmsManager.getDefault();
				ArrayList<String> parts = sms.divideMessage(addedTextEdited);
				int numParts = parts.size();

				ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
				ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

				for (int i = 0; i < numParts; i++) {
					sentIntents
							.add(PendingIntent.getBroadcast(
									getActivity(), 0,
									mSendIntent, 0));
					deliveryIntents.add(PendingIntent.getBroadcast(
							getActivity(), 0,
							mDeliveryIntent, 0));
				}
				Toast.makeText(getActivity(), "Message Sent",
						Toast.LENGTH_LONG).show();
				sms.sendMultipartTextMessage(destination_number.getText()
						.toString(), null, parts, sentIntents, deliveryIntents);
				Toast.makeText(getActivity(), "Message Sent",
						Toast.LENGTH_LONG).show();

			} else {

				Toast.makeText(context.getApplicationContext(),
						"Message not Sent, serice only available for US.",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

			if (destNumber.length() < 10) {

				Toast.makeText(getActivity(),
						"Please enter a valid destination number.",
						Toast.LENGTH_LONG).show();
				// destination_number.setText("");
			}
			if (destination_number.getText().toString().startsWith("1800")) {

				Toast.makeText(
						getActivity(),
						"Message not Sent, " + "\t"
								+ "1800 - Toll Free numbers not supported",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

		} else {

			Toast.makeText(getActivity(),
					"Message not Sent, Destination number is null.",
					Toast.LENGTH_LONG).show();

		}

	}

	private void sendMMStoHTCOne(String phone, String vCardString,
			String vCardName, String mediaType) {

		sendMMStoHTCOne(phone, vCardString, vCardName, mediaType, null);
	}

	@SuppressLint("NewApi")
	private void sendMMStoHTCOne(String phone, String vCardString,
			String vCardName, String mediaType, String msg) {

		String vcardmessage = myPhoneNumber + "\t"
				+ "has sent you this vcard with name:" + "\n" + "\n"
				+ contact_name + "\n" + "\n" + "Thank you";

		if (!destination_number.getText().toString().matches("")) {

			if (destination_number.getText().toString().startsWith("7")
					|| destination_number.getText().toString().startsWith("+1")
					|| destination_number.getText().toString().startsWith("4")
					|| destination_number.getText().toString()
							.startsWith("+14")
					|| destination_number.getText().toString()
							.startsWith("+182")
					|| destination_number.getText().toString()
							.startsWith("+17")) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At
																			// least
																			// KitKat
				{
					Intent sendIntent = new Intent(
							"com.htc.intent.action.LAUNCH_MSG_COMPOSE");
					sendIntent.setData(Uri.parse(vCardString));
					sendIntent.setType("text/x-vCard");
					sendIntent.putExtra(Intent.EXTRA_TEXT, vCardString);
					// sendIntent.putExtra("exit_on_sent", true);
					sendIntent.putExtra("sms_body", vcardmessage);
					sendIntent.putExtra("address", phone);
					sendIntent.putExtra("name", contact_name);
					startActivity(sendIntent);
				}

				else {

					Intent smsIntent = new Intent(
							"com.htc.intent.action.LAUNCH_MSG_COMPOSE");
					smsIntent.setType("text/x-vCard");
					smsIntent.putExtra(Intent.EXTRA_STREAM,
							Uri.parse(vCardString));
					/*
					 * Intent smsIntent = new
					 * Intent("com.htc.intent.action.LAUNCH_MSG_COMPOSE");
					 * smsIntent.setType("text/x-vCard");
					 */
					smsIntent.putExtra("address", phone);
					smsIntent.putExtra(Intent.EXTRA_TEXT, vCardString);
					smsIntent.putExtra("sms_body", vcardmessage);
					smsIntent.putExtra("name", contact_name);
					startActivity(smsIntent);

				}

			} else {

				Toast.makeText(getActivity(),
						"Message not Sent, serice only available for US.",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

			if (destNumber.length() < 10) {

				Toast.makeText(getActivity(),
						"Please enter a valid destination number.",
						Toast.LENGTH_LONG).show();
				// destination_number.setText("");
			}
			if (destination_number.getText().toString().startsWith("1800")) {

				Toast.makeText(
						getActivity(),
						"Message not Sent, " + "\t"
								+ "1800 - Toll Free numbers not supported",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

		} else {

			Toast.makeText(getActivity(),
					"Message not Sent, Destination number is null.",
					Toast.LENGTH_LONG).show();

		}

	}

	private void sendVcardMMS(String phone, String vCardString,
			String vCardName, Uri uri, String mediaType) {
		sendVcardMMS(phone, vCardString, vCardName, mediaType, uri, null);
	}

	@SuppressLint("NewApi")
	private void sendVcardMMS(String phone, String vCardString,
			String vCardName, String mediaType, Uri uri, String msg) {

		String vcardmessage = myPhoneNumber + "\t"
				+ "has sent you this vcard with name:" + "\n" + "\n"
				+ contact_name + "\n" + "\n" + "Thank you";

		if (!destination_number.getText().toString().matches("")) {

			if (destination_number.getText().toString().startsWith("7")
					|| destination_number.getText().toString().startsWith("+1")
					|| destination_number.getText().toString().startsWith("4")
					|| destination_number.getText().toString()
							.startsWith("+14")
					|| destination_number.getText().toString()
							.startsWith("+182")
					|| destination_number.getText().toString()
							.startsWith("+17")) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At
																			// least
																			// KitKat
				{

					String defaultSmsPackageName = Telephony.Sms
							.getDefaultSmsPackage(context
									.getApplicationContext()); // Need to change
																// the build to
																// API 19
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
					sendIntent.setType("vnd.android-dir/mms-sms");
					sendIntent.putExtra(Intent.EXTRA_TEXT, vCardString);
					sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
					sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendIntent.putExtra("address", phone);
					startActivity(sendIntent);

				}

				else {

					Intent smsIntent = new Intent(Intent.ACTION_VIEW);
					smsIntent.setType("vnd.android-dir/mms-sms");
					smsIntent.putExtra(Intent.EXTRA_STREAM,
							Uri.parse(vCardString));
					/*
					 * Intent smsIntent = new
					 * Intent("com.htc.intent.action.LAUNCH_MSG_COMPOSE");
					 * smsIntent.setType("text/x-vCard");
					 */
					smsIntent.putExtra(Intent.EXTRA_STREAM, uri);
					smsIntent.putExtra("address", phone);
					smsIntent.putExtra(Intent.EXTRA_TEXT, vCardString);
					smsIntent.putExtra("sms_body", vcardmessage);
					startActivity(smsIntent);

				}

			} else {

				Toast.makeText(getActivity(),
						"Message not Sent, serice only available for US.",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

			if (destNumber.length() < 10) {

				Toast.makeText(getActivity(),
						"Please enter a valid destination number.",
						Toast.LENGTH_LONG).show();
				// destination_number.setText("");
			}
			if (destination_number.getText().toString().startsWith("1800")) {

				Toast.makeText(
						getActivity(),
						"Message not Sent, " + "\t"
								+ "1800 - Toll Free numbers not supported",
						Toast.LENGTH_LONG).show();
				destination_number.setText("");

			}

		} else {

			Toast.makeText(getActivity(),
					"Message not Sent, Destination number is null.",
					Toast.LENGTH_LONG).show();

		}

	}

	private Boolean isSmsForHTCone() {

		String HTCModel = "HTC6500LVW";
		Boolean isHTCModel = true;
		String BuildAndManufacturer = Build.MODEL + " \t " + Build.MANUFACTURER;
		String matchBM = "HTC OneHTC";
		String build = "HTC One";
		String manufacturer = "HTC";
		if (Build.MODEL.matches(HTCModel)
				|| BuildAndManufacturer.matches(matchBM)
				|| Build.MODEL.matches(build)) {

			Log.i("HTC : ", "True");
			return isHTCModel;

		} else {

			Log.i("HTC : ", "False");
			return false;
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.select_contact_listfor_destination:

			SelectContactForDestination();

			break;

		case R.id.select_contact_list:

			SelectContactFromContactBook();

			break;

		case R.id.send_message_with_contact_selected:

			sendMessageForContactPhoneBook();
			break;

		case R.id.send_message_with_vcard:

			getActualVcard();
			break;

		case R.id.send_message_with_edited_information:

			sendMessageForEditedInfo();
			break;

		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		  mPreferencesForDest = getActivity().getSharedPreferences("dest", 0);
		  mPreferences = getActivity().getSharedPreferences("your_file_name", 0);
		  mPreferencesForDest.edit().remove("number").commit();
		  mPreferences.edit().remove("yourStringName").commit();
		  mPreferences.edit().remove("nameDisplay").commit();
		  mPreferences.edit().remove("vcard").commit();
	}
}
