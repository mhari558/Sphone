package com.sip.softphone.common;

import com.sip.softphone.sip.SipCall;

/**
 * Created by hari on 10/5/17.
 */

public interface MediaStateListener {
     void currentCall(SipCall call);
    // void  videoPreview(Video)
}
