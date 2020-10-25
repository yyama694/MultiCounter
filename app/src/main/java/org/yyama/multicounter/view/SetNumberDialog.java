package org.yyama.multicounter.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.yyama.multicounter.R;

public class SetNumberDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.set_number_dialog, null);
        final String id = getArguments().getString("id");
        builder.setView(v)
                .setPositiveButton(getString(R.string.ok), null).setNegativeButton(getString(R.string.cancel), null);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        final Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button button2 = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editText = getDialog().findViewById(R.id.set_number_edit_text);
                        String number = editText.getText().toString();
                        if (!isNumber(number)) {
                            Toast.makeText(view.getContext(), getString(R.string.enter_an_integer),Toast.LENGTH_SHORT).show();
                            editText.setText("");
                            return;
                        }
                        int j = Integer.valueOf(number);
                        MainActivity ma = (MainActivity) getActivity();
                        ma.onClickSetNumberDialogOkButton(id, j);
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    private boolean isNumber(String num) {
        try {
            int i = Integer.valueOf(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
