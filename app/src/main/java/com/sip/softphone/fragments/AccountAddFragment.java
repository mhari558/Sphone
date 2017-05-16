package com.sip.softphone.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sip.softphone.R;
import com.sip.softphone.activities.AccountSetting;
import com.sip.softphone.adapter.AccountListAdapter;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.RegisterStatusInterface;
import com.sip.softphone.common.Utils;
import com.sip.softphone.sip.SipAccountData;
import com.sip.softphone.sip.SipServiceInteractor;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.util.ArrayList;

/**
 * Created by hari on 6/4/17.
 */

public class AccountAddFragment extends Fragment implements RegisterStatusInterface {

   // private ListView mLvAccount;
    private View view;
    private Button mBtnAddAccount, mBtnRemoveAccount;
    private ArrayList<SipAccountData> accountListData;
    private AccountListAdapter adapter;
    private String accountId;
    private SipAccountData loggedSipAccount;
    private String sipAccountRegStatus = "State";
    private TextView mTxtUserId;
    private TextView mTXtStatus;
    private RelativeLayout relativeLayout;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreateView", "========");
        setHasOptionsMenu(true);
        ((AccountSetting) getActivity()).setRegisterStatue(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_addaccount, container, false);
        initUi();
        initListener();
        if (Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID) != null && Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID).length() > 0) {
            accountId = Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID);
            // mBtnAddAccount.setVisibility(View.GONE);
        } else {
        }

        return view;
    }

    private void initListener() {

        mBtnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment();
            }
        });
        mBtnRemoveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID) != null && Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID).length() > 0) {
                    accountId = Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID);
                    SipServiceInteractor.removeAccount(getActivity(), accountId);
                } else {
                    Toast.makeText(getActivity(), "No account to remove", Toast.LENGTH_SHORT).show();
                }

            }
        });

       /* mLvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                loadFragment();
            }
        });*/


    }

    private void initUi() {

        mBtnAddAccount = (Button) view.findViewById(R.id.addAccount);
        mBtnRemoveAccount = (Button) view.findViewById(R.id.removeAccount);
      //  mLvAccount = (ListView) view.findViewById(R.id.accountList);
        mTxtUserId = (TextView)view.findViewById(R.id.account);
        mTXtStatus = (TextView)view.findViewById(R.id.account_status);
        relativeLayout = (RelativeLayout)view.findViewById(R.id.includeLay);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progress);
        accountListData = new ArrayList<>();

        if (Utils.getObjectInPrefs(getActivity()) != null) {
            loggedSipAccount = Utils.getObjectInPrefs(getActivity());
            Log.d("AccountDetails", "" + loggedSipAccount.getIdUri() + "-" + loggedSipAccount.getUsername() + "-" + loggedSipAccount.getRegistrarUri());
        }
        if (loggedSipAccount != null && loggedSipAccount.getRegistrarUri().length() > 0) {
            SipServiceInteractor.setAccount(getActivity(), loggedSipAccount);
            SipServiceInteractor.getCodecPriorities(getActivity());
        }else {
            relativeLayout.setVisibility(View.GONE);
        }
    }

    private void loadAccoutInfo(String status) {

        if (Utils.getObjectInPrefs(getActivity()) != null) {
            loggedSipAccount = Utils.getObjectInPrefs(getActivity());
            Log.d("AccountDetails", "" + loggedSipAccount.getIdUri() + "-" + loggedSipAccount.getUsername() + "-" + loggedSipAccount.getRegistrarUri());
        }
        if (loggedSipAccount != null && loggedSipAccount.getRegistrarUri().length() > 0) {

            relativeLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTxtUserId.setText(loggedSipAccount.getUsername());
            switch (status) {
                case "State":
                    break;
                case "Registered":
                    mTxtUserId.setTextColor(Color.GREEN);
                    break;
                case "Failed":
                    mTxtUserId.setTextColor(Color.RED);
                    break;
                default:
                    break;
            }
            mProgressBar.setVisibility(View.GONE);
            mTXtStatus.setText(status);
        }else {
            relativeLayout.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void loadFragment() {
        FragmentManager f = getActivity().getSupportFragmentManager();
        AccountRegisterFragment add = new AccountRegisterFragment();
        f.beginTransaction().replace(R.id.content, add, add.getClass().getName()).addToBackStack(add.getClass().getName()).commit();
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
            accountConfig.setIdUri("sip:1001@172.168.10.115");
            accountConfig.getRegConfig().setRegistrarUri("sip:172.168.10.115");
            AuthCredInfo cred = new AuthCredInfo("1001", "*", "1001", 0, "1001");
            accountConfig.getSipConfig().getAuthCreds().add(cred);

            // Create the account
            // Myaccount acc = new Myaccount();
            // acc.create(accountConfig);
            Thread.sleep(10000);
            //  acc.delete();
            endpoint.libDestroy();
            endpoint.delete();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AccountSetting) getActivity()).setActionBatTitle(getString(R.string.my_account));
        if (adapter != null) {
            Log.e("ONRESUME", "========");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem rm = menu.findItem(R.id.removeAccount);
        rm.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeAccount:
                if (Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID) != null && Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID).length() > 0) {
                    accountId = Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID);
                    SipServiceInteractor.removeAccount(getActivity(), accountId);
                } else {
                    Toast.makeText(getActivity(), "No account to remove", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void getSipAccountStatus(String status) {
        Log.e("STATUS %%^&&in ADDDDDD", status);
        sipAccountRegStatus = status;
        loadAccoutInfo(status);
    }

    @Override
    public void getAccountRemoveStatus(boolean status) {
        if(status){
            relativeLayout.setVisibility(View.VISIBLE);
        }else {
            relativeLayout.setVisibility(View.GONE);
        }
    }
}


