package com.example.hi_food.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hi_food.Adapters.Customer.RecyclerViewOrderItemAdapter;
import com.example.hi_food.Model.Order;
import com.example.hi_food.R;

import java.util.ArrayList;
import java.util.List;

public class OrderBell extends AppCompatActivity {
    RecyclerView tableContentRView;
    List<Order> orders;
    RecyclerViewOrderItemAdapter orderItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pell);
        tableContentRView = findViewById(R.id.tableContentRView);

        tableContentRView.setHasFixedSize(true);
        tableContentRView.setLayoutManager(new LinearLayoutManager(this));
        getList();
        orderItemAdapter =  new RecyclerViewOrderItemAdapter(this,orders);
        orderItemAdapter.notifyDataSetChanged();

        tableContentRView.setAdapter(orderItemAdapter);

        double total = caculateTotal();
        TextView totalPrice = findViewById(R.id.total);

        totalPrice.setText(totalPrice.getText()+" "+total+" $");

        getSupportActionBar().setTitle("Your Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private double caculateTotal() {
        double total =0;
        for(Order o : orders){
            total+= o.getQty()*o.getPrice();
        }
        return total;
    }

    private void getList() {
        orders = new ArrayList<>();
        orders.add(new Order("pizza",2,5));
        orders.add(new Order("hot-dog",2,5));
        orders.add(new Order("soup",2,5));

    }
}
