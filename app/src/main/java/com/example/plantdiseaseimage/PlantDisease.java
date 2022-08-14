package com.example.plantdiseaseimage;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plantdiseaseimage.myutils.GLCM;
import com.example.plantdiseaseimage.pojo.PlantFeature;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.lmu.ifi.dbs.utilities.Arrays2;

public class PlantDisease extends AppCompatActivity {


    // Creating button.
    Button ChooseButton,UploadButton, btuplaod, btclassify;
    TextView txtresult,txtremedy;

    // Creating EditText.
    EditText ImageName ;

    // Creating ImageView.
    ImageView SelectImage;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog ;
    Bitmap btsrc;
    //EditText etdisease;
    ArrayList<PlantFeature>plantFeaturList=new ArrayList<PlantFeature>();
    String featureString="";
    double th=Double.MAX_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        progressDialog = new ProgressDialog(PlantDisease.this);
        Firebase.setAndroidContext(this);
//        FirebaseApp.initializeApp(this);
        // Assign FirebaseStorage instance to storageReference.


        SharedPreferences prefs = getSharedPreferences("plant", MODE_PRIVATE);
       try {


           String str = prefs.getString("th", "");
           th=Double.parseDouble(str);
       }
       catch (Exception ex)
       {

       }
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("PlantFeature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plantFeaturList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    PlantFeature plantFeature = postSnapshot.getValue(PlantFeature.class);
                    Log.i("feat_:",plantFeature.getDisease());

                    plantFeaturList.add(plantFeature);
                }
                Toast.makeText(PlantDisease.this, plantFeaturList.size()+" Feature loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Assign ID'S to button.
        ChooseButton = (Button)findViewById(R.id.ButtonChooseImage);
        UploadButton = (Button)findViewById(R.id.ButtonUploadImage);
       // btuplaod = (Button)findViewById(R.id.btupload);
        btclassify = (Button)findViewById(R.id.btclassify);
        txtresult = (TextView) findViewById(R.id.txtresult);
        txtremedy=findViewById(R.id.txtremedy);
        // DisplayImageButton = (Button)findViewById(R.id.DisplayImagesButton);

        // Assign ID's to EditText.
       // etdisease = (EditText)findViewById(R.id.etdisease);

        // Assign ID'S to image view.
        SelectImage = (ImageView)findViewById(R.id.ShowImageView);

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(PlantDisease.this);

        // Adding click listener to Choose image button.
        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });


        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                progressDialog.setTitle("Extracting");
                // Showing progressDialog.
                progressDialog.show();
                haralickFeatures(btsrc);
                progressDialog.dismiss();
                // Calling method to upload selected image on Firebase storage.
              //  Toast.makeText(PlantDisease.this, "Feature extracted", Toast.LENGTH_SHORT).show();

            }
        });


        btclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] featureStr = featureString.split(Pattern.quote(","));

              float[] featureFlot = new float[featureStr.length];
                for (int i = 0; i < featureStr.length; i++) {
                    Log.i("#feat:",featureStr[i].toLowerCase());
                    if(featureStr[i].toLowerCase().contains("n"))
                    {
                        featureStr[i]="0";
                    }
                    try {


                        featureFlot[i] = Float.parseFloat(featureStr[i]);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(PlantDisease.this, "Image not supported", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //  Log.i("feat:",featureFlot[i]);
                 }
                double dist=Double.MAX_VALUE;
                String result="";
                String remedy="";
                for(int i=0;i<plantFeaturList.size();i++)
                {
                 PlantFeature pf=plantFeaturList.get(i);
                 String disease=pf.getDisease();
                 String storefeat=pf.getFeature();
                    Log.i("feat:",disease+""+storefeat);

                    String[] storefeatureStr = storefeat.split(Pattern.quote(","));

                   // float[] storefeatureFlot = new float[storefeatureStr.length];
                    double diff=0;
                    double sum=0;
                    for (int k = 0; k < storefeatureStr.length; k++) {
                        if(storefeatureStr[k].toLowerCase().contains("n"))
                        {
                            storefeatureStr[k]="0";
                        }
                        float storevalue = Float.parseFloat(storefeatureStr[k]);
                        try {
                            diff = Math.abs(storevalue - featureFlot[k]);

                            Log.i("Dist:","dist:"+diff+"");

                        }

                        catch (Exception ex)
                        {

                        }
                        sum=sum+diff;


                    }
                    Log.i("feat:","Sum:"+sum+"");

                    if(sum<dist)
                    {
                        dist=sum;
                        result=disease;
                        remedy=pf.getRemedies();
                    }

                }
                featureString="";
             //   String dist1= new DecimalFormat("###.##").format(dist);
                String dist1=  String.format("%.2f", dist);
                if(dist<th) {
                    txtresult.setText("Predicted Disease " + result + " with score " + dist1);
                    txtremedy.setText(remedy);
                }
                else
                {
                    txtresult.setText("Predicted Disease Unknown");
                    txtremedy.setText("");
                }
                Toast.makeText(PlantDisease.this, "Features Classify", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void haralickFeatures(Bitmap b)
    {

        GLCM glcm=new GLCM();
        try {
            glcm.haralickDist = 1;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 90, stream); // what 90 does ??
            GLCM.imageArray = new byte[]{};

            GLCM.imageArray = stream.toByteArray();
            glcm.process(b);
            glcm.data = new ArrayList<>(1);
            glcm.addData(glcm.features);
            List<double[]> featuresHar = glcm.getFeatures();

             featureString="";
            for (double[] feature : featuresHar) {

                featureString = Arrays2.join(feature, ",", "%.2f");
            }
            String[] featureStr = featureString.split(Pattern.quote(","));
            Toast.makeText(PlantDisease.this, "Features Extracted", Toast.LENGTH_SHORT).show();

          /*  float[] featureFlot = new float[featureStr.length];
            for (int i = 0; i < featureStr.length; i++) {
                featureFlot[i] = Float.parseFloat(featureStr[i]);
              //  Log.i("feat:",featureFlot[i]);
            }*/
            Log.i("features:",featureString);



        }
        catch (Exception ex)
        {

        }
        //featureFlot is array that contain all 14 haralick features

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ChooseButton.setText("Browse");
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                btsrc=bitmap;
                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                ChooseButton.setText("Image Selected");

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
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
