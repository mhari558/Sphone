/*
package com.sip.softphone.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sip.softphone.recievers.BroadcastEventTrigger;
import com.sip.softphone.sip.SipAccount;
import com.sip.softphone.sip.SipAccountData;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.SipSingleton;

import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.sip.softphone.common.Const.ACTION_MAKE_CALL;
import static com.sip.softphone.common.Const.TAG;

*/
/**
 * Created by hari on 14/4/17.
 *//*


public class SipBackgroundService extends BackgroundService {

    private MediaPlayer mRingTone;
    private AudioManager mAudioManager;
    private Vibrator mVibrator;
    private Uri mRingtoneUri;
    private static ConcurrentHashMap<String, SipAccount> mActiveSipAccounts = new ConcurrentHashMap<>();
    private List<SipAccountData> mConfiguredAccounts = new ArrayList<>();
    private BroadcastEventTrigger mBroadcastEmitter;
    private static final long[] VIBRATOR_PATTERN = {0, 1000, 1000};
    private Endpoint mEndpoint;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        enqueueJob(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Creating SipService with priority: " + Thread.currentThread().getPriority());

                loadNativeLibraries();
                mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(SipBackgroundService.this, RingtoneManager.TYPE_RINGTONE);
                mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mBroadcastEmitter = new BroadcastEventTrigger(SipBackgroundService.this);

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

               if(intent == null) return;
               String action = intent.getAction();
               if(Const.ACTION_SET_ACCOUNT.equals(action)){
                   prepareAccountConfigureSetting(intent);
               }

           }
       });
        return START_STICKY;
    }

    private void prepareAccountConfigureSetting(Intent intent) {

        endpointConfigure();

        SipAccountData data = intent.getParcelableExtra(Const.PARAM_ACCOUNT_DATA);

        int index = mConfiguredAccounts.indexOf(data);
        if(index == -1){
            handleResetAccounts(intent);
            Log.d(Const.TAG, "Adding " + data.getIdUri());

            try {
                addAccount(data);
                mConfiguredAccounts.add(data);
                persistConfiguredAccounts();
            } catch (Exception exc) {
                Log.e(Const.TAG, "Error while adding " + data.getIdUri(), exc);
            }

        }else {
            Log.e(Const.TAG, "Reconfiguring " + data.getIdUri());

            try {
                endpointConfigure();
                removeAccount(data.getIdUri());
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
            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
            mEndpoint.libStart();
        }catch (Exception e){

        }

    }
    private void handleResetAccounts(Intent intent) {
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

    private void persistConfiguredAccounts() {
        SharedPreferences prefs = getSharedPreferences(Const.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(Const.PREFS_KEY_ACCOUNTS, new Gson().toJson(mConfiguredAccounts)).apply();
    }
    */
/**
     * Removes a SIP Account and performs un-registration.
     *//*

    private void removeAccount(String accountID) throws Exception {
        SipAccount account = mActiveSipAccounts.remove(accountID);

        if (account == null) {
            Log.e(Const.TAG, "No account for ID: " + accountID);
            return;
        }

        Log.d(Const.TAG, "Removing SIP account " + accountID);
        account.delete();
        Log.d(Const.TAG, "SIP account " + accountID + " successfully removed");
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
    */
/**
     * Adds a new SIP Account and performs initial registration.
     *
     * @param account SIP account to add
     *//*

    private void addAccount(SipAccountData account) throws Exception {
        String accountString = account.getIdUri();

        if (!mActiveSipAccounts.containsKey(accountString)) {
            SipAccount pjSipAndroidAccount = new SipAccount(this, account);
            pjSipAndroidAccount.create();
            mActiveSipAccounts.put(accountString, pjSipAndroidAccount);
            ArrayList<SipAccountData> list = new ArrayList<>();
            list.add(account);
            SipSingleton.getInstance().setSipAccountList(list);
            Log.d(Const.TAG, "SIP account " + account.getIdUri() + " successfully added");
        }
    }
    public BroadcastEventTrigger getBroadcastEmitter() {
        return mBroadcastEmitter;
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
    public synchronized AudDevManager getAudDevManager() {
        return mEndpoint.audDevManager();
    }
}
*/
