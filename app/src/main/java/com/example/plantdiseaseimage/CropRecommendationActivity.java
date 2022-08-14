package com.example.plantdiseaseimage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.plantdiseaseimage.myutils.Feature;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CropRecommendationActivity extends AppCompatActivity {
ArrayList<Feature> featureList=new ArrayList<Feature>();
EditText et1,et2,et3,et4,et5,et6,et7;
Button bt1;
TextView txtres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_recommendation);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        et3=findViewById(R.id.et3);
        et4=findViewById(R.id.et4);
        et5=findViewById(R.id.et5);
        et6=findViewById(R.id.et6);
        et7=findViewById(R.id.et7);
        bt1=findViewById(R.id.bt1);
        txtres=findViewById(R.id.txtres);


        try {
            InputStream is = this.getAssets().open("crop_recommendation.csv");
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("UTF-8"));

              BufferedReader bf = new BufferedReader(reader);
            String line = "";
            int f=0;
            while ((line = bf.readLine()) != null) {
                Log.i("#data:", line);
                f++;
                if (f > 1) {
                    String data[] = line.split(",");
                    Feature feature = new Feature();
                    double[] feat = new double[7];
                    for (int i = 0; i < data.length - 1; i++) {
                        feat[i] = Double.parseDouble(data[i]);
                    }
                    feature.setName(data[data.length - 1]);
                    feature.setFeat(feat);
                    featureList.add(feature);
                }
            }

           // Log.i("crop:",csv.size()+"");
        }
        catch (Exception ex)
        {
            Log.i("#err:",ex+"");
        }

    bt1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            double[] qfeat = new double[7];

            qfeat[0]= Double.parseDouble(et1.getText().toString());
            qfeat[1]= Double.parseDouble(et2.getText().toString());
            qfeat[2]= Double.parseDouble(et3.getText().toString());
            qfeat[3]= Double.parseDouble(et4.getText().toString());
            qfeat[4]= Double.parseDouble(et5.getText().toString());
            qfeat[5]= Double.parseDouble(et6.getText().toString());
            qfeat[6]= Double.parseDouble(et7.getText().toString());

            double dist= Double.MAX_VALUE;
            String output="";
            for(int i=0;i<featureList.size();i++)
            {
                Feature feature= featureList.get(i);
                 double []myfeat= feature.getFeat();
                double diff=0;
                for(int j=0;j<qfeat.length;j++)
                {
                    diff=diff+ Math.abs(qfeat[j]-myfeat[j]);
                }
                Log.i("#diff",diff+"");

                Log.i("#Dist",dist+"");

                if(diff<dist)
                {
                    output=feature.getName();
                    dist=diff;
                }
            }

            txtres.setText(output+" are recommended by the A.I for your farm.");
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