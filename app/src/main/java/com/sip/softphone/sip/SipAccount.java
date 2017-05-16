package com.sip.softphone.sip;

import android.util.Log;

import com.sip.softphone.bean.CallerInfo;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.Utils;
import com.sip.softphone.services.SipService;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.CallVidSetStreamParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_call_vid_strm_op;

import java.util.HashMap;
import java.util.Set;

import roboguice.RoboGuice;

/**
 * Created by hari on 10/4/17.
 */

public class SipAccount extends Account {

    private HashMap<Integer, SipCall> activeCalls = new HashMap<>();
    private SipAccountData data;
    private SipService service;

    public SipAccount(SipService service, SipAccountData data) {
        super();
        this.service = service;
        this.data = data;
    }

    public SipService getService() {
        return service;
    }

    public SipAccountData getData() {
        return data;
    }

    public void create() throws Exception {
        create(data.getAccountConfig());
    }

    public void removeCall(int callId) {
        SipCall call = activeCalls.get(callId);

        if (call != null) {
            Log.d(Const.TAG, "Removing call with ID: " + callId);
            activeCalls.remove(callId);
        }
    }

    public SipCall getCall(int callId) {
        return activeCalls.get(callId);
    }

    public Set<Integer> getCallIDs() {
        return activeCalls.keySet();
    }

    public SipCall addIncomingCall(int callId) {

        SipCall call = new SipCall(this, callId);
        activeCalls.put(callId, call);
        Log.d(Const.TAG, "Added incoming call with ID " + callId + " to " + data.getIdUri());
        return call;
    }

    public SipCall addOutgoingCall(final String numberToDial,String host) {

        SipCall call = new SipCall(this);
        CallOpParam callOpParam = new CallOpParam();
        CallVidSetStreamParam callVidSetStreamParam = new CallVidSetStreamParam();
        try {
            String number = "sip:" + numberToDial + "@" + host;//"172.168.10.115";
            Log.d(Const.TAG, "addOutgoingCall: " + number);
          /*  if (numberToDial.startsWith("sip:")) {
                call.makeCall(numberToDial, callOpParam);
            } else {
                if ("*".equals(data.getRealm())) {
                    call.makeCall("sip:" + numberToDial, callOpParam);
                } else {
                    call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                }
            }*/
            //call.makeCall("sip:1000@172.168.10.115", callOpParam);
            call.makeCall(number, callOpParam);

            activeCalls.put(call.getId(), call);
            Log.d(Const.TAG, "New outgoing call with ID: " + call.getId());

            return call;

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while making outgoing call", exc);
            return null;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SipAccount that = (SipAccount) o;

        return data.equals(that.data);

    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        Log.d("Success", "============================");
        service.getBroadcastEmitter()
                .registrationState(data.getIdUri(), prm.getCode().swigValue());
    }


    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {


        SipCall call = addIncomingCall(prm.getCallId());

        if (activeCalls.size() > 1) {
            call.declineIncomingCall();
            Log.d(Const.TAG, "sending busy to call ID: " + prm.getCallId());
            //TODO: notification of missed call
            return;
        }

        try {
            // Answer with 180 Ringing
            CallOpParam callOpParam = new CallOpParam();
            callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            call.answer(callOpParam);
            Log.d(Const.TAG, "Sending 180 ringing");
            VideoPreviewOpParam vidi = new VideoPreviewOpParam();


            service.startRingtone();

            CallerInfo contactInfo = new CallerInfo(call.getInfo());

            service.getBroadcastEmitter()
                    .incomingCall(data.getIdUri(), prm.getCallId(),
                            contactInfo.getDisplayName(), contactInfo.getRemoteUri());

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while getting call info", exc);
        }

    }

    public SipCall addOutgoingVideoCall(String numberToDial, String host) {

        SipCall call = new SipCall(this);
        CallOpParam callOpParam = new CallOpParam();
        try {
            String number = "sip:" + numberToDial + "@" + host;//"172.168.10.115";
            Log.d(Const.TAG, "addOutgoingCall: " + number);
          /*  if (numberToDial.startsWith("sip:")) {
                call.makeCall(numberToDial, callOpParam);
            } else {
                if ("*".equals(data.getRealm())) {
                    call.makeCall("sip:" + numberToDial, callOpParam);
                } else {
                    call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                }
            }*/
            //call.makeCall("sip:1000@172.168.10.115", callOpParam);

            CallSetting callsetting = new CallSetting();
            callsetting.setVideoCount(1);
            call.makeCall(number, callOpParam);
            ///call.vidSetStream(pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_ADD,callVidSetStreamParam);

            activeCalls.put(call.getId(), call);
            Log.d(Const.TAG, "New outgoing call with ID: " + call.getId());

            return call;

        } catch (Exception exc) {
            Log.e(Const.TAG, "Error while making outgoing call", exc);
            return null;
        }
    }
}
