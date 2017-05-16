package com.sip.softphone;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.sip.softphone.recievers.BroadcastReceiverEvent;
import com.sip.softphone.sip.CodecPriority;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;

/**
 * Created by hari on 6/4/17.
 */

public class SoftphoneApp extends Application {

    private BroadcastReceiverEvent sipEvents = new BroadcastReceiverEvent() {

        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
               // Toast.makeText(SoftphoneApp.this, "Softphone Registered", Toast.LENGTH_SHORT).show();
            } else {

            }
        }

        @Override
        public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {

            for (CodecPriority codec : codecPriorities) {
                Log.e("SSCodecPriority===>", "$" + codec + "<-->" + codec.getCodecName());
                if (codec.getCodecName().startsWith("G.711a")) {
                    codec.setPriority(CodecPriority.PRIORITY_MAX);
                    Log.e("SSIN CodecPriority===>", "$" + codec.getCodecName());
                } else {
                    Log.e("SSElse CodecPriorit===>", "$" + codec.getCodecName());
                    codec.setPriority(CodecPriority.PRIORITY_DISABLED);
                }
            }
            SipServiceInteractor.setCodecPriorities(SoftphoneApp.this, codecPriorities);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sipEvents.register(this);
    }
}
