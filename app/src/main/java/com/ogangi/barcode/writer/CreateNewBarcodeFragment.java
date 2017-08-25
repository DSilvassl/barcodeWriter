package com.ogangi.barcode.writer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by jmtt on 8/24/17.
 *
 */

public class CreateNewBarcodeFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    @Nullable
    private CreateBarcodeListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.new_barcode, null))
            .setTitle("Create New Barcode")
            // Add action buttons
            .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    if (listener != null) {
                        String mFormat = ((Spinner) getDialog().findViewById(R.id.new_format)).getSelectedItem().toString();
                        String mMessage = ((EditText) getDialog().findViewById(R.id.new_message)).getText().toString();
                        String mAltText = ((EditText) getDialog().findViewById(R.id.new_alt_text)).getText().toString();

                        // create new barcode
                        Barcode barcode = new Barcode(mFormat, mMessage, mAltText);
                        storeBarcode(barcode);
                        listener.onBarcodeCreate(barcode);
                    }
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    CreateNewBarcodeFragment.this.getDialog().cancel();
                    if (listener != null) {
                        listener.onBarcodeCancel();
                    }
                }
            });

        return builder.create();
    }



    //En este metodo es donde se asigna el listener, el onAttach se llama cuando aparece el bottomSheet
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            listener = (CreateBarcodeListener) parent;
        } else {
            listener = (CreateBarcodeListener) context;
        }
    }

    //El onDetach se llama cuando desaparece el bottomSheet
    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private void storeBarcode(Barcode barcode) {
        Context context = getContext().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("BarcodeDB", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(String.valueOf(barcode.hashCode()), barcode.toJson()).apply();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CreateBarcodeListener {
        public void onBarcodeCreate(Barcode barcode);
        public void onBarcodeCancel();
    }

}
