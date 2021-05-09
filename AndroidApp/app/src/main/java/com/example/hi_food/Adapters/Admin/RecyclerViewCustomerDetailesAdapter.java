package com.example.hi_food.Adapters.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Model.Customer;
import com.example.hi_food.R;

public class RecyclerViewCustomerDetailesAdapter extends RecyclerView.Adapter<RecyclerViewCustomerDetailesAdapter.AddRestaurantViewHolder> {

    private Context mContext;
    private Customer c;

    public RecyclerViewCustomerDetailesAdapter(Context mContext, Customer c) {
        this.c = c;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AddRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new AddRestaurantViewHolder(inflater.inflate(
                R.layout.admin_customer_detaile_item, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AddRestaurantViewHolder holder, int position) {
        holder.vCName_Value.setText(c.getC_name());
        holder.vCGender_Value.setText(c.getGender());
        holder.vCLoc_Value.setText(c.getAddress());
        holder.vCPhone_Value.setText(c.getC_phoneNum());
        holder.Email_Value.setText(c.getEmail());
        new DownloadImage(holder.vCImage).execute(c.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class AddRestaurantViewHolder extends RecyclerView.ViewHolder {
        EditText vCName_Value, vCLoc_Value, vCPhone_Value, vCGender_Value, Email_Value;
        ImageView vCImage;

        public AddRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            vCName_Value = itemView.findViewById(R.id.vCName_Value);
            vCGender_Value = itemView.findViewById(R.id.vCGender_Value);
            vCLoc_Value = itemView.findViewById(R.id.vCLoc_Value);
            vCPhone_Value = itemView.findViewById(R.id.vCPhone_Value);
            Email_Value = itemView.findViewById(R.id.Email_Value);
            vCImage = itemView.findViewById(R.id.vCImage);

        }
    }
}
