package org.projects.shoppinglist;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.flurry.android.FlurryAgent;


public class MainActivity extends AppCompatActivity {

    //ArrayAdapter arrayAdapter;
    FirebaseListAdapter<Product> firebaseAdapter;
    ListView listView;
    //ArrayList<Product> bag = new ArrayList<Product>();
    public int quantity;
    Product lastDeletedProduct;
    int lastDeletedPosition;

    Firebase mRef;
    public static final String FIREBASE_URL = "https://tobias-shoplist.firebaseio.com/";
    public static final String FLURRY_API_KEY = "J83S8ZVYSHRZGH8X2C3V";

    //ArrayAdapter
    public FirebaseListAdapter<Product> getMyAdapter() {
        return firebaseAdapter;
    }

    public Product getItem(int index){
        return getMyAdapter().getItem(index);
    }

    public void saveCopy(){
        lastDeletedPosition = listView.getCheckedItemPosition();
        //lastDeletedProduct = bag.get(lastDeletedPosition);
        if(lastDeletedPosition != ListView.INVALID_POSITION ){
            //get it
            lastDeletedProduct = getItem(lastDeletedPosition);
        } else {
            //if there was no item selection, display a toast to the user
            Toast.makeText(
                    this,
                    "Please select an item to delete!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // setActionBar(toolbar);
        //Firebase.setAndroidContext(this);
        //Firebase.getDefaultConfig().setPersistenceEnabled(true);

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, FLURRY_API_KEY);

        /*if (savedInstanceState != null) {
            bag = savedInstanceState.getParcelableArrayList("savedList");
        }*/

        int position = -1;

        //get previous state of the app before it gets destroyed
        if(savedInstanceState != null){
            position = savedInstanceState.getInt("position");
        }

        //getting our listview - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);


        //connection to Firebase child node
        mRef = new Firebase("https://tobias-shoplist.firebaseio.com/list");

        firebaseAdapter = new FirebaseListAdapter<Product>(this, Product
                .class, android.R.layout.simple_list_item_checked, mRef){
            @Override
            protected void populateView(View v, Product product, int i){
                TextView text = (TextView) v.findViewById(android.R.id.text1);
                text.setText(product.toString());
            }
        };


        listView.setAdapter(firebaseAdapter);

        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if(position != -1){
            listView.setSelection(position);
        }

        //Adding the EditText field
        final EditText inputText = (EditText) findViewById(R.id.inputField);
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerQuantity);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //So this code is called when ever the spinner is clicked
                quantity = Integer.valueOf((String)parent.getItemAtPosition(position));

                Toast.makeText(MainActivity.this,
                        "Item selected: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputText.getText().toString();
                int quantity = Integer.parseInt(spinner.getSelectedItem().toString());
                Log.v("QUANTITY", "QUANTITY : " + quantity);

                Product p = new Product(name,quantity); //name and q are from the input fields from the user of course.
                Log.v("PRODUCT", "PRODUCT : " + p.toString());
                mRef.push().setValue(p);
                getMyAdapter().notifyDataSetChanged();
                FlurryAgent.logEvent("Item added to list");
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MADE SO IT CAN EASILY BE CHANGED TO CHOICE_MODE_MULTIPLE
                SparseBooleanArray checkedItemPosition = listView.getCheckedItemPositions();
                    //if check to prevent it from crashing when delete button is pressed and nothing is chosen.
                    if (checkedItemPosition.size() != 0) {
                        int itemCount = listView.getCount();
                        saveCopy();
                        for (int i = itemCount - 1; i >= 0; i--) {
                            if (checkedItemPosition.get(i)) {
                                //bag.remove(bag.get(i));
                                //REMOVE BUTTON TO DO FOR MREF FIREBASE!!
                                getMyAdapter().getRef(i).setValue(null);
                                FlurryAgent.logEvent("Item removed from list");
                            }
                        }
                        getMyAdapter().notifyDataSetChanged();
                        final View parent = listView;
                        Snackbar snackbar = Snackbar.make(parent, "Item Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mRef.push().setValue(lastDeletedProduct);
                                getMyAdapter().notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(listView, "Item restored!", Snackbar
                                        .LENGTH_SHORT);
                                snackbar.show();
                            }
                        });
                        snackbar.show();

                    } else {
                        return;
                    }
            }

        });

        //add some stuff to the list
        if (savedInstanceState == null) {
           /* Product pro1 = new Product("Banana", 2);
            Product pro2 = new Product("Apples", 4);
            mRef.push().setValue(pro1);
            mRef.push().setValue(pro2);
            */
        }
    }



    //This method is called before our activity is created
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("savedList", bag);
        outState.putInt("position", listView.getCheckedItemPosition());
    }

    //this is called when our activity is recreated, but
    //AFTER our onCreate method has been called
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getMyAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_settings:
                enterSettings();
                return true;

            case R.id.menu_clearButton:
                MyDialogFragment dialog = new MyDialogFragment() {
                    @Override
                    protected void positiveClick() {
                        //bag.clear();
                        FlurryAgent.logEvent("Cleared List");
                        mRef.setValue(null);
                        getMyAdapter().notifyDataSetChanged();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "The shopping list has been cleared!", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    @Override
                    protected void negativeClick() {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Nothing changed to the list", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                };


                //Here we show the dialog
                //The tag "MyFragement" is not important for us.
                dialog.show(getFragmentManager(), "MyFragment");
                return true;

            case R.id.menu_shareButton:
                shareButton();

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //This will be called when other activities in our application
    //are finished.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){
            getSettingsOnStart();
            Snackbar snackbar = Snackbar.make(listView, "Settings saved", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void enterSettings(){
        //Here we create a new activity and we instruct the
        //Android system to start it
        Intent intent = new Intent(this, SettingsActivity.class);
        //we can use this, if we need to know when the user exists our preference screens
        startActivityForResult(intent, 1);
    }

    public void getSettingsOnStart(){
        //We read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("settings_pref", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");
        String sms = prefs.getString("sms", "");
        String gender = prefs.getString("gender", "");

        Toast.makeText(
                this,
                "Name: "
                + name + "\nEmail: " + email + "\nSMS: " + sms + "\nGender: " + gender , Toast.LENGTH_SHORT).show();

    }

    public void shareButton(){
        SharedPreferences prefs = getSharedPreferences("settings_pref", MODE_PRIVATE);
        String SMS = prefs.getString("sms", "");
        Log.v("SMS_IS", "SMS_IS" + SMS);
        String textToShare = convertListToString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", SMS);
        intent.putExtra("sms_body", textToShare);
        intent.setData(Uri.parse("smsto:" + SMS));
        startActivity(intent);
    }

    public String convertListToString()
    {
        String result = "";
        for (int i = 0; i < firebaseAdapter.getCount(); i++){
            Product p = firebaseAdapter.getItem(i);
            result += p.getQuantity() + " " + p.getName() + "\n";
        }
        return result;
    }


}
