package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;


public class EditGestureDialog extends DialogFragment {
    private int pos;
    private DialogActionOnClick actionOnClick;
    private String title;

    EditGestureDialog(DialogActionOnClick actionOnClick, int pos, String title) {
        this.pos = pos;
        this.actionOnClick = actionOnClick;
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String edit = "EDIT";
        final String rename = "RENAME";
        final String delete = "DELETE";

        final String[] items = { rename, edit, delete };
        builder.setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actionOnClick.onClick(items[which], pos);
            }
        })
            .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dismiss();
                }
            });

        final AlertDialog dialog = builder.create();
        dialog.show();

        // Create the AlertDialog object and return it
        return dialog;
    }
}