package com.example.hi_food.Adapters.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Model.CustomerMealBooking;
import com.example.hi_food.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class RecyclerViewManageMealReservationAdapter extends
        RecyclerView.Adapter<RecyclerViewManageMealReservationAdapter.TableManagerHolder> {
    private Context mContext;
    private List<CustomerMealBooking> customerMealBookings;
    int pos;

    public RecyclerViewManageMealReservationAdapter(Context mContext, List<CustomerMealBooking> customerMealBookings) {
        this.mContext = mContext;
        this.customerMealBookings = customerMealBookings;
    }

    @NonNull
    @Override
    public TableManagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.manage_meal_reservation_item
                , parent, false);
        return new TableManagerHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableManagerHolder holder, int position) {
        final CustomerMealBooking customerMealBooking = customerMealBookings.get(position);
        holder.meal_name.setText(customerMealBooking.getMeal_name());
        holder.orderNumValue.setText(customerMealBooking.getId());
    }

    @Override
    public int getItemCount() {
        return customerMealBookings.size();
    }

    public class TableManagerHolder extends RecyclerView.ViewHolder {
        Button ordDetails;
        TextView orderNumValue, meal_name;

        public TableManagerHolder(@NonNull View itemView) {
            super(itemView);
            ordDetails = itemView.findViewById(R.id.ordDetails);
            orderNumValue = itemView.findViewById(R.id.orderNumValue);
            meal_name = itemView.findViewById(R.id.meal_name);
            ordDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    CustomerMealBooking c = customerMealBookings.get(getAdapterPosition());
                    JSONObject d = c.getTable_info();
                    String detailes = "";
                    String is_in_door = c.getIs_in_door().equals("1") ? "delivery order" : "reserved on table";
                    detailes += "Meal quantity: " + c.getQuantity() + "\n" +
                            "Unit Price: " + c.getUnit_price() + "\n" +
                            "Date booking" + c.getData_time_booking() + "\n" +
                            "Order type: " + is_in_door + "\n" +
                            "Order Status: " + c.getOrder_status() + "\n";
                    Iterator iterator = d.keys();
                        while (iterator.hasNext()) {
                            String o = (String) iterator.next();
                            if (o.equals("empty"))
                                continue;
                            try {
                                detailes += o + ":  " + d.getString(o) + "\n";
                            } catch (JSONException e) {
                                Log.e("json error", e.getMessage());
                            }
                        }
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Reservation Details")
                            .setMessage(detailes)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }
    }
}
