package com.example.hi_food.Adapters.Admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Admin.ViewCustomerRView;
import com.example.hi_food.Model.Customer;
import com.example.hi_food.R;

import java.util.List;

public class RecyclerViewBrowseCustomer extends RecyclerView.Adapter<RecyclerViewBrowseCustomer.CustomerViewHolder> {
    private Context mContext;
    private List<Customer> customers;

    public RecyclerViewBrowseCustomer(Context mContext, List<Customer> customers) {
        this.mContext = mContext;
        this.customers = customers;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new CustomerViewHolder(
                inflater.inflate(R.layout.admin_manage_customer_item,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.cName.setText(customer.getC_name());
        holder.cEmail.setText(customer.getEmail());
        new DownloadImage(holder.mr_img).execute(customer.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cName, cEmail;
        ImageView mr_img;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cName = itemView.findViewById(R.id.aCName);
            cEmail = itemView.findViewById(R.id.aCEmail);
            mr_img = itemView.findViewById(R.id.mr_img);
        }

        @Override
        public void onClick(View v) {
            //Todo open detailes
            Bundle bundle = new Bundle();
            bundle.putSerializable("customer", customers.get(getAdapterPosition()));
            Intent intent = new Intent(mContext, ViewCustomerRView.class);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
    }
}
