package com.sip.softphone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.sip.softphone.fragments.CallLogFragment;
import com.sip.softphone.fragments.DialerFragment;

/**
 * Created by hari on 7/4/17.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private String accountId;

    public PageAdapter(FragmentManager fm, int NumOfTabs, String loggedAccountId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.accountId = loggedAccountId;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Log.e("In ADAPTER", "===" + accountId);
                DialerFragment tab1 = DialerFragment.getInstance(accountId);
                return tab1;
            case 1:
                CallLogFragment tab2 = new CallLogFragment();
                return tab2;


            // AccountRegisterFragment tab2 = new AccountRegisterFragment();
            //return tab2;
            case 2:
                // TabFragment3 tab3 = new TabFragment3();
                // return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
