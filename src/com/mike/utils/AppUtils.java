package com.mike.utils;

import java.util.regex.Pattern;

import com.mike.vcardparsingapplication.ContactsBook;
import com.mike.vcardparsingapplication.ContactsBook.ItemAdapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class AppUtils {

	private static String destination_number;
	private String contact_list_phonenumber;
	private String contact_list_name;
	private String contact_list_address;
	private String edited_name;
	private String edited_number;
	private String edited_address;
	private final static String TAG = "AppUtil";
	private Context context;
	private static ContactsBook contactsBook;
	private static ItemAdapter mItemAdapter;
	private static ContentResolver cr;
	private static Cursor cursor;

	public static String setDestinationNumber() {

		cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, null, null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");

		destination_number = mItemAdapter
				.getCursor()
				.getString(
						cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		Log.i(TAG, destination_number);
		return destination_number;
	}

}
