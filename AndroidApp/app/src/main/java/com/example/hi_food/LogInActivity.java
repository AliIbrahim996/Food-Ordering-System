package com.example.hi_food;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.hi_food.Admin.AdminLogInFragment;
import com.example.hi_food.Customer.CustomerLogInFragment;
import com.example.hi_food.ResataurantManager.ManagerLogInFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LogInActivity extends AppCompatActivity {

    Button reset, singup;
    RadioButton customer, admin, manager;
    String ip;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        try {
            ReadTxtFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bundle b = new Bundle();
        b.putString("ip", ip);
        customer = findViewById(R.id.logInCustomer);
        admin = findViewById(R.id.logInAdmin);
        manager = findViewById(R.id.logManager);
        singup = findViewById(R.id.create_account_btn);
        singup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SingUpActivity.class);
            startActivity(intent);
        });
        CustomerLogInFragment customerLogInFragment = new CustomerLogInFragment();
        customerLogInFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_singin_fragment, customerLogInFragment)
                .commit();
        customer.setChecked(true);

        customer.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_singin_fragment, customerLogInFragment)
                                .commit();
                    }
                });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminLogInFragment adminLogInFragment = new AdminLogInFragment();
                adminLogInFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_singin_fragment, adminLogInFragment)
                        .commit();
            }
        });

        manager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                   ManagerLogInFragment managerLogInFragment = new ManagerLogInFragment();
                                                   managerLogInFragment.setArguments(b);
                                                   getSupportFragmentManager().beginTransaction()
                                                           .replace(R.id.main_singin_fragment, managerLogInFragment)
                                                           .commit();
                                               }
                                           }
        );
    }
}
