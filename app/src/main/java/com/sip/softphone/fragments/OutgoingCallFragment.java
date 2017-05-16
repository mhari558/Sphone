package com.sip.softphone.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sip.softphone.R;
import com.sip.softphone.activities.CallsActivity;
import com.sip.softphone.common.Utils;
import com.sip.softphone.common.timerCountUpdate;
import com.sip.softphone.sip.SipServiceInteractor;

/**
 * Created by hari on 17/4/17.
 */

public class OutgoingCallFragment extends Fragment implements timerCountUpdate {

    private static final String ACCOUNTID = "accountId";
    private static final String CALLNUMBER = "callNumber";
    private static final String CALLID = "callId";
    private static final String CALLERNAME = "callerName";
    private String accountId, callNumber;
    private int callId;
    private TextView mTxtToCall, mTxtCount, mTxtSpeaker, mTxtMute;
    private RelativeLayout mImgDeclineCall;
    private AudioManager audioManager;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CallsActivity) getActivity()).setCustomObjectListener(this);
    }

    public OutgoingCallFragment getInstance(String accountId, String callNumber, int callId) {

        OutgoingCallFragment fragment = new OutgoingCallFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ACCOUNTID, accountId);
        bundle.putString(CALLNUMBER, callNumber);
        bundle.putInt(CALLID, callId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calloutgoing, container, false);
        initUi();
        getBundleData();
        clickListener();
        loudSpekerOn();
        return view;
    }

    private void loudSpekerOn() {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        //  audioManager.setSpeakerphoneOn(true);
    }

    private void clickListener() {

        mImgDeclineCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SipServiceInteractor.hangUpCall(getActivity(), accountId, callId);
                getActivity().finish();
            }
        });
        mTxtSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTxtSpeaker.getText().toString().equalsIgnoreCase("Speaker")) {

                   // mTxtSpeaker.setBackgroundColor(Color.GRAY);
                    mTxtSpeaker.setText("Speaker Off");
                    audioManager.setSpeakerphoneOn(true);

                } else {
                   // mTxtSpeaker.setBackgroundColor(Color.TRANSPARENT);
                    mTxtSpeaker.setText("Speaker");
                    audioManager.setSpeakerphoneOn(false);
                }
                /*//Log.e("SPEEKAER ON?OFF", "" + audioManager.isSpeakerphoneOn());
                if (!audioManager.isSpeakerphoneOn()) {
                    // audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(true);
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                } else {
                    audioManager.setSpeakerphoneOn(false);
                }*/

            }
        });
        mTxtMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  SipServiceInteractor.setCallMute(getActivity(), accountId, callId, true);
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
    }

    private void getBundleData() {

        accountId = getArguments().getString(ACCOUNTID);
        callNumber = getArguments().getString(CALLNUMBER);
        callId = getArguments().getInt(CALLID);
        mTxtToCall.setText(callNumber);
    }

    private void initUi() {
        mTxtToCall = (TextView) view.findViewById(R.id.toCall);
        mImgDeclineCall = (RelativeLayout) view.findViewById(R.id.declineCall);
        mTxtCount = (TextView) view.findViewById(R.id.count);
        mTxtMute = (TextView) view.findViewById(R.id.mute);
        mTxtSpeaker = (TextView) view.findViewById(R.id.speaker);

    }

    @Override
    public void onReceiveTimerCount(String count) {
        mTxtCount.setText(Utils.timeFormat(Integer.parseInt(count)));
    }
}
