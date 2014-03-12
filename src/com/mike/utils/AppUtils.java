package com.mike.utils;

import java.io.File;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.mike.vcardparsingapplication.ContactsBook.ItemAdapter;

public class AppUtils {

	private static String destination_number;

	private final static String TAG = "AppUtil";

	private static ItemAdapter mItemAdapter;
	private static ContentResolver cr;
	private static Cursor cursor;
	public static File DEFAULT_DIR;
	public final static String DEFAULT_DIR_STRING = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/interactivecalling";
	public final static String DEFAULT_LOG_DIR_STRING = DEFAULT_DIR_STRING
			+ "/logs";

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

	static {
		DEFAULT_DIR = new File(DEFAULT_DIR_STRING);

		if (!DEFAULT_DIR.exists()) {
			boolean succeed = DEFAULT_DIR.mkdir();
			if (!succeed) {
				System.out
						.println("Failed to create dir " + DEFAULT_DIR_STRING);
			}
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDither = false; // Disable Dithering mode
		opts.inPurgeable = true;

	}

}
