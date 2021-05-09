package com.example.hi_food;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hi_food.Admin.SingUpAdminFragment;
import com.example.hi_food.Customer.SingUpCustomerFragment;
import com.example.hi_food.ResataurantManager.SingUpManagerFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SingUpActivity extends AppCompatActivity {

    RadioButton customer, admin, manager;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up_activity);
        try {
            ReadTxtFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SingUpCustomerFragment customerFragment = new SingUpCustomerFragment();
        Bundle b = new Bundle();
        b.putString("ip", ip);
        customerFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_singup_fragment, customerFragment)
                .commit();
        getSupportActionBar().setTitle("Sing-Up");
        customer = findViewById(R.id.rCustomer);
        customer.setChecked(true);
        admin = findViewById(R.id.r_singAdmin);
        manager = findViewById(R.id.rManager);
        customer.setChecked(true);

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_singup_fragment, customerFragment)
                        .commit();
                getSupportActionBar().setTitle("Customer SingUp");
            }
        });


        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingUpAdminFragment singUpAdminFragment = new SingUpAdminFragment();
                singUpAdminFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_singup_fragment, singUpAdminFragment)
                        .commit();
                getSupportActionBar().setTitle("Admin SingUp");
            }
        });

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingUpManagerFragment singUpManagerFragment = new SingUpManagerFragment();
                singUpManagerFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_singup_fragment, singUpManagerFragment)
                        .commit();
                getSupportActionBar().setTitle("Manager SingUp");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void ReadTxtFile() throws IOException {
        String str = "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = getApplicationContext().getAssets().open("ip.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((str = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(str);
            this.ip = str;
            System.out.println("Ip : " + str);
        }
        is.close();
    }

}
