package com.sip.softphone.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sip.softphone.R;
import com.sip.softphone.activities.AccountSetting;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.Utils;
import com.sip.softphone.sip.SipAccountData;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_transport_type_e;

/**
 * Created by hari on 6/4/17.
 */
public class AccountRegisterFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mRLayAccountName;
    private RelativeLayout mRLayAccountUser;
    private RelativeLayout mRLayAccountServer;
    private RelativeLayout mRLayAccountPassword;

    private TextView mTxtAccountName;
    private TextView mTxtAccountUser;
    private TextView mTxtAccountServer;
    private TextView mTxtAccountPassword;

    private Button save;
    private Button cancel;

    private View view;

    private SipAccountData mSipAccount;

    private SipAccountData loggedSipAccount;


    @Override
    public void onResume() {
        super.onResume();
        ((AccountSetting) getActivity()).setActionBatTitle(getString(R.string.add_account));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_register_account, container, false);

        init();
        initListener();
        if (Utils.getObjectInPrefs(getActivity()) != null) {
            loggedSipAccount = Utils.getObjectInPrefs(getActivity());
            Log.d("AccountDetails", "" + loggedSipAccount.getAccountName() + "<---->" + loggedSipAccount.getIdUri() + "-" + loggedSipAccount.getUsername() + "-" + loggedSipAccount.getRegistrarUri());
            mTxtAccountName.setText(loggedSipAccount.getAccountName());
            mTxtAccountUser.setText(loggedSipAccount.getUsername());
            mTxtAccountServer.setText(loggedSipAccount.getHost());
            mTxtAccountPassword.setText(loggedSipAccount.getPassword());
        }

        return view;
    }

    private void initListener() {
        mRLayAccountName.setOnClickListener(this);
        mRLayAccountUser.setOnClickListener(this);
        mRLayAccountServer.setOnClickListener(this);
        mRLayAccountPassword.setOnClickListener(this);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void init() {

        mRLayAccountName = (RelativeLayout) view.findViewById(R.id.accountName);
        mRLayAccountUser = (RelativeLayout) view.findViewById(R.id.accountUser);
        mRLayAccountServer = (RelativeLayout) view.findViewById(R.id.accountServer);
        mRLayAccountPassword = (RelativeLayout) view.findViewById(R.id.accountPassword);
        mTxtAccountName = (TextView) view.findViewById(R.id.idAccName);
        mTxtAccountUser = (TextView) view.findViewById(R.id.idAccountUser);
        mTxtAccountServer = (TextView) view.findViewById(R.id.idAccountServer);
        mTxtAccountPassword = (TextView) view.findViewById(R.id.idAccountPassword);
        save = (Button) view.findViewById(R.id.save);
        cancel = (Button) view.findViewById(R.id.cancel);

    }

    private void createEndpointConfiguration() {
        try {
            Endpoint endpoint = new Endpoint();
            endpoint.libCreate();
            EpConfig epConfig = new EpConfig();
            endpoint.libInit(epConfig);
            TransportConfig transportConfig = new TransportConfig();
            transportConfig.setPort(5060);
            endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
            endpoint.libStart();

            AccountConfig accountConfig = new AccountConfig();
            Log.e("+===URI", "" + "sip:" + mTxtAccountName.getText().toString() + "@" + mTxtAccountServer.getText().toString());
            accountConfig.setIdUri("sip:" + mTxtAccountName.getText().toString() + "@" + mTxtAccountServer.getText().toString());
            Log.e("+===setRegistrarUri", "" + "sip:" + mTxtAccountServer.getText().toString());
            accountConfig.getRegConfig().setRegistrarUri("sip:" + mTxtAccountServer.getText().toString());
            AuthCredInfo cred = new AuthCredInfo("1001", "*", mTxtAccountUser.getText().toString(), 0, mTxtAccountPassword.getText().toString());
            accountConfig.getSipConfig().getAuthCreds().add(cred);
            // Create the account
            //  Myaccount acc = new Myaccount();
            // acc.create(accountConfig);
            Thread.sleep(10000);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Const.PREFS_NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(Const.PREFS_KEY_ACCOUNTS, mTxtAccountUser.getText().toString()).apply();

            Thread.sleep(10000);
            // acc.addOutgoingCall("1001");

         /*   acc.delete();
            endpoint.libDestroy();
            endpoint.delete();*/


        } catch (Exception e) {
            Log.e("Erroor in registration", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.accountName:
                showDialogInterface(getString(R.string.title_acc_name), mTxtAccountName.getText().toString());
                break;
            case R.id.accountUser:
                showDialogInterface(getString(R.string.title_user), mTxtAccountUser.getText().toString());
                break;
            case R.id.accountServer:
                showDialogInterface(getString(R.string.title_server), mTxtAccountServer.getText().toString());
                break;
            case R.id.accountPassword:
                showDialogInterface(getString(R.string.title_password), mTxtAccountPassword.getText().toString());
                break;
            case R.id.save:
                if (validate()) {
                    //createEndpointConfiguration();
                    mSipAccount = new SipAccountData();

                    if (!mTxtAccountServer.getText().toString().isEmpty()) {
                        mSipAccount.setHost(mTxtAccountServer.getText().toString())
                                .setPort(Integer.valueOf(5060))
                                .setTcpTransport(true)
                                .setAccountName(mTxtAccountName.getText().toString())
                                .setUsername(mTxtAccountUser.getText().toString())
                                .setPassword(mTxtAccountPassword.getText().toString())
                                .setRealm(mTxtAccountServer.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "Please enter server details", Toast.LENGTH_SHORT).show();
                        return;

                    }/*else{
                    mSipAccount.setHost("192.168.1.154")
                            .setPort(5060)
                            .setTcpTransport(true)
                            .setUsername("200")
                            .setPassword("password200")
                            .setRealm("devel.it");
                }*/
                    Log.e("ACCOUNAMe", "" + mSipAccount.getAccountName());
                    SipServiceInteractor.setAccount(getActivity(), mSipAccount);
                    SipServiceInteractor.getCodecPriorities(getActivity());
                    getActivity().getSupportFragmentManager().popBackStack();
                    // loadFragment();
                    // SipServiceInteractor.getCodecPriorities(getActivity());

                  /*  Intent it = new Intent(getActivity(), TabsMainActivity.class);
                    startActivity(it);
                    getActivity().finish();*/
                }
                //  createEndpointConfiguration();

                break;
            case R.id.cancel:

                break;
            default:

        }

    }

    private boolean validate() {

        if (TextUtils.isEmpty(mTxtAccountName.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter Account Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mTxtAccountUser.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter UserName", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mTxtAccountServer.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter ServerIP", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mTxtAccountPassword.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {

            return true;
        }
    }

    private void showDialogInterface(String name, String value) {

        AlertDialogFragment alert = AlertDialogFragment.newInstance(name, value);
        alert.setTargetFragment(this, 12);
        alert.show(getFragmentManager(), name);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12) {
            Log.d("FragmentAlertDialog", "Positive click!");
            if (data.getStringExtra("title") != null) {
                Log.d("FragmentAlialog data", data.getStringExtra("data"));
                String value = data.getStringExtra("data");
                switch (data.getStringExtra("title")) {
                    case "Account Name":
                        Log.e("onActivityResult", "" + value);
                        mTxtAccountName.setText(value);
                        break;
                    case "User":
                        mTxtAccountUser.setText(value);
                        break;
                    case "Server":
                        mTxtAccountServer.setText(value);
                        break;
                    case "Password":
                        mTxtAccountPassword.setText(value);
                        break;
                    default:

                }
            }
        }
    }
}


