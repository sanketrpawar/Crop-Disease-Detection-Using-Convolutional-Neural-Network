package com.example.plantdiseaseimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class ShowList extends AppCompatActivity {

    private TextView mTextMessage;
    String Storage_Path = "category1/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "category1";
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;


    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_show:

                    Intent i2= new Intent(ShowList.this,NewsActivity.class);
                    startActivity(i2);
                    return true;
                case R.id.navigation_category:

                    Intent i= new Intent(ShowList.this,creatCat.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_test:

                    Intent i3= new Intent(ShowList.this,PlantDisease.class);
                    startActivity(i3);
                    return true;
                case R.id.nav_crop:

                    Intent i4= new Intent(ShowList.this,CropRecommendationActivity.class);
                    startActivity(i4);
                    return true;
                case R.id.nav_setting:

                    Intent i5= new Intent(ShowList.this,ActivitySetting.class);
                    startActivity(i5);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowList.this));



        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.logout:
            //add the function to perform here
            Intent mg=new Intent(this,Login.class);

            mg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(mg);
            return(true);
        case R.id.menu_tf:
            //add the function to perform here
            Intent mg1=new Intent(this,Login.class);
            PackageManager pm = this.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("org.tensorflow.lite.examples.classification");
            startActivity(launchIntent);
           // mg1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            //startActivity(mg1);
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}
