package com.mike.share;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mike.utils.IcVCardBuilder;


@SuppressLint("NewApi")
public class ShareView extends FrameLayout {

	private static final int PICTURE = 1;

	private static final int CONTACT = 4;

	private static String vCardName;

	
	public ShareView(Context context) {
		super(context);

	}

	public ShareView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public ShareView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public static class ShareAction extends Activity {

		private final static String TAG = "ShareAction";

		public final static int RESULT_PICK_CONTACT = 1;
		public final static int RESULT_CAMERA_IMAGE_CAPTURE = 2;
		public final static int RESULT_LOAD_IMAGE = 3;
		public static final int RESULT_LOCATION_VCARD = 5;
		final static String VCARDTYPE = "text/x-vcard";

		private String phoneNumber = "";

		private Queue<Integer> actionQueue = new LinkedBlockingDeque<Integer>();
		private Set<Integer> actionHistory = new HashSet<Integer>();

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Get info from the calling Intent
			Intent preIntent = getIntent();
			int action = preIntent.getIntExtra("action", -1);
			String outPhoneNumber = preIntent.getStringExtra("phoneNumber");
			if (outPhoneNumber != null)
				phoneNumber = outPhoneNumber;

			String processed = preIntent.getStringExtra("vcpProcessed");
			if (processed == null) {
				if (!actionHistory.contains(action)) {
					actionQueue.add(action);
					actionHistory.add(action);
					preIntent.putExtra("vcpProcessed", "yes");
				}
			}
		}

		@Override
		public void onResume() {
			super.onResume();

			Integer action = actionQueue.poll();

			// Perform the action
			try {
				switch (action) {
				case PICTURE:
					performPicture();
					break;

				case CONTACT:
					performContact();
					break;

				default:
					finish();
					break;
				}
			} catch (Exception e) {
				Log.e("error: ", e.toString());
			}
		}

		private void performPicture() {
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(intent, RESULT_LOAD_IMAGE);
		}

		private void performContact() {
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(intent, RESULT_PICK_CONTACT);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			Log.d("on result:", "onActivityResult:" + resultCode + " request:"
					+ requestCode);
			// Request was successful
			if (resultCode == RESULT_OK) {
				// mBus.post(new
				// ShareRequestEvent(AnalyticsStatEvent.UIActionShare.SHARE_CAMERA));

				switch (requestCode) {
				case RESULT_PICK_CONTACT:
					contactPicked(data);

					break;

				}
			}

			// Finish the activity since we have what we need
			finish();
		}

		private void contactPicked(Intent data) {

			// InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionShare.SHARE_CONTACT);
			Uri contactData = data.getData();

			String vCardString = IcVCardBuilder.createVCardString(this,
					contactData);
			Uri tempUri = null;
			String tempVcardString = null;
			Cursor cursor = getContentResolver().query(
					contactData,
					null,
					null,
					null,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
							+ " COLLATE LOCALIZED ASC");
			if (cursor.moveToNext()) {

				vCardName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				Log.i("Vcard Name:", vCardName);
			}
			cursor.close();
			if (vCardString != null) {

				tempVcardString = vCardString;

				Log.d(TAG, "temp is null");
				sendMMStoHTCOne(tempVcardString, tempUri, phoneNumber,
						vCardString, VCARDTYPE);

			}

		}

		private void sendMMSWithAttachment(String phone, Uri imageUri,
				String mediaType) {
			sendMMSWithAttachment(phone, imageUri, mediaType, null);
		}

		private void sendMMStoHTCOne(String phone, Uri uri, String vCardString,
				String vCardName, String mediaType) {

			sendMMStoHTCOne(phone, uri, vCardString, vCardName, mediaType, null);
		}

		@SuppressLint("NewApi")
		private void sendMMSWithAttachment(String phone, Uri uri,
				String mediaType, String msg) {
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			int version = Build.VERSION.SDK_INT;
			sendIntent.setType(mediaType);
			if (version >= Build.VERSION_CODES.KITKAT) {
				String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
				sendIntent.setPackage(defaultSmsApp);

			}

			if (uri != null)
				Log.d("AppUtils", uri.getPath());
			else
				Log.d("AppUtils", "uri null");

			sendIntent.putExtra("exit_on_sent", true);
			sendIntent.putExtra("address", phone);
			if (msg != null)
				sendIntent.putExtra("sms_body", msg);
			sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
			try {
				startActivity(sendIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
			}
		}

		@SuppressLint("NewApi")
		private void sendMMStoHTCOne(String phone, Uri uri, String vCardString,
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

		private Boolean isSmsForHTCone() {

			String HTCModel = "HTC6500LVW";
			Boolean isHTCModel = true;

			if (Build.MODEL == HTCModel) {

				return isHTCModel;

			} else {

				return false;
			}

		}

	}
}
