package com.sip.softphone.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sip.softphone.R;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.timerCountUpdate;
import com.sip.softphone.fragments.IncomingCallFragment;
import com.sip.softphone.fragments.OutgoingCallFragment;
import com.sip.softphone.recievers.BroadcastReceiverEvent;
import com.sip.softphone.sip.CodecPriority;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;

/**
 * Created by hari on 14/4/17.
 */

public class CallsActivity extends AppCompatActivity {

    public timerCountUpdate timerUpdate;
    private String accountId, callNumber, callerName;
    private int callId;
    private CountDownTimer countDownTimer;
    private BroadcastReceiverEvent sipEvents = new BroadcastReceiverEvent() {

        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
              //  Toast.makeText(CallsActivity.this, "Call Registered", Toast.LENGTH_SHORT).show();
            } else {
               // Toast.makeText(CallsActivity.this, " Call Unregistered" + registrationStateCode + "<-->" + registrationStateCode.swigValue(), Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onCallState(String accountID, int callID, pjsip_inv_state callStateCode, long connectTimestamp, boolean isLocalHold, boolean isLocalMute) {
            super.onCallState(accountID, callID, callStateCode, connectTimestamp, isLocalHold, isLocalMute);
            Log.e("onCallState", "onCallState===" + accountID + "===" + callID + "=====" + callStateCode.swigValue() + "=====" + callStateCode + "====" + isLocalHold + "====" + isLocalMute);

            switch (callStateCode.swigValue()) {
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    startTimer();
                    break;
                case 6:
                    Toast.makeText(CallsActivity.this, "Call Disconnected", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
            }
        }

        @Override
        public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {

            for (CodecPriority codec : codecPriorities) {
                Log.e("CCCodecPriority==>", "$" + codec + "<-->" + codec.getCodecName());
                if (codec.getCodecName().startsWith("G.711a")) {
                    codec.setPriority(CodecPriority.PRIORITY_MAX);
                    Log.e("CCIN CodecPriority==>", "$" + codec.getCodecName());
                } else {
                    Log.e("CCElse CodecPriority==>", "$" + codec.getCodecName());
                    codec.setPriority(CodecPriority.PRIORITY_DISABLED);
                }
            }
            SipServiceInteractor.setCodecPriorities(CallsActivity.this, codecPriorities);
        }

        @Override
        public void onOutgoingVideoCall(String accountID, int callID, String number) {
            super.onOutgoingVideoCall(accountID, callID, number);
        }
        /* @Override
        public void onOutgoingCall(String accountID, int callID, String number) {
            super.onOutgoingCall(accountID, callID, number);
            Log.e("CallonOutgoingCall", "callonOutgoingCall===" + accountID + "===" + callID + "=====" + number);

        }*/
    };

    public void setCustomObjectListener(timerCountUpdate listener) {
        this.timerUpdate = listener;
    }

    public timerCountUpdate getCustomListener() {
        return timerUpdate;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        if (getIntent().getExtras() != null) {

            accountId = getIntent().getExtras().getString(Const.PREFS_ACCOUNT_ID, "");
            callNumber = getIntent().getExtras().getString(Const.CALLNUMBER, "");
            callId = getIntent().getExtras().getInt(Const.CALLID, 0);
            callerName = getIntent().getExtras().getString(Const.CALLERNAME, "");
            if (getIntent().getExtras().getString(Const.CALLTYPE) != null) {
                loadFragment(getIntent().getExtras().getString(Const.CALLTYPE));
            }
        }
    }

    private void loadFragment(String callType) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        if (callType.equalsIgnoreCase("incoming")) {
            mFragmentTransaction.replace(R.id.content, new IncomingCallFragment().getInstance(accountId, callNumber, callId, callerName), "");
        } else {
            mFragmentTransaction.replace(R.id.content, new OutgoingCallFragment().getInstance(accountId, callNumber, callId), "");
        }
        mFragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sipEvents.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sipEvents.register(this);

    }

    public void startTimer() {

        countDownTimer = new CountDownTimer(Const.TIMELIMIT, Const.TIMEINTERVAL) {

            public void onTick(long millisUntilFinished) {
                Log.e("Timer count", "" + ((Const.TIMELIMIT - millisUntilFinished) / Const.TIMEINTERVAL));
                String count = "" + ((Const.TIMELIMIT - millisUntilFinished) / Const.TIMEINTERVAL);
                getCustomListener().onReceiveTimerCount(count);

            }

            public void onFinish() {
                Log.e("Timer count", "DONE");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
