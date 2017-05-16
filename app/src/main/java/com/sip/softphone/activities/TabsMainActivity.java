package com.sip.softphone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sip.softphone.R;
import com.sip.softphone.adapter.PageAdapter;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.Utils;
import com.sip.softphone.recievers.BroadcastReceiverEvent;
import com.sip.softphone.sip.CodecPriority;
import com.sip.softphone.sip.SipAccountData;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;


/**
 * Created by hari on 7/4/17.
 */

public class TabsMainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private PageAdapter adapter;
    private ViewPager viewPager;
    private SipAccountData loggedSipAccount;
    private String loggedAccountId;

    private BroadcastReceiverEvent sipEvents = new BroadcastReceiverEvent() {

        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
               // Toast.makeText(TabsMainActivity.this, "TABBBBB Registered", Toast.LENGTH_SHORT).show();
            } else {

                Log.e("VALUEEEEEE", "" + "Unregistered" + registrationStateCode + "<-->" + registrationStateCode.swigValue());
               // Toast.makeText(TabsMainActivity.this, "Unregistered" + registrationStateCode + "<-->" + registrationStateCode.swigValue(), Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onOutgoingCall(String accountID, int callID, String number) {
            super.onOutgoingCall(accountID, callID, number);
            Log.e("onOutgoingCall", "onOutgoingCall===" + accountID + "===" + number + callID);

            Intent it = new Intent(TabsMainActivity.this, CallsActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putString(Const.CALLTYPE, "outgoing");
            bundle.putString(Const.PREFS_ACCOUNT_ID, accountID);
            bundle.putInt(Const.CALLID, callID);
            bundle.putString(Const.CALLNUMBER, number);
            it.putExtras(bundle);
            startActivity(it);
        }

        @Override
        public void onOutgoingVideoCall(String accountID, int callID, String number) {
            super.onOutgoingVideoCall(accountID, callID, number);

            Log.e("onOutgoingCall", "onOutgoingCall===" + accountID + "===" + number + callID);

        }

        @Override
        public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {

            for (CodecPriority codec : codecPriorities) {
                Log.e("TTCodecPriority===>", "$" + codec + "<-->" + codec.getCodecName());
                if (codec.getCodecName().startsWith("G.711a")) {
                    codec.setPriority(CodecPriority.PRIORITY_MAX);
                    Log.e("TTIN CodecPriority===>", "$" + codec.getCodecName());
                } else {
                    Log.e("TTEl CodecPriority===>", "$" + codec.getCodecName());
                    codec.setPriority(CodecPriority.PRIORITY_DISABLED);
                }
            }
            SipServiceInteractor.setCodecPriorities(TabsMainActivity.this, codecPriorities);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Dialer"));
        tabLayout.addTab(tabLayout.newTab().setText("Call Log"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PageAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), loggedAccountId);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.phoneCall:

                                break;
                            case R.id.settings:

                                loadFragment();

                                break;
                            case R.id.messaging:
                                Intent it = new Intent(TabsMainActivity.this, AVCallsActivity.class);
                                startActivity(it);
                                finish();
                                break;
                        }
                        return false;
                    }
                });
    }

    private void loadFragment() {

        Intent it = new Intent(this, AccountSetting.class);
        startActivity(it);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sipEvents.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Onresume", "onresume===========");
        sipEvents.register(this);
        if (Utils.getObjectInPrefs(this) != null) {
            loggedSipAccount = Utils.getObjectInPrefs(this);
            Log.d("AccountDetails", "" + loggedSipAccount.getIdUri() + "-" + loggedSipAccount.getUsername() + "-" + loggedSipAccount.getRegistrarUri());
        }
        Log.e("onCreateView", "========");
        if (loggedSipAccount != null && loggedSipAccount.getRegistrarUri().length() > 0) {
            loggedAccountId = loggedSipAccount.getIdUri();
            // SipServiceInteractor.setAccount(this,loggedSipAccount);
        }


    }

}
