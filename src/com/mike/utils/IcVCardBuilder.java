package com.mike.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.util.Log;

public class IcVCardBuilder {
	
//	BEGIN:VCARD
//	VERSION:2.1
//	N:Address
//	FN:Address
//	GEO:42.39680863709133,-71.27350330352783
//	X-VZW-NGM-LOC:location.jpg
//	URL:http://nbi.atlasbook.com/map/map?center=42.396813,-71.2735&zoom=15&type=r&place1=Address...99.Sylvan_20Rd..Waltham..MA.02451.USA..42_2e396808.-71_2e273503.0.0&pin1line1=99_20Sylvan_20Rd&pin1line2=Waltham_2c_20MA_2c_2002451_2cUSA&pin1Type=4
//	ADR;PREF;HOME:;;99 Sylvan Rd;Waltham;MA;02451;USA
//	END:VCARD
    private static String LOCATION_VCARD_TEMPLATE="BEGIN:VCARD\n" +
    		"VERSION:2.1\n" +
    		"N:Address\n" +
    		"FN:Address\n" +
    		"GEO:geotag\n" +
    		"X-VZW-NGM-LOC:location.jpg\n" +
    		"URL:mapUrl\n" +
//    		"ADR;PREF;HOME:;;99 Sylvan Rd;Waltham;MA;02451;USA
    		"ADR;PREF;HOME:;;locString\n" +
    		"END:VCARD";
    private static String LOCATION_VCARD_TEMPLATE2 = "BEGIN:VCARD\n" +
            "VERSION:2.1\n" +
            "N:;Location;Address;;;\n" +
            "FN:Location Address\n" +
            "GEO:geotag\n" +
            "URL:mapUrl\n" +
            "ADR;PREF;HOME:;;streetName;city;;state;area\n" +
            "LABEL;HOME:;fullStreet\n" +
            "X-VZW-NGM-LOC:location.jpeg\n" +
            "END:VCARD\n";
     
public static String createVCardString(Context context, Uri contactUri){
    	
    	int vCardType = VCardConfig.VCARD_TYPE_V21_GENERIC;
	    String vCardResult =null;
	    Log.i("Uri: ", contactUri.toString());
	    try {

	        VCardComposer composer = new VCardComposer(context,vCardType);
	        Map<String, List<ContentValues>> dataMap = extractContact(context,contactUri);
	        //create vCard representation
	        vCardResult= composer.buildVCard(dataMap);
	        /*vCardResult = vCardResult
                    .replace("BEGIN:VCARD","")
                    .replace("VERSION:2.1", "")
                    .replace("FN:", "Name:")
                    .replace("TEL;CELL", "Phone No:")
                    .replace("END:VCARD", "");*/
                    
	        
	        Log.d("FinalResultforHTCONE:", vCardResult);
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return vCardResult;
    	
    }
	/**
	 * 
	 * @param context
	 * @param vcardType should be one of following 
	 * VCardConfig.java
		    public static final int VERSION_21 = 0;
		    public static final int VERSION_30 = 1;
		    public static final int VERSION_40 = 2;	
     * @return
	 */


	static public Map<String, List<ContentValues>> extractContact(Context context, Uri contactData)
	{
        Map<String, List<ContentValues>> contactMap = new HashMap<String, List<ContentValues>>();
	
		Cursor c = context.getContentResolver().query(contactData, null, null, null, null);
		try{
			if (c.moveToFirst()) {
				addNameProperties(context, contactMap,c);
				addPhoneNumbers(context,contactMap, c);
				addEmailAddresses(context, contactMap, c);
				addWebSites(context, contactMap, c);
				addPostalAdress(context, contactMap, c);				
			}			
		}
		catch(Exception e ){
			//FIXME:
			e.printStackTrace();
		}
		finally
		{
			if (c!=null)
				c.close();
		}
        
        return contactMap;       
	}

	static private void addPhoneNumbers(Context context,Map<String, List<ContentValues>> dataMap, Cursor c) {
		String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
		Cursor c1 =null;
		List<ContentValues> phoneList =new ArrayList<ContentValues>();
		try{
			 c1 = context.getContentResolver().query(
						Data.CONTENT_URI,
						new String[] { Data._ID, Phone.NUMBER, Phone.TYPE,
								Phone.LABEL },
						Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
								+ Phone.CONTENT_ITEM_TYPE + "'",
						new String[] { String.valueOf(contactId) }, null);
			
				if(c1.moveToFirst())
				{
					addPhoneEntry(c1, phoneList);
					while (c1.moveToNext())
					{
						addPhoneEntry(c1, phoneList);
					}
					
				}
		}
		catch(Exception e )
		{
			//FIXME:
			e.printStackTrace();
		}
		finally
		{
			if(c1!=null)
			{
				c1.close();
			}
		}
		dataMap.put(Phone.CONTENT_ITEM_TYPE, phoneList);	
	}
	
	static private void addEmailAddresses(Context context, Map<String, List<ContentValues>> dataMap, Cursor cursor){
		String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		String [] tableColumns = new String[] { Data._ID, Email.ADDRESS, Email.TYPE, Email.LABEL };
		String whereClause = Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='" + Email.CONTENT_ITEM_TYPE + "'" ;
		String [] whereArgs =  new String[] { String.valueOf(contactId) };
		Cursor queryResult = null;
		List<ContentValues> emailList = new ArrayList<ContentValues>();
		try{
			queryResult = context.getContentResolver().query(Email.CONTENT_URI,
					tableColumns, whereClause, whereArgs, null);
			
			if(queryResult.getCount() !=0 ){
				while(queryResult.moveToNext()){
					addEmailEntry(queryResult, emailList);
				}	
			}			
		} 
		catch(Exception e){
			e.printStackTrace();
		} 
		finally{
			if(queryResult != null) queryResult.close();
		}		
		
		dataMap.put(Email.CONTENT_ITEM_TYPE, emailList);
	}
	
	
	static private void addPostalAdress(Context context, Map<String, List<ContentValues>> dataMap, Cursor cursor){
		String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		String [] tableColumns = new String[] { Data._ID, StructuredPostal.FORMATTED_ADDRESS, 
												StructuredPostal.TYPE, StructuredPostal.LABEL };
		String whereClause = Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='" + StructuredPostal.CONTENT_ITEM_TYPE + "'" ;
		String [] whereArgs =  new String[] { String.valueOf(contactId) };
		Cursor queryResult = null;
		List<ContentValues> addressList = new ArrayList<ContentValues>(); 
		try{
			queryResult = context.getContentResolver().query(Data.CONTENT_URI, tableColumns, whereClause, whereArgs, null);
			if(queryResult.getCount() !=0 ) {
				while(queryResult.moveToNext()){
				addFormattedAddress(queryResult, addressList);
				}
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(queryResult != null) queryResult.close();
		}
		
		dataMap.put(StructuredPostal.CONTENT_ITEM_TYPE, addressList);
	}

	static private void addWebSites(Context context, Map<String, List<ContentValues>> dataMap, Cursor cursor){
		
		String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		String [] tableColumns = new String[] { Data._ID, Website.URL, Website.TYPE, Website.LABEL };
		String whereClause = Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='" + Website.CONTENT_ITEM_TYPE + "'" ;
		String [] whereArgs =  new String[] { String.valueOf(contactId) };
		Cursor queryResult = null;
		List<ContentValues> webList = new ArrayList<ContentValues>();
		try{
			queryResult = context.getContentResolver().query(Data.CONTENT_URI , tableColumns, whereClause, whereArgs, null);
			
			if(queryResult.getCount() !=0 ){
				while(queryResult.moveToNext()){
					addWebSiteEntry(queryResult, webList);
				}	
			}			
		} 
		catch(Exception e){
			e.printStackTrace();
		} 
		finally{
			if(queryResult != null) queryResult.close();
		}		
		
		dataMap.put(Website.CONTENT_ITEM_TYPE, webList);
	}

	static private void addPhoneEntry(Cursor c1, List<ContentValues> phoneList) {
		String number = c1.getString(1);			
		String type = c1.getString(2);			
		String label = c1.getString(3);			
		ContentValues phoneEntry = new ContentValues();
		if (number!=null) phoneEntry.put(Phone.NUMBER, number);
		if (type!=null) phoneEntry.put(Phone.TYPE, type);
		if (label!=null) phoneEntry.put(Phone.LABEL, label);
		phoneList.add(phoneEntry);
	}
	
	static private void addEmailEntry(Cursor cursor, List<ContentValues> emailList) {
		
		String address = cursor.getString(1);			
		String type = cursor.getString(2);			
		String label = cursor.getString(3);
		ContentValues emailEntry = new ContentValues();
		if (address!=null) emailEntry.put(Email.ADDRESS, address);
		if (type!=null) emailEntry.put(Email.TYPE, type);
		if (label!=null) emailEntry.put(Email.LABEL, label);
		emailList.add(emailEntry);
		
	}

static private void addFormattedAddress(Cursor cursor, List<ContentValues> theAddress) { 
		
		String address = cursor.getString(1);	// col1 = formatted_address		
		String type = cursor.getString(2);		// col 2 = type	
		String label = cursor.getString(3);		// col 3 = label
		
		ContentValues postalEntry = new ContentValues();
		if (address!=null) postalEntry.put(StructuredPostal.FORMATTED_ADDRESS, address);
		if (type!=null) postalEntry.put(StructuredPostal.TYPE, type);
		if(label!=null) postalEntry.put(StructuredPostal.LABEL, label);
		
		
		theAddress.add(postalEntry);
		
	}

static private void addWebSiteEntry(Cursor cursor, List<ContentValues> webList) { 
	
	String url = cursor.getString(1);	// col1 = url	
	String type = cursor.getString(2);		// col 2 = type	
	String label = cursor.getString(3);		// col 3 = label
	
	ContentValues webEntry = new ContentValues();
	if (url!=null) webEntry.put(Website.URL, url);
	if (type!=null) webEntry.put(Website.TYPE, type);
	if (label!=null) webEntry.put(Website.LABEL, label);
	//if (type.equals("3")) webEntry.put(Website.LABEL, "OTHER");
	
	webList.add(webEntry);
	
}
/*	static private void addEmail(Map<String, List<ContentValues>> dataMap ,Cursor c) throws Exception{
		ContentValues emailValue = new ContentValues();
		String email =c.getString(c.getColumnIndex(Email.DATA));
		emailValue.put(Email.DATA, email);
		dataMap.put(Email.CONTENT_ITEM_TYPE, new ArrayList<ContentValues>(Arrays.asList(emailValue)));
	}*/
	
	static private void addNameProperties (Context context,
			Map<String, List<ContentValues>> contactMap, Cursor c1) throws Exception {
		String contactId = c1.getString(c1.getColumnIndex(ContactsContract.Contacts._ID));
		Cursor c=null;
		List<ContentValues> nameProperties =new ArrayList<ContentValues>();
		try{
			
			c = context.getContentResolver().query(
					Data.CONTENT_URI,        null,
			Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"	+ StructuredName.CONTENT_ITEM_TYPE + "'",
 	       new String[] { String.valueOf(contactId) }, null);


			if(c.moveToFirst())
			{
				String displayName =c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				addContentValue(StructuredName.DISPLAY_NAME,displayName, nameProperties);
				String familyName =c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
				addContentValue(StructuredName.FAMILY_NAME,familyName, nameProperties);
				String middleName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
				addContentValue(StructuredName.MIDDLE_NAME,middleName, nameProperties);
				String givenName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
				addContentValue(StructuredName.GIVEN_NAME,givenName, nameProperties);
				String prefix = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX));
				addContentValue(StructuredName.PREFIX,prefix, nameProperties);
				String suffix = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.SUFFIX));
				addContentValue(StructuredName.SUFFIX,suffix, nameProperties);

				
			}
		}
		catch(Exception e )
		{
			//FIXME:
			e.printStackTrace();
		}
		finally
		{
			if(c!=null)
			{
				c.close();
			}
		}
		contactMap.put(StructuredName.CONTENT_ITEM_TYPE, nameProperties);

	}

	static private void addContentValue(String structureName ,String value, List<ContentValues> valueList) {
		ContentValues dispValue = new ContentValues();
		dispValue.put(structureName, value);
		valueList.add(dispValue);
	}
}

