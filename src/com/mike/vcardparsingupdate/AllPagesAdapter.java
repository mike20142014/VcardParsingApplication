package com.mike.vcardparsingupdate;

/**
 * Created by MichaelHenry on 11/2/13.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mike.pages.HistoryFragment;
import com.mike.pages.MainFragment;

public class AllPagesAdapter extends FragmentStatePagerAdapter {

            public AllPagesAdapter(FragmentManager fm) {

                super(fm);

            }

            @Override
            public Fragment getItem(int index) {

        switch (index) {
            case 0:

                return new MainFragment();

            case 1:

                return new HistoryFragment();
            
        }
        return null;


    }

    @Override
    public int getCount() {
        return 2;
    }

}