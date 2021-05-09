package com.example.hi_food.Customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hi_food.API.TableReservation;
import com.example.hi_food.Interfaces.DialogCallback;
import com.example.hi_food.Model.Order;
import com.example.hi_food.R;
import com.example.hi_food.utils.GlobalUtils;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddInfo extends AppCompatActivity {
    List<Order> orders;
    String c_email, ip, rest_email, rest_id, table_id,full_name;
    SharedPreferences sharedPreferences;
    EditText numofC, occasion, numofBallons, numofflowers, numofPersons, od;
    String nOfc, occ, nOfB, nOfF, nOfP, otherD;
    Button t_book;
    MaterialCheckBox mc1, mc2;
    Toolbar myBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_info);
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orders = (List<Order>) bundle.getSerializable("orders");
            System.out.println("Orders size" + orders.size());
        }
        myBar = findViewById(R.id.myAppBar);
        setSupportActionBar(myBar);
        getSupportActionBar().setTitle("");
        myBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPress();
            }
        });
        rest_id = intent.getStringExtra("rest_id");
        rest_email = intent.getStringExtra("rest_email");
        c_email = intent.getStringExtra("c_email");
        full_name = intent.getStringExtra("full_name");
        ip = intent.getStringExtra("ip");
        table_id = intent.getStringExtra("table_id");
        rest_id = intent.getStringExtra("rest_id");
        numofC = findViewById(R.id.numOfC);
        occasion = findViewById(R.id.occasion);
        numofBallons = findViewById(R.id.numOfBallons);
        numofflowers = findViewById(R.id.numOfFlowers);
        numofPersons = findViewById(R.id.numofP);
        od = findViewById(R.id.od);
        t_book = findViewById(R.id.t_book);
        mc1 = findViewById(R.id.mC1);
        mc2 = findViewById(R.id.mC2);
        mc1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    numofBallons.setVisibility(View.VISIBLE);
                } else {
                    numofBallons.setVisibility(View.INVISIBLE);

                }

            }
        });
        mc2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    numofflowers.setVisibility(View.VISIBLE);
                } else {
                    numofflowers.setVisibility(View.INVISIBLE);

                }

            }
        });
        t_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo get values and perform reservation
                nOfc = numofC.getText().toString();
                occ = occasion.getText().toString();
                nOfB = numofBallons.getText().toString();
                nOfF = numofflowers.getText().toString();
                nOfP = numofPersons.getText().toString();
                otherD = od.getText().toString();
                JSONObject data = new JSONObject();
                try {
                    data.put("number_of_seats", nOfc);
                    data.put("occasion", occ);
                    data.put("number_of_balloons", nOfB);
                    data.put("number_of_flowers", nOfF);
                    data.put("number_of_persons", nOfP);
                    data.put("other_detail's", otherD);
                    System.out.println("DaTA " + data);
                    new TableReservation(orders, AddInfo.this, ip,
                            rest_email, c_email, table_id, sharedPreferences, data).execute();
                    GlobalUtils g = new GlobalUtils(rest_id, c_email, ip, AddInfo.this);
                    g.showDialogRating(AddInfo.this, new DialogCallback() {
                        @Override
                        public void callBack(String ratings) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void backPress() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("email", c_email);
        intent.putExtra("full_name", full_name);
        intent.putExtra("ip", ip);
        startActivity(intent);
        this.finish();
    }

}
