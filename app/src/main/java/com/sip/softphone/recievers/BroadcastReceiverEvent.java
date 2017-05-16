package com.sip.softphone.recievers;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.sip.softphone.activities.AccountSetting;
import com.sip.softphone.activities.CallsActivity;
import com.sip.softphone.common.Const;
import com.sip.softphone.sip.CodecPriority;

import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;

/**
 * Created by hari on 11/4/17.
 */

public class BroadcastReceiverEvent extends BroadcastReceiver {

    private Context receiverContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        //save context internally for convenience in subclasses, which can get it with
        //getReceiverContext method
        receiverContext = context;
        Log.e("INTENT ACTION ON",""+intent.getAction());

        String action = intent.getAction();

        if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.REGISTRATION))) {
            int stateCode = intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.CODE, -1);
            onRegistration(intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID),
                    pjsip_status_code.swigToEnum(stateCode));

        } else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.INCOMING_CALL))) {
            onIncomingCall(intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID),
                    intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.CALL_ID, -1),
                    intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.DISPLAY_NAME),
                    intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.REMOTE_URI));

        } else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.CALL_STATE))) {
            int callState = intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.CALL_STATE, -1);
            onCallState(intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID),
                    intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.CALL_ID, -1),
                    pjsip_inv_state.swigToEnum(callState),
                    intent.getLongExtra(BroadcastEventTrigger.BroadcastParameters.CONNECT_TIMESTAMP, -1),
                    intent.getBooleanExtra(BroadcastEventTrigger.BroadcastParameters.LOCAL_HOLD, false),
                    intent.getBooleanExtra(BroadcastEventTrigger.BroadcastParameters.LOCAL_MUTE, false));

        } else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.OUTGOING_CALL))) {
            onOutgoingCall(intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID),
                    intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.CALL_ID, -1),
                    intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.NUMBER));

        } else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.STACK_STATUS))) {
            onStackStatus(intent.getBooleanExtra(BroadcastEventTrigger.BroadcastParameters.STACK_STARTED, false));

        } else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.CODEC_PRIORITIES))) {
            ArrayList<CodecPriority> codecList = intent.getParcelableArrayListExtra(BroadcastEventTrigger.BroadcastParameters.CODEC_PRIORITIES_LIST);
            onReceivedCodecPriorities(codecList);

        } else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.CODEC_PRIORITIES_SET_STATUS))) {
            onCodecPrioritiesSetStatus(intent.getBooleanExtra(BroadcastEventTrigger.BroadcastParameters.SUCCESS, false));

        }else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.SERVICE_STOP))) {

            Log.e("%%%%%%%%%%%%%%","****************");
           Intent it = new Intent(receiverContext, AccountSetting.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            receiverContext.getApplicationContext().startActivity(it);
        }else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.REMOVED_ACCOUNT))){
            onAccountRemoved();
        }else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.OUTGOING_VIDEO_CALL))){
            onOutgoingVideoCall(intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID),
                    intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.CALL_ID, -1),
                    intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.NUMBER));
        }else if (action.equals(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.VIDEO_MEDIA_CALL))){
            onVideoMediaState(intent.getStringExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID),
                    intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.VIDEO_WINDOW, -1),
                    intent.getIntExtra(BroadcastEventTrigger.BroadcastParameters.VIDEO_PREVIEW,-1));
        }
    }

    public void onVideoMediaState(String accountID, int videoWindow, int videoPreview) {

        Log.d(Const.TAG, "onVideoMediaState - accountID: " + accountID +
                ", Video Window: " + videoWindow +
                ", number: " + videoPreview);
    }

    public void onOutgoingVideoCall(String accountID, int callID, String number) {


        Log.d(Const.TAG, "onOutgoingCall - accountID: " + accountID +
                ", callID: " + callID +
                ", number: " + number);
    }

    public void onAccountRemoved() {
        Log.d(Const.TAG, "onAccountRemoved - Success:");
    }

    public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
        Log.d(Const.TAG, "onRegistration - accountID: " + accountID +
                ", registrationStateCode: " + registrationStateCode);
    }

    public void onIncomingCall(String accountID, int callID, String displayName, String remoteUri) {
        Log.d(Const.TAG, "onIncomingCall - accountID: " + accountID +
                ", callID: " + callID +
                ", displayName: " + displayName +
                ", remoteUri: " + remoteUri);
        wakeScreenLock();
        Intent intent = new Intent(receiverContext, CallsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CALLTYPE,"incoming");
        bundle.putString(Const.CALLERNAME,displayName);
        bundle.putString(Const.PREFS_ACCOUNT_ID,accountID);
        bundle.putString(Const.REMOTEURI,remoteUri);
        bundle.putInt(Const.CALLID,callID);
        intent.putExtras(bundle);
        receiverContext.getApplicationContext().startActivity(intent);
    }

    public void wakeScreenLock() {

        KeyguardManager.KeyguardLock lock = ((KeyguardManager) receiverContext.getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock("1");
        PowerManager powerManager = ((PowerManager) receiverContext.getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

        lock.disableKeyguard();
        wake.acquire();
        /*receiverContext.getApplicationContext().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)*/
    }

    public void onCallState(String accountID, int callID, pjsip_inv_state callStateCode,
                            long connectTimestamp, boolean isLocalHold, boolean isLocalMute) {
        Log.d(Const.TAG, "onCallState - accountID: " + accountID +
                ", callID: " + callID +
                ", callStateCode: " + callStateCode +
                ", connectTimestamp: " + connectTimestamp +
                ", isLocalHold: " + isLocalHold +
                ", isLocalMute: " + isLocalMute);

    }

    public void onOutgoingCall(String accountID, int callID, String number) {


        Log.d(Const.TAG, "onOutgoingCall - accountID: " + accountID +
                ", callID: " + callID +
                ", number: " + number);
        /*Intent it = new Intent(receiverContext,CallsActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(Const.CALLTYPE,"outgoing");
        bundle.putString(Const.PREFS_ACCOUNT_ID,accountID);
        bundle.putInt(Const.CALLID,callID);
        bundle.putString(Const.CALLNUMBER,number);
        it.putExtras(bundle);
        receiverContext.getApplicationContext().startActivity(it);*/

    }

    public void onStackStatus(boolean started) {
        Log.d(Const.TAG, "SIP service stack " + (started ? "started" : "stopped"));
    }

    public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
        Log.d(Const.TAG, "Received codec priorities");
        for (CodecPriority codec : codecPriorities) {
            Log.d(Const.TAG, codec.toString());
        }
    }

    public void onCodecPrioritiesSetStatus(boolean success) {
        Log.d(Const.TAG, "Codec priorities " + (success ? "successfully set" : "set error"));
    }

    protected Context getReceiverContext() {
        return receiverContext;
    }
    /**
     * Register this broadcast receiver.
     * It's recommended to register the receiver in Activity's onResume method.
     *
     * @param context context in which to register this receiver
     */
    public void register(final Context context) {

        Log.e("INNNN","===========");
        Log.e("INNNN","===========");

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.REGISTRATION));
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.INCOMING_CALL));
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.CALL_STATE));
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.OUTGOING_CALL));
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.STACK_STATUS));
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.CODEC_PRIORITIES));
        intentFilter.addAction(BroadcastEventTrigger.getAction(BroadcastEventTrigger.BroadcastAction.CODEC_PRIORITIES_SET_STATUS));
        context.registerReceiver(this, intentFilter);
    }

    /**
     * Unregister this broadcast receiver.
     * It's recommended to unregister the receiver in Activity's onPause method.
     *
     * @param context context in which to unregister this receiver
     */
    public void unregister(final Context context) {
        context.unregisterReceiver(this);
    }
}
