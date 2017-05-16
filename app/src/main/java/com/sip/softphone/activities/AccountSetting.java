package com.sip.softphone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sip.softphone.R;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.MediaStateListener;
import com.sip.softphone.common.RegisterStatusInterface;
import com.sip.softphone.common.Utils;
import com.sip.softphone.fragments.AccountAddFragment;
import com.sip.softphone.recievers.BroadcastReceiverEvent;
import com.sip.softphone.services.SipService;
import com.sip.softphone.sip.CodecPriority;
import com.sip.softphone.sip.SipCall;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;

/**
 * Created by hari on 6/4/17.
 */
public class AccountSetting extends AppCompatActivity  {

    public FragmentManager fragmentManager;
    public String registrationStatus;
    private ActionBar actionBar;
    private RegisterStatusInterface statusInterface;
    private BroadcastReceiverEvent event = new BroadcastReceiverEvent() {
        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
                //Toast.makeText(AccountSetting.this, "Registered ACCOUNT SETTINGS", Toast.LENGTH_SHORT).show();
                Utils.putBooleanInPrefs(AccountSetting.this, Const.ISREGISTERED, true);
                registrationStatus = "Registered";
                getRegStatus().getSipAccountStatus(registrationStatus);
            } else {
                Log.e("ELSE", "" + "Unregistered" + registrationStateCode + "<-->" + registrationStateCode.swigValue());

               // Toast.makeText(AccountSetting.this, "UN Registered ACCOUNT SETTINGS", Toast.LENGTH_SHORT).show();
                Utils.putBooleanInPrefs(AccountSetting.this, Const.ISREGISTERED, false);
                registrationStatus = "Failed";
                getRegStatus().getSipAccountStatus(registrationStatus);
            }
        }

        @Override
        public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {

            for (CodecPriority codec : codecPriorities) {
                Log.e("AACodecPriority==>", "$" + codec + "<-->" + codec.getCodecName());
                if (codec.getCodecName().startsWith("G.711a")) {
                    codec.setPriority(CodecPriority.PRIORITY_MAX);
                    Log.e("AAIN CodecPriority==>", "$" + codec.getCodecName());
                } else {
                    Log.e("AAElse CodecPriority==>", "$" + codec.getCodecName());
                    codec.setPriority(CodecPriority.PRIORITY_DISABLED);
                }
            }
            SipServiceInteractor.setCodecPriorities(AccountSetting.this, codecPriorities);
        }

        @Override
        public void onStackStatus(boolean started) {
                getRegStatus().getAccountRemoveStatus(started);
        }
    };

    public void setRegisterStatue(RegisterStatusInterface status) {
        statusInterface = status;

    }

    public RegisterStatusInterface getRegStatus() {
        return statusInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_account_settings);
        setActionBatTitle(getString(R.string.my_account));
        fragmentManager = getSupportFragmentManager();
        AccountAddFragment add = new AccountAddFragment();
        fragmentManager.beginTransaction().replace(R.id.content, add, add.getClass().getName()).addToBackStack(add.getClass().getName()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e("COUNT", "" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            Intent it = new Intent(AccountSetting.this, TabsMainActivity.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        event.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        event.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.accountmenu, menu);
        MenuItem rm = menu.findItem(R.id.removeAccount);
        rm.setVisible(false);
        return true;
    }

    public void setActionBatTitle(String title) {
        actionBar = getSupportActionBar();
        actionBar.setTitle("" + title);
    }

}
