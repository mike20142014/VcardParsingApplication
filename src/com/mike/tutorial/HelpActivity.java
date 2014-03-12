package com.mike.tutorial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mike.vcardparsingapplication.R;

public class HelpActivity extends Activity implements OnClickListener {

	public Activity c;
	public Dialog d;
	public Button yes, no;

	static HelpActivity mActivity;
	ListView listview;

	yourAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help);

		TextView close = (TextView) findViewById(R.id.close_button);
		close.setOnClickListener(this);
		
		mActivity = this;

		listview = (ListView) findViewById(R.id.helpListView);
		mAdapter = new yourAdapter(this, new String[] { "Item1", "Item2",
				"Item3", "Item4" });

		listview.setAdapter(mAdapter);
	}
	
	public static HelpActivity getInstance(){
		   return   mActivity;
		 }

	class yourAdapter extends BaseAdapter {

		Context context;
		String[] data;

		private LayoutInflater inflater = null;

		public yourAdapter(Context context, String[] data) {

			this.context = context;
			this.data = data;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {

			return data.length;
		}

		@Override
		public Object getItem(int position) {

			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View vi = convertView;
			if (vi == null)
				vi = inflater.inflate(R.layout.row, null);

			if (position == 0) {

				vi = inflater.inflate(R.layout.help_row1, null);

			}
			if (position == 1) {

				vi = inflater.inflate(R.layout.help_row_2, null);

			}
			if (position == 2) {

				vi = inflater.inflate(R.layout.help_row_3, null);

			}
			if (position == 3) {

				vi = inflater.inflate(R.layout.help_row_4, null);

			}

			return vi;
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.close_button:
			
			/*Intent closeIntent = new Intent(HelpActivity.this,
					MainActivity.class);
			closeIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(closeIntent);*/
			finish();
			
			//HelpActivity.getInstance.finish();
			
			break;

		default:
			break;
		}
		
	}

}
