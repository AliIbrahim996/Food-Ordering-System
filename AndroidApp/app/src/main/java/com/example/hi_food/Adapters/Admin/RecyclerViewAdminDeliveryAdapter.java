package com.example.hi_food.Adapters.Admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Model.DeliveryServices;
import com.example.hi_food.R;

import java.util.List;

public class RecyclerViewAdminDeliveryAdapter extends RecyclerView.Adapter<RecyclerViewAdminDeliveryAdapter.ManageDeliveryViewHolder> {

    private Context mContext;
    private List<DeliveryServices> customer_Meal_bookings;

    public RecyclerViewAdminDeliveryAdapter(Context mContext, List<DeliveryServices> customer_Meal_bookings) {
        this.mContext = mContext;
        this.customer_Meal_bookings = customer_Meal_bookings;
    }

    @NonNull
    @Override
    public ManageDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.check_delivery_item, parent, false);

        return new ManageDeliveryViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManageDeliveryViewHolder holder, int position) {
        DeliveryServices customer_Meal_booking = customer_Meal_bookings.get(position);
        int colorRed = Color.RED, colorGreen = Color.GREEN,
                colorBlue = Color.BLUE;
        holder.c_name.setText(customer_Meal_booking.getCustomer_name());
        holder.restaurant_name.setText(customer_Meal_booking.getRestaurant_name());
        holder.order_num.setText(customer_Meal_booking.getReservation_id());
        holder.date_in.setText(customer_Meal_booking.getDate_booking());
        String dataDeliverd = customer_Meal_booking.getDate_delivered() != null ? "not delivered yet" : customer_Meal_booking.getDate_delivered();
        holder.date_out.setText(dataDeliverd);
        String status = customer_Meal_booking.getOrder_status();
        holder.status.setText(status);
        switch (status) {
            case "accepted":
                holder.status.setTextColor(colorGreen);
                break;
            case "rejected":
                holder.status.setTextColor(colorRed);
                break;
            case "waiting for approval":
                holder.status.setTextColor(colorBlue);
                break;

        }

    }

    @Override
    public int getItemCount() {

        return customer_Meal_bookings.size();
    }

    public static class ManageDeliveryViewHolder extends RecyclerView.ViewHolder {

        TextView c_name, order_num, date_in, date_out, status, restaurant_name;

        public ManageDeliveryViewHolder(@NonNull View itemView) {
            super(itemView);

            c_name = itemView.findViewById(R.id.customer_name);
            order_num = itemView.findViewById(R.id.orderNmuValue);
            date_in = itemView.findViewById(R.id.date_inValue);
            date_out = itemView.findViewById(R.id.date_outValue);
            status = itemView.findViewById(R.id.orderStatusValue);
            restaurant_name = itemView.findViewById(R.id.restaurant_name);

        }
    }
}
