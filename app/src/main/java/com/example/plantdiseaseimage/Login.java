package com.example.plantdiseaseimage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity
{
    ViewFlipper v_flipper;

    EditText log_name,log_password;

    Button login_btn,log_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


       // Firebase.setAndroidContext(this);
      //  final Firebase cloud=new Firebase(Config.url+"/Waiter");

        int images[]={R.drawable.plant,R.drawable.plant,R.drawable.plant,R.drawable.plant,R.drawable.plant};
        v_flipper= (ViewFlipper) findViewById(R.id.v_flipper);

        for(int image:images)
        {
            flipperImages(image);
        }

        log_name=(EditText)findViewById(R.id.log_name);
        log_password=(EditText)findViewById(R.id.log_password);
        login_btn=(Button) findViewById(R.id.login_btn);
       /* log_register=(Button) findViewById(R.id.log_register);

        log_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg=new Intent(Login.this,Register.class);
                startActivity(reg);

            }
        });*/
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if(log_name.getText().toString().equals("admin") && log_password.getText().toString().equals("pass"))
                {
                    Intent mg=new Intent(Login.this,ShowList.class);

                    mg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(mg);
                }
                if(log_name.getText().toString().equals("farmer") && log_password.getText().toString().equals("farmer"))
                {
                    Intent mg=new Intent(Login.this,ShowListFarmer.class);

                    mg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(mg);
                }


              }
        });

    }

    public void flipperImages(int image)
    {
        ImageView imageView=new ImageView(this);
        imageView.setBackgroundResource(image);
        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(2000);
        v_flipper.setAutoStart(true);
        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }



}
