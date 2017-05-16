package com.sip.softphone.common;

import com.sip.softphone.sip.SipAccountData;

import java.util.ArrayList;

/**
 * Created by hari on 11/4/17.
 */

public class SipSingleton {
    public static SipSingleton sipSingleton;
    private ArrayList<SipAccountData> sipAccountList;

    public static SipSingleton getInstance() {


        if (sipSingleton == null) {
            sipSingleton = new SipSingleton();
        }
        return sipSingleton;
    }

    public ArrayList<SipAccountData> getSipAccountList() {
        return sipAccountList;
    }

    public void setSipAccountList(ArrayList<SipAccountData> sipAccountList) {
        this.sipAccountList = sipAccountList;
    }
}
