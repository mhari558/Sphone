package com.sip.softphone.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sip.softphone.activities.AVCallsActivity;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.MediaStateListener;
import com.sip.softphone.common.SipSingleton;
import com.sip.softphone.common.Utils;
import com.sip.softphone.recievers.BroadcastEventTrigger;
import com.sip.softphone.sip.CodecPriority;
import com.sip.softphone.sip.SipAccount;
import com.sip.softphone.sip.SipAccountData;
import com.sip.softphone.sip.SipCall;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.CodecInfo;
import org.pjsip.pjsua2.CodecInfoVector;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.VidDevManager;
import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.VideoWindowHandle;
import org.pjsip.pjsua2.pj_qos_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.sip.softphone.common.Const.TAG;

/**
 * Created by hari on 10/4/17.
 */

public class SipService extends BackgroundService {


    private static final long[] VIBRATOR_PATTERN = {0, 1000, 1000};
    private static ConcurrentHashMap<String, SipAccount> mActiveSipAccounts = new ConcurrentHashMap<>();
    private MediaPlayer mRingTone;
    private AudioManager mAudioManager;
    private Vibrator mVibrator;
    private Uri mRingtoneUri;
    private BroadcastEventTrigger mBroadcastEmitter;
    private Endpoint mEndpoint;
    private volatile boolean mStarted;
    private List<SipAccountData> mConfiguredAccounts = new ArrayList<>();

    public MediaStateListener mediaStateListener;

    public void setMediaStateListener(MediaStateListener mediaStateListener){
        this.mediaStateListener = mediaStateListener;
    }
    public MediaStateListener getMediaStateListener(){
        return mediaStateListener;
    }
    public static SipService sipService;
    public static SipService getInstance(){
        return sipService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sipService = this;
        enqueueJob(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Creating SipService with priority: " + Thread.currentThread().getPriority());

                loadNativeLibraries();
                mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(SipService.this, RingtoneManager.TYPE_RINGTONE);
                mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mBroadcastEmitter = new BroadcastEventTrigger(SipService.this);
                //  endpointConfigure();
                loadConfiguredAccounts();
                addAllConfiguredAccounts();


                Log.d(Const.TAG, "SipService created!");
            }
        });
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        enqueueJob(new Runnable() {
            @Override
            public void run() {

                if (intent == null) return;
                String action = intent.getAction();
                if (Const.ACTION_SET_ACCOUNT.equals(action)) {
                    handleSetAccount(intent);

                } else if (Const.ACTION_REMOVE_ACCOUNT.equals(action)) {
                    handleRemoveAccount(intent);

                } else if (Const.ACTION_RESTART_SIP_STACK.equals(action)) {
                    handleRestartSipStack();

                } else if (Const.ACTION_MAKE_CALL.equals(action)) {

                    Log.e("INside make action call", "======");
                    handleMakeCall(intent);

                } else if (Const.ACTION_HANG_UP_CALL.equals(action)) {
                    handleHangUpCall(intent);

                } else if (Const.ACTION_HANG_UP_CALLS.equals(action)) {
                    handleHangUpActiveCalls(intent);

                } else if (Const.ACTION_HOLD_CALLS.equals(action)) {
                    handleHoldActiveCalls(intent);

                } else if (Const.ACTION_GET_CALL_STATUS.equals(action)) {
                    handleGetCallStatus(intent);

                } else if (Const.ACTION_SEND_DTMF.equals(action)) {
                    handleSendDTMF(intent);

                } else if (Const.ACTION_ACCEPT_INCOMING_CALL.equals(action)) {
                    handleAcceptIncomingCall(intent);

                } else if (Const.ACTION_DECLINE_INCOMING_CALL.equals(action)) {
                    handleDeclineIncomingCall(intent);

                } else if (Const.ACTION_SET_HOLD.equals(action)) {
                    handleSetCallHold(intent);

                } else if (Const.ACTION_TOGGLE_HOLD.equals(action)) {
                    handleToggleCallHold(intent);

                } else if (Const.ACTION_SET_MUTE.equals(action)) {
                    handleSetCallMute(intent);

                } else if (Const.ACTION_TOGGLE_MUTE.equals(action)) {
                    handleToggleCallMute(intent);

                } else if (Const.ACTION_TRANSFER_CALL.equals(action)) {
                    handleTransferCall(intent);

                } else if (Const.ACTION_GET_CODEC_PRIORITIES.equals(action)) {
                    handleGetCodecPriorities();

                } else if (Const.ACTION_SET_CODEC_PRIORITIES.equals(action)) {
                    handleSetCodecPriorities(intent);

                } else if (Const.ACTION_GET_REGISTRATION_STATUS.equals(action)) {
                    handleGetRegistrationStatus(intent);

                }else if(Const.ACTION_VIDEO_CALL.equals(action)){

                    hadleVideoCall(intent);
                }else if(Const.ACTION_SET_VIDEOPREVIEW.equals(action)){

                    setVideoPreviewOnCall(intent);
                }

                if (mConfiguredAccounts.isEmpty()) {
                    Log.d(Const.TAG, "No more configured accounts. Shutting down service");
                    //mBroadcastEmitter.serviceStopped();
                    stopSelf();
                }
            }
        });
        return START_STICKY;
    }

    private void setVideoPreviewOnCall(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        SipAccount account = mActiveSipAccounts.get(accountID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        VideoWindowHandle vidWH = new VideoWindowHandle();
       // vidWH.getHandle().setWindow(holder.getSurface());
        VideoPreviewOpParam vidPrevParam = new VideoPreviewOpParam();
        vidPrevParam.setWindow(vidWH);

        SipCall sipCall = getCall(accountID, callID);
        try {
            sipCall.vidPrev.start(vidPrevParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hadleVideoCall(Intent intent) {

        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        String number = intent.getStringExtra(Const.PARAM_NUMBER);

        String host = Utils.getObjectInPrefs(SipService.this).getHost();
        Log.d(Const.TAG, "Making Video call to " + number + "   AccountId " + accountID);
        try {
            SipCall call = mActiveSipAccounts.get(accountID).addOutgoingVideoCall(number,host);

            mBroadcastEmitter.outgoingVideoCall(accountID, call.getId(), number);
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while making outgoing call", exc);
            mBroadcastEmitter.outgoingVideoCall(accountID, -1, number);
        }
    }

    private void handleMakeCall(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        String number = intent.getStringExtra(Const.PARAM_NUMBER);

        String host = Utils.getObjectInPrefs(SipService.this).getHost();
        Log.d(Const.TAG, "Making call to " + number + "   AccountId " + accountID);

        try {
            SipCall call = mActiveSipAccounts.get(accountID).addOutgoingCall(number,host);

            mBroadcastEmitter.outgoingCall(accountID, call.getId(), number);
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while making outgoing call", exc);
            mBroadcastEmitter.outgoingCall(accountID, -1, number);
        }
    }

    private void handleRestartSipStack() {
        Log.d(Const.TAG, "Restarting SIP stack");
        stopStack();
        addAllConfiguredAccounts();
    }

    private void handleAcceptIncomingCall(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.acceptIncomingCall();

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while accepting incoming call. AccountID: "
                    + accountID + ", CallID: " + callID);
        }

    }

    private void notifyCallDisconnected(String accountID, int callID) {
        mBroadcastEmitter.callState(accountID, callID,
                pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED.swigValue(), 0, false, false);
    }

    private SipCall getCall(String accountID, int callID) {
        SipAccount account = mActiveSipAccounts.get(accountID);

        if (account == null) return null;
        return account.getCall(callID);
    }

    private void handleGetCodecPriorities() {
        ArrayList<CodecPriority> codecs = getCodecPriorityList();

        if (codecs != null) {
            mBroadcastEmitter.codecPriorities(codecs);
        }
    }

    private void handleGetRegistrationStatus(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);

        if (!mStarted || mActiveSipAccounts.get(accountID) == null) {
            mBroadcastEmitter.registrationState("", 400);
            return;
        }

        SipAccount account = mActiveSipAccounts.get(accountID);
        try {
            mBroadcastEmitter.registrationState(accountID, account.getInfo().getRegStatus().swigValue());
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while getting registration status for " + accountID, exc);
        }
    }

    private void handleRemoveAccount(Intent intent) {
        String accountIDtoRemove = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);

        Log.d(Const.TAG, "Removing " + accountIDtoRemove);

        Iterator<SipAccountData> iterator = mConfiguredAccounts.iterator();

        while (iterator.hasNext()) {
            SipAccountData data = iterator.next();

            if (data.getIdUri().equals(accountIDtoRemove)) {
                try {
                    removeAccount(accountIDtoRemove);
                    iterator.remove();
                    persistConfiguredAccounts();
                } catch (Exception exc) {
                    Log.e(Const.TAG, "Error while removing account " + accountIDtoRemove, exc);
                }
                break;
            }
        }
    }

    private void handleSetCallHold(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);
        boolean hold = intent.getBooleanExtra(Const.PARAM_HOLD, false);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.setHold(hold);
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while setting hold. AccountID: "
                    + accountID + ", CallID: " + callID);
        }
    }

    private void handleToggleCallHold(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.toggleHold();
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while toggling hold. AccountID: "
                    + accountID + ", CallID: " + callID);
        }
    }

    private void handleSetCallMute(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);
        boolean mute = intent.getBooleanExtra(Const.PARAM_MUTE, false);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.setMute(mute);
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while setting mute. AccountID: "
                    + accountID + ", CallID: " + callID);
        }
    }

    private void handleToggleCallMute(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.toggleMute();
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while toggling mute. AccountID: "
                    + accountID + ", CallID: " + callID);
        }
    }

    private void handleDeclineIncomingCall(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.declineIncomingCall();
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while declining incoming call. AccountID: "
                    + accountID + ", CallID: " + callID);
        }
    }

    private void handleHangUpCall(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        try {
            SipCall sipCall = getCall(accountID, callID);

            if (sipCall == null) {
                notifyCallDisconnected(accountID, callID);
                return;
            }

            sipCall.hangUp();

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while hanging up call", exc);
            notifyCallDisconnected(accountID, callID);
        }
    }

    private void handleHangUpActiveCalls(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);

        SipAccount account = mActiveSipAccounts.get(accountID);
        if (account == null) return;

        Set<Integer> activeCallIDs = account.getCallIDs();

        if (activeCallIDs == null || activeCallIDs.isEmpty()) return;

        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = getCall(accountID, callID);

                if (sipCall == null) {
                    notifyCallDisconnected(accountID, callID);
                    return;
                }

                sipCall.hangUp();
            } catch (Exception exc) {
                Log.e(Const.TAG, "Error while hanging up call", exc);
                notifyCallDisconnected(accountID, callID);
            }
        }
    }

    private void handleHoldActiveCalls(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);

        SipAccount account = mActiveSipAccounts.get(accountID);
        if (account == null) return;

        Set<Integer> activeCallIDs = account.getCallIDs();

        if (activeCallIDs == null || activeCallIDs.isEmpty()) return;

        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = getCall(accountID, callID);

                if (sipCall == null) {
                    notifyCallDisconnected(accountID, callID);
                    return;
                }

                sipCall.setHold(true);
            } catch (Exception exc) {
                Log.e(Const.TAG, "Error while holding call", exc);
            }
        }
    }

    private void handleTransferCall(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);
        String number = intent.getStringExtra(Const.PARAM_NUMBER);

        try {
            SipCall sipCall = getCall(accountID, callID);

            if (sipCall == null) {
                notifyCallDisconnected(accountID, callID);
                return;
            }

            sipCall.transferTo(number);

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while transferring call to " + number, exc);
            notifyCallDisconnected(accountID, callID);
        }
    }

    private ArrayList<CodecPriority> getCodecPriorityList() {
        startStack();

        if (!mStarted) {
            Log.e(Const.TAG, "Can't get codec priority list! The SIP Stack has not been " +
                    "initialized! Add an account first!");
            return null;
        }

        try {
            CodecInfoVector codecs = mEndpoint.codecEnum();
            if (codecs == null || codecs.size() == 0) return null;

            ArrayList<CodecPriority> codecPrioritiesList = new ArrayList<>((int) codecs.size());

            for (int i = 0; i < (int) codecs.size(); i++) {
                CodecInfo codecInfo = codecs.get(i);
                CodecPriority newCodec = new CodecPriority(codecInfo.getCodecId(),
                        codecInfo.getPriority());
                if (!codecPrioritiesList.contains(newCodec))
                    codecPrioritiesList.add(newCodec);
                codecInfo.delete();
            }

            codecs.delete();

            Collections.sort(codecPrioritiesList);
            return codecPrioritiesList;

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while getting codec priority list!", exc);
            return null;
        }
        //return null;
    }

    private void handleSetAccount(Intent intent) {

        SipAccountData data = intent.getParcelableExtra(Const.PARAM_ACCOUNT_DATA);

        int index = mConfiguredAccounts.indexOf(data);
        if (index == -1) {
            handleResetAccounts();
            Log.d(Const.TAG, "Adding " + data.getIdUri());
            Log.d(Const.TAG, "USERNAME " + data.getAccountName());

            try {

                handleSetCodecPriorities(intent);
                addAccount(data);
                mConfiguredAccounts.add(data);
                persistConfiguredAccounts();
            } catch (Exception exc) {
                Log.e(Const.TAG, "Error while adding " + data.getIdUri(), exc);
            }
        } else {
            Log.e(Const.TAG, "Reconfiguring " + data.getIdUri());

            try {
                // endpointConfigure();
                removeAccount(data.getIdUri());
                handleSetCodecPriorities(intent);
                addAccount(data);
                mConfiguredAccounts.set(index, data);
                persistConfiguredAccounts();
            } catch (Exception exc) {
                Log.e(Const.TAG, "Error while reconfiguring " + data.getIdUri(), exc);
            }
        }
    }

    private void endpointConfigure() {

        try {
            mEndpoint = new Endpoint();
            mEndpoint.libCreate();
            EpConfig epConfig = new EpConfig();
            mEndpoint.libInit(epConfig);
            TransportConfig transportConfig = new TransportConfig();
            //transportConfig.setPort(5060);
            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
            mEndpoint.libStart();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setEndPoint() {
        try {
            Endpoint endpoint = new Endpoint();
            endpoint.libCreate();
            EpConfig epConfig = new EpConfig();
            endpoint.libInit(epConfig);
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.setPort(5060);
            endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
            endpoint.libStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleSetCodecPriorities(Intent intent) {
        ArrayList<CodecPriority> codecPriorities = intent.getParcelableArrayListExtra(Const.PARAM_CODEC_PRIORITIES);

        if (codecPriorities == null) {
            return;
        }

        startStack();

        if (!mStarted) {
            mBroadcastEmitter.codecPrioritiesSetStatus(false);
            return;
        }

        try {
            StringBuilder log = new StringBuilder();
            log.append("Codec priorities successfully set. The priority order is now:\n");

            for (CodecPriority codecPriority : codecPriorities) {
                mEndpoint.codecSetPriority(codecPriority.getCodecId(), (short) codecPriority.getPriority());
                log.append(codecPriority.toString()).append("\n");
            }

            persistConfiguredCodecPriorities(codecPriorities);
            Log.e(Const.TAG, log.toString());
            mBroadcastEmitter.codecPrioritiesSetStatus(true);

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while setting codec priorities", exc);
            mBroadcastEmitter.codecPrioritiesSetStatus(false);
        }
    }

    private void persistConfiguredAccounts() {
        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(Const.PREFS_KEY_ACCOUNTS, new Gson().toJson(mConfiguredAccounts)).apply();
    }

    private void persistConfiguredCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(Const.PREFS_KEY_CODEC_PRIORITIES, new Gson().toJson(codecPriorities)).apply();
    }

    private ArrayList<CodecPriority> getConfiguredCodecPriorities() {
        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);

        String codecPriorities = prefs.getString(Const.PREFS_KEY_CODEC_PRIORITIES, "");
        if (codecPriorities.isEmpty()) {
            return null;
        }

        Type listType = new TypeToken<ArrayList<CodecPriority>>() {
        }.getType();
        return new Gson().fromJson(codecPriorities, listType);
    }

    private void handleResetAccounts() {
        Log.d(Const.TAG, "Removing all the configured accounts");

        Iterator<SipAccountData> iterator = mConfiguredAccounts.iterator();

        while (iterator.hasNext()) {
            SipAccountData data = iterator.next();

            try {
                removeAccount(data.getIdUri());


                iterator.remove();
            } catch (Exception exc) {
                Log.e(Const.TAG, "Error while removing account " + data.getIdUri(), exc);
            }
        }

        persistConfiguredAccounts();
    }

    private void handleGetCallStatus(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        getMediaStateListener().currentCall(sipCall);

        mBroadcastEmitter.callState(accountID, callID, sipCall.getCurrentState().swigValue(),
                sipCall.getConnectTimestamp(), sipCall.isLocalHold(),
                sipCall.isLocalMute());
    }

    private void handleSendDTMF(Intent intent) {
        String accountID = intent.getStringExtra(Const.PARAM_ACCOUNT_ID);
        int callID = intent.getIntExtra(Const.PARAM_CALL_ID, 0);
        String dtmf = intent.getStringExtra(Const.PARAM_DTMF);

        SipCall sipCall = getCall(accountID, callID);
        if (sipCall == null) {
            notifyCallDisconnected(accountID, callID);
            return;
        }

        try {
            sipCall.dialDtmf(dtmf);
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while dialing dtmf: " + dtmf + ". AccountID: "
                    + accountID + ", CallID: " + callID);
        }
    }

    /**
     * Removes a SIP Account and performs un-registration.
     */
    private void removeAccount(String accountID) throws Exception {
        SipAccount account = mActiveSipAccounts.remove(accountID);

        if (account == null) {
            Log.e(Const.TAG, "No account for ID: " + accountID);
            return;
        }

        Log.d(Const.TAG, "Removing SIP account " + accountID);
        account.delete();
        Utils.claerAccountDetails(this);
        Log.d(Const.TAG, "SIP account " + accountID + " successfully removed");
    }

    private void loadConfiguredAccounts() {
        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);

        String accounts = prefs.getString(Const.PREFS_KEY_ACCOUNTS, "");

        if (accounts.isEmpty()) {
            mConfiguredAccounts = new ArrayList<>();
        } else {
            Type listType = new TypeToken<ArrayList<SipAccountData>>() {
            }.getType();
            mConfiguredAccounts = new Gson().fromJson(accounts, listType);
        }
    }

    private void addAllConfiguredAccounts() {
        if (!mConfiguredAccounts.isEmpty()) {
            for (SipAccountData accountData : mConfiguredAccounts) {
                try {
                    addAccount(accountData);
                } catch (Exception exc) {
                    Log.e(Const.TAG, "Error while adding " + accountData.getIdUri());
                }
            }
        }
    }

    public synchronized void startRingtone() {
        mVibrator.vibrate(VIBRATOR_PATTERN, 0);

        try {
            mRingTone = MediaPlayer.create(this, mRingtoneUri);
            mRingTone.setLooping(true);

            int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            mRingTone.setVolume(volume, volume);

            mRingTone.start();
        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while trying to play ringtone!", exc);
        }
    }

    public synchronized void stopRingtone() {
        mVibrator.cancel();

        if (mRingTone != null) {
            try {
                if (mRingTone.isPlaying())
                    mRingTone.stop();
            } catch (Exception ignored) {
            }

            try {
                mRingTone.reset();
                mRingTone.release();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Adds a new SIP Account and performs initial registration.
     *
     * @param account SIP account to add
     */
    private void addAccount(SipAccountData account) throws Exception {
        String accountString = account.getIdUri();

        if (!mActiveSipAccounts.containsKey(accountString)) {
            startStack();
            SipAccount pjSipAndroidAccount = new SipAccount(this, account);
            pjSipAndroidAccount.create();
            mActiveSipAccounts.put(accountString, pjSipAndroidAccount);
            ArrayList<SipAccountData> list = new ArrayList<>();
            list.add(account);
            SipSingleton.getInstance().setSipAccountList(list);
            Log.e("Add Account ID", accountString);
            Utils.putStringInPrefs(this, Const.PREFS_ACCOUNT_ID, accountString);
            Utils.putObjectInPrefs(this, account);

          /*  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);*/

          /*  preferences.edit().putString(Const.PREFS_ACCOUNT_ID,accountString).apply();*/
            Log.d(Const.TAG, "SIP account " + account.getIdUri() + " successfully added");
        }
    }

    private void loadNativeLibraries() {
        try {
            System.loadLibrary("openh264");
            Log.d(TAG, "OpenH264 loaded");
        } catch (UnsatisfiedLinkError error) {
            Log.e(TAG, "Error while loading OpenH264 native library", error);
            throw new RuntimeException(error);
        }

        try {
            System.loadLibrary("yuv");
            Log.d(TAG, "libyuv loaded");
        } catch (UnsatisfiedLinkError error) {
            Log.e(TAG, "Error while loading libyuv native library", error);
            throw new RuntimeException(error);
        }

        try {
            System.loadLibrary("pjsua2");
            Log.d(TAG, "PJSIP pjsua2 loaded");
        } catch (UnsatisfiedLinkError error) {
            Log.e(TAG, "Error while loading PJSIP pjsua2 native library", error);
            throw new RuntimeException(error);
        }
    }

    /**
     * Starts PJSIP Stack.
     */
    private void startStack() {

          /* try {
                Endpoint endpoint = new Endpoint();
                endpoint.libCreate();
                EpConfig epConfig = new EpConfig();
                endpoint.libInit(epConfig);
                TransportConfig transportConfig = new TransportConfig();
                transportConfig.setPort(5060);
                endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
                endpoint.libStart();
            }catch (Exception e){

            }*/
        if (mStarted) return;

        try {
            Log.d(Const.TAG, "Starting PJSIP");
            mEndpoint = new Endpoint();
            mEndpoint.libCreate();

            EpConfig epConfig = new EpConfig();
            epConfig.getUaConfig().setUserAgent(SipServiceInteractor.AGENT_NAME);
            epConfig.getMedConfig().setHasIoqueue(true);
            epConfig.getMedConfig().setClockRate(16000);
            epConfig.getMedConfig().setQuality(10);
            epConfig.getMedConfig().setEcOptions(1);
            epConfig.getMedConfig().setEcTailLen(200);
            epConfig.getMedConfig().setThreadCnt(2);
            epConfig.getMedConfig().setVidPreviewEnableNative(true);
            mEndpoint.libInit(epConfig);

            TransportConfig udpTransport = new TransportConfig();
            udpTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
            TransportConfig tcpTransport = new TransportConfig();
            tcpTransport.setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);

            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, udpTransport);
            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, tcpTransport);
            mEndpoint.libStart();
            ArrayList<CodecPriority> codecPriorities = getConfiguredCodecPriorities();
            if (codecPriorities != null) {
                Log.d(Const.TAG, "Setting saved codec priorities...");
                for (CodecPriority codecPriority : codecPriorities) {
                    Log.d(Const.TAG, "Setting " + codecPriority.getCodecId() + " priority to " + codecPriority.getPriority());
                    mEndpoint.codecSetPriority(codecPriority.getCodecId(), (short) codecPriority.getPriority());
                }
                Log.d(Const.TAG, "Saved codec priorities set!");
            } else {
                mEndpoint.codecSetPriority("PCMA/8000", (short) (CodecPriority.PRIORITY_MAX - 1));
                mEndpoint.codecSetPriority("PCMU/8000", (short) (CodecPriority.PRIORITY_MAX - 2));
                mEndpoint.codecSetPriority("G729/8000", (short) (CodecPriority.PRIORITY_DISABLED));
                mEndpoint.codecSetPriority("speex/8000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("speex/16000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("speex/32000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("GSM/8000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("G722/16000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("G7221/16000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("G7221/32000", (short) CodecPriority.PRIORITY_DISABLED);
                mEndpoint.codecSetPriority("ilbc/8000", (short) CodecPriority.PRIORITY_DISABLED);
            }

            Log.d(Const.TAG, "PJSIP started!");
            mStarted = true;
            mBroadcastEmitter.stackStatus(true);

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while starting PJSIP", exc);
            mStarted = false;
        }
    }

    public BroadcastEventTrigger getBroadcastEmitter() {
        return mBroadcastEmitter;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {
        enqueueJob(new Runnable() {
            @Override
            public void run() {
                Log.d(Const.TAG, "Destroying SipService");
                stopStack();
            }
        });
        super.onDestroy();
    }

    /**
     * Shuts down PJSIP Stack
     *
     * @throws Exception if an error occurs while trying to shut down the stack
     */
    private void stopStack() {

        if (!mStarted) return;

        try {
            Log.d(Const.TAG, "Stopping PJSIP");

            removeAllActiveAccounts();

            // try to force GC to do its job before destroying the library, since it's
            // recommended to do that by PJSUA examples
            Runtime.getRuntime().gc();

            mEndpoint.libDestroy();
            mEndpoint.delete();
            mEndpoint = null;

            Log.d(Const.TAG, "PJSIP stopped");
            mBroadcastEmitter.stackStatus(false);

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while stopping PJSIP", exc);

        } finally {
            mStarted = false;
            mEndpoint = null;
        }
    }

    private void removeAllActiveAccounts() {
        if (!mActiveSipAccounts.isEmpty()) {
            for (String accountID : mActiveSipAccounts.keySet()) {
                try {
                    removeAccount(accountID);
                } catch (Exception exc) {
                    Log.e(Const.TAG, "Error while removing " + accountID);
                }
            }
        }
    }

    public synchronized AudDevManager getAudDevManager() {
        return mEndpoint.audDevManager();
    }
    public synchronized VidDevManager getVidDevManager() {
        return mEndpoint.vidDevManager();
    }
}
