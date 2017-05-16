package com.sip.softphone.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sip.softphone.R;
import com.sip.softphone.activities.CallsActivity;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.MediaStateListener;
import com.sip.softphone.common.Utils;
import com.sip.softphone.common.timerCountUpdate;
import com.sip.softphone.services.SipService;
import com.sip.softphone.sip.SipCall;
import com.sip.softphone.sip.SipServiceInteractor;
import com.sip.softphone.video.VideoPreviewHandler;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.VideoWindowHandle;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;

/**
 * Created by hari on 17/4/17.
 */

public class IncomingCallFragment extends Fragment implements timerCountUpdate, MediaStateListener {

    private static final String ACCOUNTID = "accountId";
    private static final String CALLNUMBER = "callNumber";
    private static final String CALLID = "callId";
    private static final String CALLERNAME = "callerName";
    private String accountId, callNumber, callerName;
    private int callId;
    private TextView mTxtFromCall, mTxtTimerCount, mTxtSpeaker, mTxtMute;
    ;
    private RelativeLayout mRLDeclineCall, mRLAcceptCall, mRLCallOptions,mRLAudioView;
    private LinearLayout mRLVideoView;
    private View view;
    private AudioManager audioManager;
    private  SurfaceView surfaceInVideo,surfacePreview;
    private Button buttonShowPreview;

    public boolean videoPreviewActive = false;
    private static CallInfo lastCallInfo;
    private SipCall sipCall;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CallsActivity) getActivity()).setCustomObjectListener(this);
    }

    public IncomingCallFragment getInstance(String accountId, String callNumber, int callId, String CallerName) {

        IncomingCallFragment fragment = new IncomingCallFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ACCOUNTID, accountId);
        bundle.putString(CALLNUMBER, callNumber);
        bundle.putInt(CALLID, callId);
        bundle.putString(CALLERNAME, CallerName);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_callincoming, container, false);

        SipService.getInstance().setMediaStateListener(this);
        initUi();
        getBundleData();
        clickListener();
        loudSpekerOn();
        return view;
    }

    private void loudSpekerOn() {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setSpeakerphoneOn(false);
    }

    private void getBundleData() {
        accountId = getArguments().getString(ACCOUNTID);
        callNumber = getArguments().getString(CALLNUMBER);
        callId = getArguments().getInt(CALLID);
        callerName = getArguments().getString(CALLERNAME);
        mTxtFromCall.setText(callerName);

    }

    private void clickListener() {

        mRLAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SipServiceInteractor.acceptIncomingCall(getActivity(), accountId, callId);
                mRLDeclineCall.setTag(Const.ACTIVE_CALL);
                mRLAcceptCall.setVisibility(View.GONE);
                mRLCallOptions.setVisibility(View.VISIBLE);
                audioManager.setSpeakerphoneOn(false);
                //SipServiceInteractor.getCallStatus(getActivity(), accountId, callId);
            }
        });

        mRLDeclineCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRLDeclineCall.getTag().toString().equalsIgnoreCase(Const.ACTIVE_CALL)) {
                    SipServiceInteractor.hangUpActiveCalls(getActivity(), accountId);
                    getActivity().finish();
                } else if (mRLDeclineCall.getTag().toString().equalsIgnoreCase(Const.NONACTIVE_CALL)) {
                    SipServiceInteractor.declineIncomingCall(getActivity(), accountId, callId);
                    getActivity().finish();
                }
            }
        });
        mTxtMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTxtMute.getText().toString().equalsIgnoreCase("Mute")) {
                   // mTxtMute.setBackgroundColor(Color.GRAY);
                    mTxtMute.setText("Unmute");
                    SipServiceInteractor.setCallMute(getActivity(), accountId, callId, true);
                } else {
                   // mTxtMute.setBackgroundColor(Color.TRANSPARENT);
                    mTxtMute.setText("Mute");
                    SipServiceInteractor.setCallMute(getActivity(), accountId, callId, false);
                }

            }
        });
        mTxtSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTxtSpeaker.getText().toString().equalsIgnoreCase("Speaker")) {

                  //  mTxtSpeaker.setBackgroundColor(Color.GRAY);
                    mTxtSpeaker.setText("Speaker Off");
                    audioManager.setSpeakerphoneOn(true);

                } else {
                   // mTxtSpeaker.setBackgroundColor(Color.TRANSPARENT);
                    mTxtSpeaker.setText("Speaker");
                    audioManager.setSpeakerphoneOn(false);
                }
                /*if (!audioManager.isSpeakerphoneOn()) {
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(true);
                   // audioManager.setMode(AudioManager.MODE_NORMAL);
                } else {
                    audioManager.setSpeakerphoneOn(false);
                }*/
            }
        });
    }

    private void initUi() {
        mTxtFromCall = (TextView) view.findViewById(R.id.callFrom);
        mRLAcceptCall = (RelativeLayout) view.findViewById(R.id.callAccept);
        mRLDeclineCall = (RelativeLayout) view.findViewById(R.id.callEnd);
        mTxtTimerCount = (TextView) view.findViewById(R.id.timecount);
        mTxtMute = (TextView) view.findViewById(R.id.mute);
        mTxtSpeaker = (TextView) view.findViewById(R.id.speaker);
        mRLCallOptions = (RelativeLayout) view.findViewById(R.id.optionLayout);
        mRLAudioView = (RelativeLayout)view.findViewById(R.id.audioView);
        mRLVideoView = (LinearLayout)view.findViewById(R.id.videoView);
        // mRLCallOptions.setVisibility(View.GONE);
        mRLAcceptCall.setVisibility(View.VISIBLE);
        mRLDeclineCall.setTag(Const.NONACTIVE_CALL);

         surfaceInVideo = (SurfaceView)
                view.findViewById(R.id.surfaceIncomingVideo);
         surfacePreview = (SurfaceView)
                view.findViewById(R.id.surfacePreviewCapture);
         buttonShowPreview = (Button)
                view.findViewById(R.id.buttonShowPreview);
    }

    @Override
    public void onReceiveTimerCount(String count) {
        mTxtTimerCount.setText(Utils.timeFormat(Integer.parseInt(count)));
    }

    @Override
    public void currentCall(SipCall call) {

        Log.e("INTEFARCE====>",""+call+"<->"+call.vidPrev+"<->"+call.vidWin);
        if(call != null && call.vidPrev != null && call.vidWin != null) {

            sipCall = call;
            mRLAudioView.setVisibility(View.GONE);
            mRLVideoView.setVisibility(View.VISIBLE);
            if (call == null ||
                    call.vidWin == null)
            {
                surfaceInVideo.setVisibility(View.GONE);
                buttonShowPreview.setVisibility(View.GONE);
            }
            setupVideoPreview(surfacePreview, buttonShowPreview);

            surfaceInVideo.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    updateVideoWindow(true);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    updateVideoWindow(false);
                }
            });
            surfacePreview.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    updateVideoPreview(holder);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    try {
                        sipCall.vidPrev.stop();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            });
            if (sipCall != null) {
                try {
                     lastCallInfo = sipCall.getInfo();
                    updateCallState(lastCallInfo);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                updateCallState(lastCallInfo);
            }

        }



     /*   try {
            CallInfo info  = call.getInfo();
            for (int i = 0; i < info.getMedia().size(); i++) {
               // Media media = getMedia(i);
                CallMediaInfo mediaInfo = info.getMedia().get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            Log.e("=====currentCall====",""+call.getInfo().getRemoteUri().toString());
        }catch (Exception e){

            Log.e("Exception",""+e.getMessage());

        }*/
    }

    public void setupVideoPreview(SurfaceView surfacePreview,
                                  Button buttonShowPreview)
    {
        surfacePreview.setVisibility(videoPreviewActive?
                View.VISIBLE:View.GONE);

        buttonShowPreview.setText(videoPreviewActive?
                getString(R.string.hide_preview):
                getString(R.string.show_preview));
    }

    public void updateVideoPreview(SurfaceHolder holder)
    {
        if (sipCall != null &&
                sipCall.vidWin != null &&
                sipCall.vidPrev != null)
        {
            if (videoPreviewActive) {
                VideoWindowHandle vidWH = new VideoWindowHandle();
                vidWH.getHandle().setWindow(holder.getSurface());
                VideoPreviewOpParam vidPrevParam = new VideoPreviewOpParam();
                vidPrevParam.setWindow(vidWH);
                try {
                    sipCall.vidPrev.start(vidPrevParam);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                try {
                    sipCall.vidPrev.stop();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
    private void updateVideoWindow(boolean show)
    {
        if (sipCall != null &&
                sipCall.vidWin != null &&
                sipCall.vidPrev != null)
        {
            VideoWindowHandle vidWH = new VideoWindowHandle();
            if (show) {
                vidWH.getHandle().setWindow(
                        surfaceInVideo.getHolder().getSurface());
            } else {
                vidWH.getHandle().setWindow(null);
            }
            try {
                sipCall.vidWin.setWindow(vidWH);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    private void updateCallState(CallInfo ci) {
        Button buttonHangup = (Button) view.findViewById(R.id.buttonHangup);
        Button buttonAccept = (Button) view.findViewById(R.id.buttonAccept);
        String call_state = "";

        if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAC) {
            buttonAccept.setVisibility(View.GONE);
        }

        if (ci.getState().swigValue() <
                pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue())
        {
            if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAS) {
                call_state = "Incoming call..";
		/* Default button texts are already 'Accept' & 'Reject' */
            } else {
                buttonHangup.setText("Cancel");
                call_state = ci.getStateText();
            }
        }
        else if (ci.getState().swigValue() >=
                pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue())
        {
            buttonAccept.setVisibility(View.GONE);
            call_state = ci.getStateText();
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                buttonHangup.setText("Hangup");
            } else if (ci.getState() ==
                    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
            {
                buttonHangup.setText("OK");
                call_state = "Call disconnected: " + ci.getLastReason();
            }
        }

       // tvPeer.setText(ci.getRemoteUri());
       // tvState.setText(call_state);
    }
}
