package com.sip.softphone.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sip.softphone.R;
import com.sip.softphone.common.Const;
import com.sip.softphone.common.Utils;
import com.sip.softphone.sip.SipServiceInteractor;

/**
 * Created by hari on 10/4/17.
 */

public class DialerFragment extends Fragment implements View.OnClickListener {
    public static final String ACCOUNTID = "accountId";
    private View view;
    private Button mBtnMakeCall;
    private FrameLayout frameLayout;
    private ImageView cancel, mImgEditNumber;
    private Button mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9, mBtn0, mBtnStar, mBtnHash;
    private TextView mTxtNumber;
    private SharedPreferences preferences;
    private String loggedAccountId;

    public static DialerFragment getInstance(String accountId) {
        DialerFragment dialerFragment = new DialerFragment();
        Bundle b = new Bundle();
        b.putString(ACCOUNTID, accountId);
        dialerFragment.setArguments(b);
        return dialerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dialer1, container, false);
        initUi();
        listeners();
        loggedAccountId = getArguments().getString(ACCOUNTID);
        Log.e(" Dial DATA", "" + loggedAccountId);

        mBtnMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayDialog();

            }


        });
        return view;
    }
    private void displayDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Audio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID) != null && Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID).length() > 0) {
                    loggedAccountId = Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID);
                    if (mTxtNumber.getText().toString().length() > 0) {
                        SipServiceInteractor.makeCall(getActivity(), loggedAccountId, mTxtNumber.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "Please enter number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please register account from the Settings", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }
        }).setNegativeButton("Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID) != null && Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID).length() > 0) {
                    loggedAccountId = Utils.getStringFromPrefs(getActivity(), Const.PREFS_ACCOUNT_ID);
                    if (mTxtNumber.getText().toString().length() > 0) {
                        SipServiceInteractor.makeVideoCall(getActivity(),loggedAccountId,mTxtNumber.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "Please enter number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please register account from the Settings", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }).create().show();
    }

    private void listeners() {
        mBtn1.setClickable(true);
        mBtn2.setClickable(true);
        mBtn3.setClickable(true);
        mBtn4.setClickable(true);
        mBtn5.setClickable(true);
        mBtn6.setClickable(true);
        mBtn7.setClickable(true);
        mBtn8.setClickable(true);
        mBtn9.setClickable(true);
        mBtn0.setClickable(true);
        mBtnStar.setClickable(true);
        mBtnHash.setClickable(true);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
        mBtn3.setOnClickListener(this);
        mBtn4.setOnClickListener(this);
        mBtn5.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
        mBtn7.setOnClickListener(this);
        mBtn8.setOnClickListener(this);
        mBtn9.setOnClickListener(this);
        mBtn0.setOnClickListener(this);
        mBtnStar.setOnClickListener(this);
        mBtnHash.setOnClickListener(this);
        mImgEditNumber.setOnClickListener(this);

    }

    private void initUi() {

        mBtnMakeCall = (Button) view.findViewById(R.id.makeCall);
        //frameLayout = (FrameLayout) view.findViewById(R.id.dialFragment);
        cancel = (ImageView) view.findViewById(R.id.cancel);
        mBtn1 = (Button) view.findViewById(R.id.one);
        mBtn2 = (Button) view.findViewById(R.id.two);
        mBtn3 = (Button) view.findViewById(R.id.three);
        mBtn4 = (Button) view.findViewById(R.id.four);
        mBtn5 = (Button) view.findViewById(R.id.five);
        mBtn6 = (Button) view.findViewById(R.id.six);
        mBtn7 = (Button) view.findViewById(R.id.seven);
        mBtn8 = (Button) view.findViewById(R.id.eight);
        mBtn9 = (Button) view.findViewById(R.id.nine);
        mBtn0 = (Button) view.findViewById(R.id.zero);
        mBtnStar = (Button) view.findViewById(R.id.star);
        mBtnHash = (Button) view.findViewById(R.id.hash);
        mTxtNumber = (TextView) view.findViewById(R.id.number);
        mImgEditNumber = (ImageView) view.findViewById(R.id.numberEdit);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.one:
                mTxtNumber.append("1");
                break;
            case R.id.two:
                mTxtNumber.append("2");
                break;
            case R.id.three:
                mTxtNumber.append("3");
                break;
            case R.id.four:
                mTxtNumber.append("4");
                break;
            case R.id.five:
                mTxtNumber.append("5");
                break;
            case R.id.six:
                mTxtNumber.append("6");
                break;
            case R.id.seven:
                mTxtNumber.append("7");
                break;
            case R.id.eight:
                mTxtNumber.append("8");
                break;
            case R.id.nine:
                mTxtNumber.append("9");
                break;
            case R.id.zero:
                mTxtNumber.append("0");
                break;
            case R.id.star:
                mTxtNumber.append("*");
                break;
            case R.id.hash:
                mTxtNumber.append("#");
                break;
            case R.id.numberEdit:
                if (mTxtNumber.getText().toString().length() > 0) {
                    String s = mTxtNumber.getText().toString();
                    s = s.substring(0, s.length() - 1);
                    mTxtNumber.setText(s);
                } else {
                    return;
                }
                break;
            default:
        }

    }
}
