package com.example.quiz_sheet_remote_control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GetStringDialogFragment extends DialogFragment {

    private Dialog dialog;
    private LinearLayout layout;
    private EditText nameEditText;
    private String oldName;
    private DialogInterface.OnClickListener myClickListener;
    private Context context;

    public GetStringDialogFragment(Context con, DialogInterface.OnClickListener listener, String on)
    {
        myClickListener = listener;
        oldName = on;
        context = con;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        layout = (LinearLayout) inflater.inflate(R.layout.enter_name_dialog_layout, null);
        nameEditText = layout.findViewById(R.id.new_name_dialog_field);
        nameEditText.setText(oldName);
        nameEditText.selectAll();
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);

        nameEditText.requestFocus();

        builder.setView(layout);

        builder.setPositiveButton(R.string.ok_button_text, myClickListener);
        builder.setNegativeButton(R.string.cancel_button_text, myClickListener);

        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        return dialog;
    }
}
