package com.example.hi_food.Adapters.Customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Customer.BrowseCategories;
import com.example.hi_food.Model.Restaurant;
import com.example.hi_food.R;

import java.util.List;


public class RecyclerViewRestaurantAdapter
        extends RecyclerView.Adapter<RecyclerViewRestaurantAdapter.RestaurantHolder> {
    private Context mContext;
    private String email;
    private List<Restaurant> restaurants;
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RecyclerViewRestaurantAdapter(Context mContext, List<Restaurant> restaurants) {
        this.mContext = mContext;
        System.out.println("MyContext: == " + mContext);
        this.restaurants = restaurants;
    }


    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mView = mInflater.inflate(R.layout.restaurant_item, parent, false);
        return new RestaurantHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.name.setText(restaurant.getR_name());
        holder.location.setText(restaurant.getR_location());
        holder.openAt.setText(restaurant.getOpenAt());
        holder.closeAt.setText(restaurant.getCloseAt());
        holder.rate.setText(restaurant.getRate());
        new DownloadImage(holder.restImage).execute(restaurant.getImageURL());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class RestaurantHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView name, location, openAt, closeAt, rate;
        ImageView restImage;
        int id;

        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.rest_name);
            location = itemView.findViewById(R.id.rest_loc);
            openAt = itemView.findViewById(R.id.r_open);
            closeAt = itemView.findViewById(R.id.r_close);
            rate = itemView.findViewById(R.id.r_rate);
            restImage = itemView.findViewById(R.id.admin_r_img);
        }

        @Override
        public void onClick(View v) {
            Restaurant restaurant = restaurants.get(getAdapterPosition());
            Intent inflater = new Intent(mContext, BrowseCategories.class);
            inflater.putExtra("rest_id", restaurant.getId());
            inflater.putExtra("rest_email", restaurant.getManger_email());
            inflater.putExtra("c_email", getEmail());
            inflater.putExtra("ip", getIp());
            mContext.startActivity(inflater);
        }
    }
}
