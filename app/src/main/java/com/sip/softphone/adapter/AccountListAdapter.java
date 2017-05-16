package com.sip.softphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sip.softphone.R;
import com.sip.softphone.sip.SipAccountData;

import java.util.ArrayList;

/**
 * Created by hari on 11/4/17.
 */

public class AccountListAdapter extends BaseAdapter {
    private ArrayList<SipAccountData> accountList;
    private Context c;
    private String status;

    public AccountListAdapter(Context c, ArrayList<SipAccountData> accountList, String sipAccountRegStatus) {

        this.c = c;
        this.accountList = accountList;
        this.status = sipAccountRegStatus;
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            view = inflater.inflate(R.layout.custom_list, null);
        } else {
            view = convertView;
        }
        TextView account = (TextView) view.findViewById(R.id.account);
        TextView accountStatus = (TextView) view.findViewById(R.id.account_status);
        account.setText(accountList.get(position).getUsername());

        switch (status) {
            case "State":
                break;
            case "Registered":
                account.setTextColor(Color.GREEN);
                accountStatus.setText(status);
                break;
            case "Failed":
                account.setTextColor(Color.RED);
                accountStatus.setText(status);
                break;
            default:
                break;
        }
        return view;
    }
}
