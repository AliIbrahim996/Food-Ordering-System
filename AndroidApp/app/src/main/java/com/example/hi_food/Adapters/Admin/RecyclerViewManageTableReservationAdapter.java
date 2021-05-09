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

import com.example.hi_food.Model.CustomerTableBooking;
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

public class RecyclerViewManageTableReservationAdapter extends RecyclerView.Adapter<RecyclerViewManageTableReservationAdapter.TableManagerHolder> {
    private  Context mContext;
    private  List<CustomerTableBooking> tableList;

    public RecyclerViewManageTableReservationAdapter(Context mContext, List<CustomerTableBooking> tableList) {
        this.mContext = mContext;
        this.tableList = tableList;
    }

    @NonNull
    @Override
    public TableManagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.manage_table_reservation_item, parent, false);
        return new TableManagerHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableManagerHolder holder, int position) {
        final CustomerTableBooking table = tableList.get(position);
        holder.tableNumber.setText(table.getTable_number());
        holder.customer_name.setText(table.getCustomer_name());
        holder.OrderNum.setText(table.getId());
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public class TableManagerHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, customer_name, OrderNum;
        Button order_detailes;

        public TableManagerHolder(@NonNull View itemView) {
            super(itemView);
            tableNumber = itemView.findViewById(R.id.tManageNmuberValue);
            OrderNum = itemView.findViewById(R.id.tordNmuValue);
            customer_name = itemView.findViewById(R.id.tManagecustomer_name);
            order_detailes = itemView.findViewById(R.id.ordDetailsValue);
            order_detailes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject d = tableList.get(getAdapterPosition()).getReservation_info();
                    String detailes = "";
                    Iterator iterator = d.keys();
                    if (!iterator.hasNext()) {
                        detailes = "no details";
                    } else
                        while (iterator.hasNext()) {
                            String o = (String) iterator.next();
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
