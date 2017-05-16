package com.sip.softphone.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import com.sip.softphone.R;
import com.sip.softphone.common.Utils;

/**
 * Created by hari on 7/4/17.
 */

public class AlertDialogFragment extends DialogFragment {

    public static AlertDialogFragment newInstance(String title, String value) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("value", value);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String title = getArguments().getString("title");
        final String value = getArguments().getString("value");
        final EditText editText = new EditText(getActivity());
        editText.setText(value);
        editText.setSelection(editText.getText().length());
        //editText.requestFocus();
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent();
                                //Bundle bundle = new Bundle();
                                intent.putExtra("title", title);
                                Log.e("onCreateDialog", "" + editText.getText().toString());
                                intent.putExtra("data", editText.getText().toString());
                                //intent.putExtras(bundle);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), 12, intent);
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // ((AccountRegisterFragment)getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }
}
