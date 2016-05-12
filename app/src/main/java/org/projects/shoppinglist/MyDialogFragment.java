package org.projects.shoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by tobias on 04-04-2016.
 */
public class MyDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Here we create a new dialogbuilder;
        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());
        alert.setTitle("Delete Confirmation");
        alert.setMessage("Are you sure?");
        alert.setPositiveButton("Yes", pListener);
        alert.setNegativeButton("No", nListener);

        return alert.create();
    }

    //This is our positive listener for when the user presses
    //the yes button
    DialogInterface.OnClickListener pListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // these will be executed when user click Yes button
            positiveClick();
        }
    };

    //This is our negative listener for when the user presses
    //the no button.
    DialogInterface.OnClickListener nListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // these will be executed when user click No button
            negativeClick();
        }
    };

    //These two methods are empty, because they will
    //be overridden
    protected void positiveClick()
    {

    }
    protected void negativeClick()
    {

    }
}
