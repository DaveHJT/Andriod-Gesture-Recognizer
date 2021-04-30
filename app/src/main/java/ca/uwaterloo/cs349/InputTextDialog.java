package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;

// referenced the android developers document:
// https://developer.android.com/guide/topics/ui/dialogs

public class InputTextDialog extends DialogFragment {
    private int message;
    private DialogActionOnClick actionOnClick;
    private int pos;

    InputTextDialog(int message, DialogActionOnClick actionOnClick, int pos) {
        super();
        this.message = message;
        this.actionOnClick = actionOnClick;
        this.pos = pos;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // add the edit text field
        final EditText textInput = new EditText(getContext());
        builder.setView(textInput);

        builder.setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actionOnClick.onClick(textInput.getText().toString(), pos);
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        // disable the dialog positive button
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // check whether the text field is empty
                if (TextUtils.isEmpty(s)) {
                    positiveButton.setEnabled(false);
                } else {
                    positiveButton.setEnabled(true);
                }
            }
        });

        // Create the AlertDialog object and return it
        return dialog;
    }
}
