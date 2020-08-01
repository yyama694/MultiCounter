package org.yyama.multicounter.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.yyama.multicounter.R;
import org.yyama.multicounter.model.CounterGroup;

public class ChangeCounterNameDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.change_counter_name_dialog, null);
        final String id = getArguments().getString("id");
        final String beforeName = getArguments().getString("beforeName");

        builder.setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView tv = v.findViewById(R.id.change_name_edit_text);
                        ((MultiCounterActivity)getActivity()).changeName(id,tv.getText().toString());
                    }
                }).setNegativeButton("Cancel", null);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                EditText et = v.findViewById(R.id.change_name_edit_text);
                et.setText(beforeName);
                et.setSelection(et.length());
            }
        });
        return dialog;
    }
}
