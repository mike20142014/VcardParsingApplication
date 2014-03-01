/*package com.mike.vcardparsingapplication;



import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.utils.AppUtils;
import com.mike.utils.IcVCardBuilder;

*//**
 * Created by elkintr on 1/21/14.
 *//*
public class ShareView extends FrameLayout {

    private static final int PICTURE = 1;
    private static final int CAMERA = 2;
    private static final int MESSAGE = 3;
    private static final int CONTACT = 4;
    private static final int LOCATION = 5;

    private LayoutInflater mInflater;
    private ViewGroup mAppsContainer;
    private List<AppObject> mApps;
    private Bus mBus;
    private InCallSession mSession;
    private static String vCardName;
	
    public ShareView(Context context) {
        super(context);
        init();
    }

    public ShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShareView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mBus = EventBus.getInstance();
        mInflater.inflate(R.layout.view_send, this);
        mAppsContainer = (ViewGroup) findViewById(R.id.share_icons);

        mApps = loadUserSettings(getContext());
        if(mApps.size() == 0) {
            mApps.add(new AppObject("Picture", null, R.drawable.ic_icon_pic, true, PICTURE));
            mApps.add(new AppObject("Camera", null, R.drawable.ic_icon_camera, true, CAMERA));
            mApps.add(new AppObject("Message", null, R.drawable.ic_icon_msg, true, MESSAGE));
            mApps.add(new AppObject("Contacts", null, R.drawable.ic_icon_contacts, true, CONTACT));
            mApps.add(new AppObject("Location", null, R.drawable.ic_icon_location, true, LOCATION));
        }

        for (AppObject app : mApps) {
            mAppsContainer.addView(getAppView(app));
        }
    }

    private List<AppObject> loadUserSettings(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String s = settings.getString(SettingsShareListFragment.PREFERENCE_NAME, null);
        List<AppObject> list;
        if(s != null) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<AppObject>>(){}.getType();
            list = gson.fromJson(s, collectionType);
        }
        else
            list = new ArrayList<AppObject>();
        return list;
    }

    @Subscribe
    public void getInCallSession(InCallSessionEvent event) {
        mSession = event.getSession();
    }

    private View getAppView(final AppObject app) {
        View frame = mInflater.inflate(R.layout.share_app_icon, this, false);
        final ImageView icon = (ImageView) frame.findViewById(android.R.id.icon);
        final TextView title = (TextView) frame.findViewById(android.R.id.text1);
        title.setText(app.getAppName());

        Drawable iconDrawable = this.getResources().getDrawable(app.getResourceId());
        icon.setImageDrawable(iconDrawable);
        icon.setContentDescription(app.getAppName());

        frame.setTag(app);
        frame.setId(app.getId());
        frame.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                processShareClick(app);
            }
        });
        return frame;
    }

    public void processShareClick(AppObject app) {
        if(AppUtils.isScreenLocked(getContext())) {
            AppUtils.startActivityToAndroidHomeScreen(getContext());
        }
        else {
            switch(app.getId()) {
                case MESSAGE:
                    mBus.post(new OpenQuickTextViewEvent());
                    break;
                case PICTURE:
                case CAMERA:
                case CONTACT:
                case LOCATION: {
                    String packageName = app.getAppName();
                    if(packageName != null) {
                        Intent intent = new Intent(getContext(), ShareAction.class);
                        intent.putExtra("action", app.getId());
                        intent.putExtra("phoneNumber", mSession.getInCallNumber());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                    }
                }
                break;
            }
        }
    }

    public static class ShareAction extends Activity {
        IcLogger Log = new IcLogger(ShareAction.class);
        private final static String TAG = "ShareAction";

        public final static int RESULT_PICK_CONTACT = 1;
        public final static int RESULT_CAMERA_IMAGE_CAPTURE = 2;
        public final static int RESULT_LOAD_IMAGE = 3;
        public static final int RESULT_LOCATION_VCARD = 5;
        final static String VCARDTYPE = "text/x-vcard";
        private Uri imageUri = null;
        private String phoneNumber = "";
        private Bus mBus;

        private Queue<Integer> actionQueue = new LinkedBlockingDeque<Integer>();
        private Set<Integer> actionHistory = new HashSet<Integer>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mBus = EventBus.getInstance();
            mBus.register(this);

            //Get info from the calling Intent
            Intent preIntent = getIntent();
            int action = preIntent.getIntExtra("action", -1);
            String outPhoneNumber = preIntent.getStringExtra("phoneNumber");
            if(outPhoneNumber != null)
                phoneNumber = outPhoneNumber;

            String processed = preIntent.getStringExtra("vcpProcessed");
            if(processed == null) {
                if(!actionHistory.contains(action)) {
                    actionQueue.add(action);
                    actionHistory.add(action);
                    preIntent.putExtra("vcpProcessed", "yes");
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            //Check whether we are able to perform action
            boolean isAllowed = NetworkUtils.isIcAllowedNetwork(this);
            boolean isConnected = NetworkUtils.isConnectingToInternet(this);
            if((!isConnected)||(!isAllowed)) {
                Toast.makeText(this, getResources().getString(R.string.service_not_allowed), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            Integer action = actionQueue.poll();

            //Perform the action
            try {
                switch(action) {
                    case PICTURE:
                        performPicture();
                        break;
                    case CAMERA:
                        performCamera();
                        break;
                    case CONTACT:
                        performContact();
                        break;
                    case LOCATION:
                        performLocation();
                        break;
                    default:
                        finish();
                        break;
                }
            }
            catch(Exception e) {
                Log.e("error: ", e.toString());
            }
        }

        private void performPicture() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }

        private void performCamera() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            long callTime = System.currentTimeMillis();
            String dir = AppUtils.getCameraDirectory();
            File file = new File(dir, callTime + ".jpg");
            imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, RESULT_CAMERA_IMAGE_CAPTURE);
        }

        private void performContact() {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, RESULT_PICK_CONTACT);
        }

        private void performLocation() {
            Intent intent = new Intent(this, MainMapView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, RESULT_LOCATION_VCARD);
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if(imageUri != null) {
                outState.putString("cameraImageUri", imageUri.toString());
            }
        }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            if(savedInstanceState.containsKey("cameraImageUri")) {
                imageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d("on result:", "onActivityResult:" + resultCode + " request:" + requestCode);
            //Request was successful
            if(resultCode == RESULT_OK) {
//                mBus.post(new ShareRequestEvent(AnalyticsStatEvent.UIActionShare.SHARE_CAMERA));

                switch(requestCode) {
                    case RESULT_PICK_CONTACT:
                        contactPicked(data);
                        mBus.post(new ShareRequestEvent(AnalyticsStatEvent.UIActionShare.SHARE_CONTACT));

                        break;
                    case RESULT_LOAD_IMAGE:
                        imagePicked(data);
                        mBus.post(new ShareRequestEvent(AnalyticsStatEvent.UIActionShare.SHARE_GALLERY));

                        break;
                    case RESULT_CAMERA_IMAGE_CAPTURE:
                        cameraImageCaptured(data);
                        mBus.post(new ShareRequestEvent(AnalyticsStatEvent.UIActionShare.SHARE_GALLERY));

                        break;
                    case RESULT_LOCATION_VCARD:
                        locationPicked(data);
                        mBus.post(new ShareRequestEvent(AnalyticsStatEvent.UIActionShare.SHARE_LOCATION));

                        break;
                }
            }
            //Request failed
            else {
//                mBus.post(new CancelRequestEvent(requestCode));
                trackFailedRequest(requestCode);
            }

            //Finish the activity since we have what we need
            finish();
        }

        private void trackFailedRequest(int requestCode) {

//            mBus.post(new CancelRequestEvent(requestCode));
            switch(requestCode) {
                case RESULT_PICK_CONTACT:
                    mBus.post(new CancelRequestEvent(AnalyticsStatEvent.UIActionCancel.CANCEL_CONTACT));
//                    InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionCancel.CANCEL_CONTACT);
                    break;
                case RESULT_LOAD_IMAGE:
                    mBus.post(new CancelRequestEvent(AnalyticsStatEvent.UIActionCancel.CANCEL_GALLERY));

//                    InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionCancel.CANCEL_GALLERY);
                    break;
                case RESULT_CAMERA_IMAGE_CAPTURE:
                    mBus.post(new CancelRequestEvent(AnalyticsStatEvent.UIActionCancel.CANCEL_PICTURE));

//                    InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionCancel.CANCEL_PICTURE);
                    break;
                case RESULT_LOCATION_VCARD:
                    mBus.post(new CancelRequestEvent(AnalyticsStatEvent.UIActionCancel.CANCEL_MAP));

//                    InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionCancel.CANCEL_MAP);
                    break;
            }
            Log.d(TAG, String.valueOf(requestCode));
        }

        private void locationPicked(Intent data) {
//            InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionShare.SHARE_LOCATION);
            Uri uris       = data.getParcelableExtra("uris");
            String address = data.getStringExtra("address");
            Double lat     = data.getDoubleExtra("lat",0);
            Double lon     = data.getDoubleExtra("lon",0);

            if(uris != null)
                Log.d("on result", uris.getPath());

            //Send Url for hangout on KitKat
            String defaultApp = AppUtils.getDefaultMsgApp(this);
            if(defaultApp != null
                    && ("com.google.android.talk".equals(defaultApp)
                        || "com.motorola.messaging".equals(defaultApp))) {
                String mapString =address+"\n"+ MapUrlParser.GOOGLE_MAP_HEADER2+lat+","+lon+")";
                AppUtils.sendQuickText(this, phoneNumber, mapString);
            }
            else if((uris != null) && (address != null)) {
                sendMMSWithAttachment(phoneNumber, uris, VCARDTYPE, address);
            }
        }

        private void cameraImageCaptured(Intent data) {
            Uri returnedUri = null;
//            InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionShare.SHARE_CAMERA);
            if(data != null) {
                returnedUri = data.getData();
            }
            if(returnedUri != null) {
                imageUri = returnedUri;
            }
            File imageFile = new File(imageUri.getPath());
            AppUtils.addPicToGallery(this, imageFile);
            sendMMSWithAttachment(phoneNumber, imageUri, "image/jpg");
        }

        private void imagePicked(Intent data) {
//            InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionShare.SHARE_GALLERY);
            if(data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    if(selectedImage != null)
                        cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if(cursor != null)
                        cursor.moveToFirst();
                    sendMMSWithAttachment(phoneNumber, selectedImage, "image/jpg"); //TODO: what is the point of the cursor if it's not used?
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if(cursor != null)
                        cursor.close();
                }
            }
        }

        private void contactPicked(Intent data) {
        	
        	
//          InCallAnalyticsData.getInstance().trackAnalyticsData(AnalyticsStatEvent.UIActionShare.SHARE_CONTACT);
            Uri contactData = data.getData();
            File vCardFile = IcVCardBuilder.createVCard(this, contactData);
            String vCardString = IcVCardBuilder.createVCardString(this, contactData);
            Uri tempUri = null;
            String tempVcardString = null;
            Cursor cursor = getContentResolver().query(contactData, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
					+ " COLLATE LOCALIZED ASC");
            if(cursor.moveToNext()){
            	
            	vCardName = cursor
    					.getString(cursor
    							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            	Log.i("Vcard Name:", vCardName);
            }
            cursor.close();
            if(vCardFile != null||vCardString!=null) {
                boolean exists = vCardFile.exists();
                if(exists)
                    Log.d(TAG, "file not null");
                tempVcardString = vCardString;
                tempUri = Uri.fromFile(vCardFile);
                if(tempUri != null){ 
                    Log.d(TAG, "temp is null");
                    sendMMSWithAttachment(phoneNumber, tempUri, VCARDTYPE);
                }
               
                if(isSmsForHTCone()){
                	sendMMStoHTCOne(phoneNumber, tempUri, tempVcardString, vCardName, VCARDTYPE);
                	
                }else{
                	sendMMSWithAttachment(phoneNumber, tempUri, VCARDTYPE);
                }
                	
            }
            
            
        }
        
        
        private void sendMMSWithAttachment(String phone, Uri imageUri, String mediaType) {
            sendMMSWithAttachment(phone, imageUri, mediaType, null);
        }
        private void sendMMStoHTCOne(String phone,Uri uri, String vCardString,String vCardName, String mediaType){
        	
        	sendMMStoHTCOne(phone,uri, vCardString,vCardName, mediaType,null);
        }

        @SuppressLint("NewApi")
        private void sendMMSWithAttachment(String phone, Uri uri, String mediaType, String msg) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            int version = Build.VERSION.SDK_INT;
            sendIntent.setType(mediaType);
            if(version>=Build.VERSION_CODES.KITKAT) {
                String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
                sendIntent.setPackage(defaultSmsApp);

            }
            else {
                List<ResolveInfo> mmsApps = AppUtils.findAvailableMessagingApps(this, Intent.ACTION_VIEW, "vnd.android-dir/mms-sms");
                if((mmsApps != null) && (mmsApps.size() > 0)) {
                    String packageName = mmsApps.get(0).activityInfo.packageName;
                    String appName = mmsApps.get(0).activityInfo.name;
                    sendIntent.setClassName(packageName, appName);
                }
            }

            if(uri != null)
                Log.d("AppUtils", uri.getPath());
            else
                Log.d("AppUtils", "uri null");

            sendIntent.putExtra("exit_on_sent", true);
            sendIntent.putExtra("address", phone);
            if(msg != null)
                sendIntent.putExtra("sms_body", msg);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            try {
                startActivity(sendIntent);
            }
            catch(ActivityNotFoundException e) {
                Toast.makeText(this, getString(R.string.error_mms), Toast.LENGTH_LONG).show();
            }
        }
        
        @SuppressLint("NewApi")
        private void sendMMStoHTCOne(String phone,Uri uri, String vCardString,String vCardName, String mediaType, String msg) {
        	
        	Intent  sendIntent = new Intent("com.htc.intent.action.LAUNCH_MSG_COMPOSE");
        	sendIntent.setData(Uri.parse(vCardString));
        	sendIntent.setType("text/x-vCard");
        	sendIntent.putExtra(Intent.EXTRA_TEXT, vCardString);
        	sendIntent.putExtra("address", phone);
        	sendIntent.putExtra("name", vCardName);
        	startActivity(sendIntent);
        	}
        
        private Boolean isSmsForHTCone(){
        	
        	String HTCModel = "HTC6500LVW";
        	Boolean isHTCModel = true;
        	
        	if(Build.MODEL==HTCModel){
        		
        		return isHTCModel;
        		
        	}
        	else{
        		
        	 return false;
        	}
        	
        
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            mBus.unregister(this);
        }
    }
}
*/