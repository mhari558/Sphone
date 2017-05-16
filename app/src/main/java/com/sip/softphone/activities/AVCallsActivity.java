package com.sip.softphone.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.sip.softphone.R;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.Utils;
import com.sip.softphone.recievers.BroadcastReceiverEvent;
import com.sip.softphone.sip.SipAccountData;
import com.sip.softphone.video.VideoPreviewHandler;

import org.pjsip.pjsua2.VideoWindowHandle;

/**
 * Created by hari on 9/5/17.
 */

public class AVCallsActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static Handler handler_;
    private static VideoPreviewHandler videoPreviewHandler = new VideoPreviewHandler();
    BroadcastReceiverEvent event = new BroadcastReceiverEvent() {

        @Override
        public void onVideoMediaState(String accountID, int videoWindow, int videoPreview) {
            super.onVideoMediaState(accountID, videoWindow, videoPreview);

            Log.d(Const.TAG, "AVSCALLonVideoMediaState - accountID: " + accountID +
                    ", callID: " + videoWindow +
                    ", number: " + videoPreview);
        }

        @Override
        public void onIncomingCall(String accountID, int callID, String displayName, String remoteUri) {
            super.onIncomingCall(accountID, callID, displayName, remoteUri);

            Log.d(Const.TAG, "AVSCALLonIncomingCall - accountID: " + accountID +
                    ", callID: " + displayName +
                    ", number: " + remoteUri);

        }

        @Override
        public void onOutgoingCall(String accountID, int callID, String number) {
            super.onOutgoingCall(accountID, callID, number);
            // SipServiceInteractor.setVideoPreview(AVCallsActivity.this,accountID,callID);

           /* String loggedAccountId = loggedSipAccount.getIdUri();
            SipAccount pjSipAndroidAccount = new SipAccount(AVCallsActivity.this, loggedSipAccount);
            SipCall sipCall = getCall(accountID, callID);
            VideoPreviewHandler videoPreviewHandler = new VideoPreviewHandler();*/
            //videoPreviewHandler.
        }
    };
    //  private final Handler handler = new Handler(this);
    private SipAccountData loggedSipAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avcall);
        SurfaceView surfaceInVideo = (SurfaceView)
                findViewById(R.id.surfaceIncomingVideo);
        SurfaceView surfacePreview = (SurfaceView)
                findViewById(R.id.surfacePreviewCapture);
        Button buttonShowPreview = (Button)
                findViewById(R.id.buttonShowPreview);

        /*if (MainActivity.currentCall == null ||
                MainActivity.currentCall.vidWin == null) {
            surfaceInVideo.setVisibility(View.GONE);
            buttonShowPreview.setVisibility(View.GONE);
        }*/
        if (Utils.getObjectInPrefs(AVCallsActivity.this) != null) {
            loggedSipAccount = Utils.getObjectInPrefs(AVCallsActivity.this);
            Log.d("AccountDetails", "" + loggedSipAccount.getIdUri() + "-" + loggedSipAccount.getUsername() + "-" + loggedSipAccount.getRegistrarUri());
        }
        Log.e("onCreateView", "========");
        if (loggedSipAccount != null && loggedSipAccount.getRegistrarUri().length() > 0) {
            String loggedAccountId = loggedSipAccount.getIdUri();

            //SipServiceInteractor.setVideoPreview(this,loggedAccountId,);
            // SipServiceInteractor.setAccount(this,loggedSipAccount);
        }


        setupVideoPreview(surfacePreview, buttonShowPreview);
        surfaceInVideo.getHolder().addCallback(this);
        surfacePreview.getHolder().addCallback(videoPreviewHandler);
        // handler_ = handler;
        /*if (new SipAccount(SipService.class,"").currentCall != null) {
            try {
                lastCallInfo = MainActivity.currentCall.getInfo();
                updateCallState(lastCallInfo);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            updateCallState(lastCallInfo);
        }*/


    }

    public void setupVideoPreview(SurfaceView surfacePreview,
                                  Button buttonShowPreview) {
        surfacePreview.setVisibility(videoPreviewHandler.videoPreviewActive ?
                View.VISIBLE : View.GONE);

        buttonShowPreview.setText(videoPreviewHandler.videoPreviewActive ?
                getString(R.string.hide_preview) :
                getString(R.string.show_preview));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        updateVideoWindow(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        event.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        event.unregister(this);
    }

    private void updateVideoWindow(boolean show) {

        SurfaceView surfaceInVideo = (SurfaceView)
                findViewById(R.id.surfaceIncomingVideo);

        VideoWindowHandle vidWH = new VideoWindowHandle();
        if (show) {
            vidWH.getHandle().setWindow(
                    surfaceInVideo.getHolder().getSurface());
        } else {
            vidWH.getHandle().setWindow(null);
        }
        try {
            //MainActivity.currentCall.vidWin.setWindow(vidWH);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
