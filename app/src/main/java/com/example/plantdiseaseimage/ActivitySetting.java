package com.example.plantdiseaseimage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

public class ActivitySetting extends AppCompatActivity {
EditText etth;
Button bt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        etth=findViewById(R.id.etth);
        bt1=findViewById(R.id.bt1);


        SharedPreferences prefs = getSharedPreferences("plant", MODE_PRIVATE);
        try {


            String str = prefs.getString("th", "");
            etth.setText(str);
        }
        catch (Exception ex)
        {

        }
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etth.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(ActivitySetting.this, "Enter threshold value", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences.Editor prefsEditor = ActivitySetting.this.getSharedPreferences("plant", MODE_PRIVATE).edit();


                prefsEditor.putString("th",etth.getText().toString());
                prefsEditor.commit();

            }
        });

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

    }
        return(super.onOptionsItemSelected(item));
    }
}