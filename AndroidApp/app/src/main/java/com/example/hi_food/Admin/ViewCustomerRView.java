package com.example.hi_food.Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Adapters.Admin.RecyclerViewCustomerDetailesAdapter;
import com.example.hi_food.Model.Customer;
import com.example.hi_food.Model.Order;
import com.example.hi_food.R;

import java.util.List;

public class ViewCustomerRView extends AppCompatActivity {
    RecyclerView recyclerView;
    Customer c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_customer_rview);
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            c = (Customer) bundle.getSerializable("customer");
        }
        recyclerView = findViewById(R.id.viewCustomer_r_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new
                RecyclerViewCustomerDetailesAdapter(this, c));
    }
}
