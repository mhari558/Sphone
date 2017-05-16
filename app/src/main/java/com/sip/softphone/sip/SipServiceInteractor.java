package com.sip.softphone.sip;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.sip.softphone.BuildConfig;
import com.sip.softphone.activities.AVCallsActivity;
import com.sip.softphone.common.Const;
import com.sip.softphone.fragments.AccountRegisterFragment;
import com.sip.softphone.services.SipService;

import java.util.ArrayList;

/**
 * Created by hari on 10/4/17.
 */

public class SipServiceInteractor {

    public static String AGENT_NAME = "AndroidSipService/" + BuildConfig.VERSION_CODE;


    public SipServiceInteractor(AccountRegisterFragment accountRegisterFragment, SipAccountData mSipAccount) {


    }

    /**
     * Adds a new SIP account.
     *
     * @param context    application context
     * @param sipAccount sip account data
     * @return sip account ID uri as a string
     */
    public static String setAccount(Context context, SipAccountData sipAccount) {
        if (sipAccount == null) {
            throw new IllegalArgumentException("sipAccount MUST not be null!");
        }

        String accountID = sipAccount.getIdUri();
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_SET_ACCOUNT);
        intent.putExtra(Const.PARAM_ACCOUNT_DATA, sipAccount);
        context.startService(intent);

        return accountID;
    }

    /**
     * Requests the codec priorities. This is going to return results only if the sip stack has
     * been started, otherwise you will see an error message in LogCat.
     *
     * @param context application context
     */
    public static void getCodecPriorities(Context context) {
        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_GET_CODEC_PRIORITIES);
        context.startService(intent);
    }

    /**
     * Set codec priorities. this is going to work only if the sip stack has
     * been started, otherwise you will see an error message in LogCat.
     *
     * @param context         application context
     * @param codecPriorities list with the codec priorities to set
     */
    public static void setCodecPriorities(Context context, ArrayList<CodecPriority> codecPriorities) {
        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_SET_CODEC_PRIORITIES);
        intent.putParcelableArrayListExtra(Const.PARAM_CODEC_PRIORITIES, codecPriorities);
        context.startService(intent);
    }

    private static void checkAccount(String accountID) {
        Log.e("AccountID", "================" + accountID);
        if (accountID == null || accountID.isEmpty() || !accountID.startsWith("sip:")) {
            Log.e("Account INNNN ID", "================" + accountID);
            throw new IllegalArgumentException("Invalid accountID! Example: sip:user@domain");

        }
    }

    /**
     * Remove a SIP account.
     *
     * @param context   application context
     * @param accountID account ID uri
     */
    public static void removeAccount(Context context, String accountID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_REMOVE_ACCOUNT);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        context.startService(intent);
    }

    /**
     * Gets the registration status for an account.
     *
     * @param context   application context
     * @param accountID sip account data
     */
    public static void getRegistrationStatus(Context context, String accountID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_GET_REGISTRATION_STATUS);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        context.startService(intent);
    }

    /**
     * Makes a call.
     *
     * @param context      application context
     * @param accountID    account ID used to make the call
     * @param numberToCall number to call
     */
    public static void makeCall(Context context, String accountID, String numberToCall) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_MAKE_CALL);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_NUMBER, numberToCall);
        context.startService(intent);
    }

    /**
     * Accept an incoming call. If the call does not exist or has been terminated, a disconnected
     * state will be sent to
     * {@link
     *
     * @param context   application context
     * @param accountID account ID
     * @param callID    call ID to hang up
     */
    public static void acceptIncomingCall(Context context, String accountID, int callID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_ACCEPT_INCOMING_CALL);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_CALL_ID, callID);
        context.startService(intent);
    }

    /**
     * Decline an incoming call. If the call does not exist or has been terminated, a disconnected
     * state will be sent to
     * {@link //com.sip.softphone.recievers.BroadcastEventTrigger#onCallState(String, int, pjsip_inv_state, long, boolean, boolean)}
     *
     * @param context   application context
     * @param accountID account ID
     * @param callID    call ID to hang up
     */
    public static void declineIncomingCall(Context context, String accountID, int callID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_DECLINE_INCOMING_CALL);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_CALL_ID, callID);
        context.startService(intent);
    }

    /**
     * Hangs up an active call. If the call does not exist or has been terminated, a disconnected
     * state will be sent to
     * {@link //BroadcastEventReceiver#onCallState(String, int, pjsip_inv_state, long, boolean, boolean)}
     *
     * @param context   application context
     * @param accountID account ID
     * @param callID    call ID to hang up
     */
    public static void hangUpCall(Context context, String accountID, int callID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_HANG_UP_CALL);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_CALL_ID, callID);
        context.startService(intent);
    }

    /**
     * Checks the status of a call. You will receive the result in
     * {@link //BroadcastEventReceiver#onCallState(String, int, pjsip_inv_state, long, boolean, boolean)}
     *
     * @param context   application context
     * @param accountID account ID used to make the call
     * @param callID    call ID
     */
    public static void getCallStatus(Context context, String accountID, int callID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_GET_CALL_STATUS);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_CALL_ID, callID);
        context.startService(intent);
    }

    /**
     * Hangs up active calls.
     *
     * @param context   application context
     * @param accountID account ID
     */
    public static void hangUpActiveCalls(Context context, String accountID) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_HANG_UP_CALLS);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        context.startService(intent);
    }

    /**
     * Sets mute status for a call. If the call does not exist or has been terminated, a disconnected
     * state will be sent to
     * {@link //BroadcastEventReceiver#onCallState(String, int, pjsip_inv_state, long, boolean, boolean)}
     *
     * @param context   application context
     * @param accountID account ID
     * @param callID    call ID
     * @param mute      true to mute the call, false to un-mute it
     */
    public static void setCallMute(Context context, String accountID, int callID, boolean mute) {
        checkAccount(accountID);

        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_SET_MUTE);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_CALL_ID, callID);
        intent.putExtra(Const.PARAM_MUTE, mute);
        context.startService(intent);
    }


    public static void makeVideoCall(Context context, String accountID, String numberToCall) {
        checkAccount(accountID);
        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_VIDEO_CALL);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_NUMBER, numberToCall);
        context.startService(intent);

    }

    public static void setVideoPreview(Context context, String accountID,int callID) {
        checkAccount(accountID);
        Intent intent = new Intent(context, SipService.class);
        intent.setAction(Const.ACTION_SET_VIDEOPREVIEW);
        intent.putExtra(Const.PARAM_ACCOUNT_ID, accountID);
        intent.putExtra(Const.PARAM_CALL_ID, callID);
        context.startService(intent);
    }
}
