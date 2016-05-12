package org.projects.shoppinglist;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Tobias on 05/12/16.
 */
public class FirebaseSetup extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
