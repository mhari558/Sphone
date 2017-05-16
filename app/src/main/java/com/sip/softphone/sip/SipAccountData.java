package com.sip.softphone.sip;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountVideoConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.pj_qos_type;

/**
 * Created by hari on 10/4/17.
 */
public class SipAccountData implements Parcelable {

    public static final String AUTH_TYPE_DIGEST = "digest";
    public static final String AUTH_TYPE_PLAIN = "plain";
    // This is used to regenerate the object.
    // All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<SipAccountData> CREATOR =
            new Parcelable.Creator<SipAccountData>() {
                @Override
                public SipAccountData createFromParcel(final Parcel in) {
                    return new SipAccountData(in);
                }

                @Override
                public SipAccountData[] newArray(final int size) {
                    return new SipAccountData[size];
                }
            };
    private String username;
    private String password;
    private String realm;
    private String host;
    private String accountName;
    private long port = 5060;
    private boolean tcpTransport = false;
    private String authenticationType = AUTH_TYPE_DIGEST;


    public SipAccountData() {
    }

    private SipAccountData(Parcel in) {
        username = in.readString();
        accountName = in.readString();
        password = in.readString();
        realm = in.readString();
        host = in.readString();
        port = in.readLong();
        tcpTransport = in.readByte() == 1;
        authenticationType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeString(username);
        parcel.writeString(accountName);
        parcel.writeString(password);
        parcel.writeString(realm);
        parcel.writeString(host);
        parcel.writeLong(port);
        parcel.writeByte((byte) (tcpTransport ? 1 : 0));
        parcel.writeString(authenticationType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUsername() {
        return username;
    }

    public SipAccountData setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public SipAccountData setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRealm() {
        return realm;
    }

    public SipAccountData setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    public String getHost() {
        return host;
    }

    public SipAccountData setHost(String host) {
        this.host = host;
        return this;
    }

    public long getPort() {
        return port;
    }

    public SipAccountData setPort(long port) {
        this.port = port;
        return this;
    }

    public boolean isTcpTransport() {
        return tcpTransport;
    }

    public SipAccountData setTcpTransport(boolean tcpTransport) {
        this.tcpTransport = tcpTransport;
        return this;
    }

    public String getIdUri() {
        if ("*".equals(realm))
            return "sip:" + username;
        return "sip:" + username + "@" + realm;
    }

    public String getRegistrarUri() {
        return "sip:" + host;
    }

    public String getProxyUri() {
        StringBuilder proxyUri = new StringBuilder();

        proxyUri.append("sip:").append(host).append(":").append(port);

        if (tcpTransport) {
            proxyUri.append(";transport=tcp");
        }

        return proxyUri.toString();
    }

    public boolean isValid() {
        return ((username != null) && !username.isEmpty()
                && (password != null) && !password.isEmpty()
                && (host != null) && !host.isEmpty()
                && (realm != null) && !realm.isEmpty());
    }

    protected AccountConfig getAccountConfig() {


        Log.e("+===URI", "" + getIdUri() + "<-->" + getRegistrarUri());

        AccountConfig accountConfig = new AccountConfig();
        accountConfig.getMediaConfig().getTransportConfig().setQosType(pj_qos_type.PJ_QOS_TYPE_VOICE);
        accountConfig.setIdUri(getIdUri());
        accountConfig.getRegConfig().setRegistrarUri(getRegistrarUri());
        AccountVideoConfig accountVideoConfig = new AccountVideoConfig();
          accountVideoConfig.setAutoShowIncoming(true);
          accountVideoConfig.setAutoTransmitOutgoing(true);
        accountConfig.setVideoConfig(accountVideoConfig);
      /*  AccountConfig accountConfig = new AccountConfig();
        Log.e("+===URI", "" + getIdUri());
        accountConfig.setIdUri(getIdUri());
        //accountConfig.
        Log.e("+===setRegistrarUri", "" + getRegistrarUri() + "UNAME " + getUsername() + "PAssword" + getPassword());
        accountConfig.getRegConfig().setRegistrarUri(getRegistrarUri());
*/
       /* AuthCredInfo cred = new AuthCredInfo(authenticationType, "*",
                getUsername(), 0, getPassword());*/
        AuthCredInfo cred = new AuthCredInfo(authenticationType, "*",
                getUsername(), 0, getPassword());
        accountConfig.getSipConfig().getAuthCreds().add(cred);
      //  accountConfig.getSipConfig().getProxies().add(getProxyUri());

        return accountConfig;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public SipAccountData setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SipAccountData that = (SipAccountData) o;

        return getIdUri().equals(that.getIdUri());

    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + realm.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + (int) (port ^ (port >>> 32));
        result = 31 * result + (tcpTransport ? 1 : 0);
        return result;
    }

    public String getAccountName() {
        return accountName;
    }

    public SipAccountData setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }
}
