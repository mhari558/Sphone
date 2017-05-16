package com.sip.softphone.common;

/**
 * Created by hari on 6/4/17.
 */

public interface Const {

    String TAG = "Softphone";
    String PREFS_NAME = TAG + "prefs";
    String PREFS_KEY_ACCOUNTS = "accounts";

    String USER_ACCOUNT = "accountData";

    String PREFS_ACCOUNT_ID = "accountID";
    String ISREGISTERED = "registered";


    String CALLERNAME = "callerName";
    String REMOTEURI = "remoteUri";
    String CALLID = "remoteUri";
    String CALLNUMBER = "callNumber";
    String PREFS_KEY_CODEC_PRIORITIES = "codec_priorities";

    String ACTIVE_CALL = "activeCall";
    String NONACTIVE_CALL = "nonactiveCall";

    String CALLTYPE = "calltype";

    int TIMELIMIT = 300000;
    int TIMEINTERVAL = 1000;


    String ACTION_RESTART_SIP_STACK = "restartSipStack";
    String ACTION_SET_ACCOUNT = "setAccount";
    String ACTION_REMOVE_ACCOUNT = "removeAccount";
    String ACTION_MAKE_CALL = "makeCall";
    String ACTION_HANG_UP_CALL = "hangUpCall";
    String ACTION_HANG_UP_CALLS = "hangUpCalls";
    String ACTION_HOLD_CALLS = "holdCalls";
    String ACTION_GET_CALL_STATUS = "getCallStatus";
    String ACTION_SEND_DTMF = "sendDtmf";
    String ACTION_ACCEPT_INCOMING_CALL = "acceptIncomingCall";
    String ACTION_DECLINE_INCOMING_CALL = "declineIncomingCall";
    String ACTION_SET_HOLD = "callSetHold";
    String ACTION_SET_MUTE = "callSetMute";
    String ACTION_TOGGLE_HOLD = "callToggleHold";
    String ACTION_TOGGLE_MUTE = "callToggleMute";
    String ACTION_TRANSFER_CALL = "callTransfer";
    String ACTION_GET_CODEC_PRIORITIES = "codecPriorities";
    String ACTION_SET_CODEC_PRIORITIES = "setCodecPriorities";
    String ACTION_GET_REGISTRATION_STATUS = "getRegistrationStatus";

    String PARAM_ACCOUNT_DATA = "accountData";
    String PARAM_ACCOUNT_ID = "accountID";
    String PARAM_NUMBER = "number";
    String PARAM_CALL_ID = "callId";
    String PARAM_DTMF = "dtmf";
    String PARAM_HOLD = "hold";
    String PARAM_MUTE = "mute";
    String PARAM_CODEC_PRIORITIES = "codecPriorities";

    String ACTION_VIDEO_CALL = "videoCall";
    String ACTION_SET_VIDEOPREVIEW = "action_set_videopreview" ;
}
