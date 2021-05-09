package com.example.hi_food.Adapters.Customer;

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
    String ip, order_id, order_status, table_id;
    int pos;
    SharedPreferences sharedPreferences;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public RecyclerViewManageMealReservationAdapter(Context mContext, List<CustomerMealBooking> customerMealBookings) {
        this.mContext = mContext;
        this.customerMealBookings = customerMealBookings;
    }

    @NonNull
    @Override
    public TableManagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.customer_meal_reservation_item
                , parent, false);
        return new TableManagerHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableManagerHolder holder, int position) {
        final CustomerMealBooking customerMealBooking = customerMealBookings.get(position);
        holder.meal_name.setText(customerMealBooking.getMeal_name());
        holder.orderNumValue.setText(customerMealBooking.getId());
        holder.restName.setText(customerMealBooking.getRestaurant_name());
    }

    @Override
    public int getItemCount() {
        return customerMealBookings.size();
    }

    public class TableManagerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button ordDetails;
        TextView orderNumValue, meal_name, restName;

        public TableManagerHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ordDetails = itemView.findViewById(R.id.ordDetails);
            orderNumValue = itemView.findViewById(R.id.orderNumValue);
            meal_name = itemView.findViewById(R.id.meal_name);
            restName = itemView.findViewById(R.id.restName);
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

        @Override
        public void onClick(View v) {
            //Todo updateStatus
            pos = getAdapterPosition();
            order_id = customerMealBookings.get(getAdapterPosition()).getId();
            table_id = customerMealBookings.get(getAdapterPosition()).getTable_id();
            System.out.println("Order id " + order_id);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setTitle("Delete Reservation");
            builder1.setMessage("Are you sure!");
            builder1.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Todo delete
                    new deleteReservationTask().execute();
                }
            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            if (!customerMealBookings.get(getAdapterPosition()).getOrder_status().equals("accepted")
                    && !customerMealBookings.get(getAdapterPosition()).getOrder_status().equals("rejected")) {
                System.out.println("My status " + customerMealBookings.get(getAdapterPosition()).getOrder_status().equals("accepted"));
                builder1.show();
            }

        }
    }

    class deleteReservationTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/ReservationController/cancelMealReservation.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("table_id", table_id);
                uData.put("order_id", order_id);
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
                    if (urlConnection.getResponseCode() != 200) {
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

                if (responseCode != 200) {

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
                if (flag == 1) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    customerMealBookings.remove(pos);
                    notifyItemRemoved(pos);
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
