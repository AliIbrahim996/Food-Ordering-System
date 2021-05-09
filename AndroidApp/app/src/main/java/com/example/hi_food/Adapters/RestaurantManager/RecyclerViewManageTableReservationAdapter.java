package com.example.hi_food.Adapters.RestaurantManager;

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

import org.json.JSONArray;
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
     String table_status, table_id;
    SharedPreferences sharedPreferences;
     String ip;
     int pos;
    View.OnClickListener tableOnClickListener;

    public void setTableOnClickListener(View.OnClickListener tableOnClickListener) {
        this.tableOnClickListener = tableOnClickListener;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

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

    public class TableManagerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tableNumber, customer_name, OrderNum;
        Button order_detailes;

        public TableManagerHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tableNumber = itemView.findViewById(R.id.tManageNmuberValue);
            OrderNum = itemView.findViewById(R.id.tordNmuValue);
            customer_name = itemView.findViewById(R.id.tManagecustomer_name);
            order_detailes = itemView.findViewById(R.id.ordDetailsValue);
            order_detailes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
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
        @Override
        public void onClick(View v) {
            //Todo updateStatus
            pos = getAdapterPosition();
            table_id = tableList.get(getAdapterPosition()).getRestaurant_tables_id();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setTitle("Change status");
            builder1.setMessage("Accept or reject reservation!");
            builder1.setPositiveButton("accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    table_status = "unavailable";
                    //Todo update status
                    new updateReservationTask().execute();
                }
            }).setNegativeButton("available", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    table_status = "available";
                    new updateReservationTask().execute();
                    //Todo update status
                }
            }).show();
        }
    }

    class updateReservationTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/ReservationController/acceptOrRejectTableReservation.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("restaurent_tables_id", table_id);
                uData.put("table_status", table_status);
                System.out.println(uData);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(uData.toString());
                writer.flush();
                writer.close();
                os.close();

                urlConnection.connect();
                try {
                    responseCode = urlConnection.getResponseCode();
                    responseMessage = urlConnection.getResponseMessage();
                    if (urlConnection.getResponseCode() != 201) {
                        System.out.println("No data");
                        System.out.println("Response code: " + urlConnection.getResponseCode());
                        System.out.println("Response Message: " + responseMessage);
                        return responseMessage;
                    }
                    System.out.println(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    System.out.println("Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                    return stringBuilder.toString();

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            try {

                if (responseCode != 201) {

                    Toast.makeText(mContext,
                            "Response Code : " + responseCode, Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, responseMessage, Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                int flag = -3;
                String message = "";
                flag = jsonObject.getInt("flag");
                message = jsonObject.getString("message");
                if (flag == 1|| flag == -1) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    tableList.get(pos).setT_status(table_status);
                    notifyItemChanged(pos);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, message + " Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
