package com.mike.vcardparsingupdate;

import com.mike.tutorial.HelpActivity;
import com.mike.vcardparsingapplication.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("NewApi")
public class TabActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public ViewPager viewPager;
	private AllPagesAdapter mAdapter;
	private ActionBar actionBar;
	private String[] tabs = { "Main View", "History" };
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.mainfragment_activity);
		
		//Initializing all stuff
        viewPager = (ViewPager)findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new AllPagesAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
      //Add the tabs here
        for(String tab_name:tabs){
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
        public void onPageSelected(int position){

                //on Page change, that particular page should be selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
                public void onPageScrolled(int arg0,float arg1,int arg2){

            }
            @Override
        public void onPageScrollStateChanged(int position){

            }

        });
		
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {

		viewPager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		
		viewPager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		
		switch (item.getItemId()) {
		
		case R.id.helpmenu:
			
			Intent helpIntent = new Intent(this, HelpActivity.class);
			helpIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(helpIntent);
			return true;
			

		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

}
