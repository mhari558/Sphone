package com.sip.softphone.recievers;

import android.content.Context;
import android.content.Intent;

import com.sip.softphone.sip.CodecPriority;

import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoWindow;

import java.util.ArrayList;

/**
 * Created by hari on 10/4/17.
 */

public class BroadcastEventTrigger {

    public static String NAMESPACE = "com.sip.softphone";

    private Context mContext;

    public BroadcastEventTrigger(Context context) {
        mContext = context;
    }

    public static String getAction(BroadcastAction action) {
        return NAMESPACE + "." + action;
    }

    public void codecPriorities(ArrayList<CodecPriority> codecPriorities) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.CODEC_PRIORITIES));
        intent.putParcelableArrayListExtra(BroadcastParameters.CODEC_PRIORITIES_LIST, codecPriorities);

        mContext.sendBroadcast(intent);
    }

    public void codecPrioritiesSetStatus(boolean success) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.CODEC_PRIORITIES_SET_STATUS));
        intent.putExtra(BroadcastParameters.SUCCESS, success);

        mContext.sendBroadcast(intent);
    }

    /**
     * Emit a registration state broadcast intent.
     *
     * @param accountID             account IdUri
     * @param registrationStateCode SIP registration status code
     */
    public void registrationState(String accountID, int registrationStateCode) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.REGISTRATION));
        intent.putExtra(BroadcastParameters.ACCOUNT_ID, accountID);
        intent.putExtra(BroadcastParameters.CODE, registrationStateCode);

        mContext.sendBroadcast(intent);
    }

    public void stackStatus(boolean started) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.STACK_STATUS));
        intent.putExtra(BroadcastParameters.STACK_STARTED, started);

        mContext.sendBroadcast(intent);
    }

    /**
     * Emit an incoming call broadcast intent.
     *
     * @param accountID   call's account IdUri
     * @param callID      call ID number
     * @param displayName the display name of the remote party
     * @param remoteUri   the IdUri of the remote party
     */
    public void incomingCall(String accountID, int callID, String displayName, String remoteUri) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.INCOMING_CALL));
        intent.putExtra(BroadcastParameters.ACCOUNT_ID, accountID);
        intent.putExtra(BroadcastParameters.CALL_ID, callID);
        intent.putExtra(BroadcastParameters.DISPLAY_NAME, displayName);
        intent.putExtra(BroadcastParameters.REMOTE_URI, remoteUri);

        mContext.sendBroadcast(intent);
    }

    /**
     * Emit a call state broadcast intent.
     *
     * @param accountID        call's account IdUri
     * @param callID           call ID number
     * @param callStateCode    SIP call state code
     * @param connectTimestamp call start timestamp
     * @param isLocalHold      true if the call is held locally
     * @param isLocalMute      true if the call is muted locally
     */
    public void callState(String accountID, int callID, int callStateCode, long connectTimestamp,
                          boolean isLocalHold, boolean isLocalMute) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.CALL_STATE));
        intent.putExtra(BroadcastParameters.ACCOUNT_ID, accountID);
        intent.putExtra(BroadcastParameters.CALL_ID, callID);
        intent.putExtra(BroadcastParameters.CALL_STATE, callStateCode);
        intent.putExtra(BroadcastParameters.CONNECT_TIMESTAMP, connectTimestamp);
        intent.putExtra(BroadcastParameters.LOCAL_HOLD, isLocalHold);
        intent.putExtra(BroadcastParameters.LOCAL_MUTE, isLocalMute);

        mContext.sendBroadcast(intent);
    }

    public void outgoingCall(String accountID, int callID, String number) {
        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastEventTrigger.BroadcastAction.OUTGOING_CALL));
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID, accountID);
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.CALL_ID, callID);
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.NUMBER, number);

        mContext.sendBroadcast(intent);
    }

    public void serviceStopped() {
        final Intent intent = new Intent();
        intent.setAction(getAction(BroadcastEventTrigger.BroadcastAction.SERVICE_STOP));
        mContext.sendBroadcast(intent);
    }

    public void removedAccount() {

           final Intent intent = new Intent();
           intent.setAction(getAction(BroadcastEventTrigger.BroadcastAction.REMOVED_ACCOUNT));
            mContext.sendBroadcast(intent);
    }

    public void outgoingVideoCall(String accountID, int callID, String number) {

        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.OUTGOING_VIDEO_CALL));
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID, accountID);
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.CALL_ID, callID);
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.NUMBER, number);

        mContext.sendBroadcast(intent);
    }

    public void videoCallState(String accountID, int vidWin, int vidPrev) {

        final Intent intent = new Intent();

        intent.setAction(getAction(BroadcastAction.VIDEO_MEDIA_CALL));
        intent.putExtra(BroadcastEventTrigger.BroadcastParameters.ACCOUNT_ID, accountID);
        intent.putExtra(BroadcastParameters.VIDEO_WINDOW, vidWin);
        intent.putExtra(BroadcastParameters.VIDEO_PREVIEW, vidPrev);

        mContext.sendBroadcast(intent);

    }

    /**
     * Enumeration of the broadcast actions
     */
    public enum BroadcastAction {
        REGISTRATION,
        INCOMING_CALL,
        CALL_STATE,
        OUTGOING_CALL,
        STACK_STATUS,
        CODEC_PRIORITIES,
        SERVICE_STOP,
        CODEC_PRIORITIES_SET_STATUS,
        REMOVED_ACCOUNT,
        OUTGOING_VIDEO_CALL,
        VIDEO_MEDIA_CALL
    }

    /**
     * Parameters passed in the broadcast intents.
     */
    public class BroadcastParameters {
        public static final String ACCOUNT_ID = "account_id";
        public static final String CALL_ID = "call_id";
        public static final String VIDEO_WINDOW = "video_window";
        public static final String VIDEO_PREVIEW = "video_preview";
        public static final String CODE = "code";
        public static final String REMOTE_URI = "remote_uri";
        public static final String DISPLAY_NAME = "display_name";
        public static final String CALL_STATE = "call_state";
        public static final String NUMBER = "number";
        public static final String CONNECT_TIMESTAMP = "connectTimestamp";
        public static final String STACK_STARTED = "stack_started";
        public static final String CODEC_PRIORITIES_LIST = "codec_priorities_list";
        public static final String LOCAL_HOLD = "local_hold";
        public static final String LOCAL_MUTE = "local_mute";
        public static final String SUCCESS = "success";
    }
}
