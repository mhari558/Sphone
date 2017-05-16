package com.sip.softphone.sip;

import android.util.Log;

import com.sip.softphone.common.Const;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_media_status;

/**
 * Created by hari on 9/5/17.
 */

public class MyClass extends Call {

    public VideoWindow vidWin;
    public VideoPreview vidPrev;
    private SipAccount account;

    protected MyClass(long cPtr, boolean cMemoryOwn) {
        super(cPtr, cMemoryOwn);
    }

    public MyClass(SipAccount account, int call_id) {
        super(account, call_id);
        this.account = account;

    }

    public MyClass(Account acc) {
        super(acc);
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        try {
            CallInfo ci = getInfo();
            if (ci.getState() ==
                    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
            {
                this.delete();
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        CallInfo info;
        try {
            Log.i(Const.TAG, "onCallMediaState: onCallMediaState");
            info = getInfo();
        } catch (Exception exc) {
            Log.e(Const.TAG, "onCallMediaState: error while getting call info", exc);
            return;
        }

        CallMediaInfoVector cmiv = info.getMedia();

        for (int i = 0; i < cmiv.size(); i++) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = info.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                    (mediaInfo.getStatus() ==
                            pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                            mediaInfo.getStatus() ==
                                    pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
            {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                account.getService().stopRingtone();

                // connect the call audio media to sound device
                try {
                    AudDevManager mgr = account.getService().getAudDevManager();

                    try {
                        audioMedia.adjustRxLevel((float) 1.5);
                        audioMedia.adjustTxLevel((float) 1.5);
                    } catch (Exception exc) {
                        Log.e(Const.TAG, "Error while adjusting levels", exc);
                    }

                    audioMedia.startTransmit(mgr.getPlaybackDevMedia());
                    mgr.getCaptureDevMedia().startTransmit(audioMedia);
                } catch (Exception exc) {
                    Log.e(Const.TAG, "Error while connecting audio media to sound device", exc);
                }
            } else if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
                    mediaInfo.getStatus() ==
                            pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE &&
                    mediaInfo.getVideoIncomingWindowId() != pjsua2.INVALID_ID)
            {
                vidWin = new VideoWindow(mediaInfo.getVideoIncomingWindowId());
                vidPrev = new VideoPreview(mediaInfo.getVideoCapDev());
            }
        }

        //MyApp.observer.notifyCallMediaState(this);
    }
}
