package com.example.hi_food.Adapters.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Admin.AddRestaurantRView;
import com.example.hi_food.Admin.UpdateRestaurantRView;
import com.example.hi_food.Model.Restaurant;
import com.example.hi_food.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class RecyclerViewBrowseRestaurant extends RecyclerView.Adapter<RecyclerViewBrowseRestaurant.BrowseRestaurantViewHolder> {

    public Context mContext;
    private List<Restaurant> restaurants;
    String rest_id, status;
    String ip;
    SharedPreferences sharedPreferences;
    String full_name, email;
    int pos;

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public RecyclerViewBrowseRestaurant(Context mContext, List<Restaurant> restaurants) {
        this.mContext = mContext;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public BrowseRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        return new BrowseRestaurantViewHolder(
                layoutInflater.inflate(R.layout.admin_manage_restaurent_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseRestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.restName.setText(restaurant.getR_name());
        holder.restManager.setText(restaurant.getManager_name());
        new DownloadImage(holder.mrImage).execute(restaurant.getImageURL());
        rest_id = restaurants.get(position).getId();
        status = restaurants.get(position).getStatus();
        if (status.equals("1")) {
            holder.block.setImageResource(R.drawable.available_96px);
            status = "0";
        } else {
            holder.block.setImageResource(R.drawable.unavailable_96px);
            status = "1";
        }


        holder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo block restaurant
                pos = position;
                new BlockTask().execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println("Size == " + restaurants.size());
        return restaurants.size();
    }

    public class BrowseRestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView restName, restManager;
        ImageView mrImage, block;

        public BrowseRestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            restName = itemView.findViewById(R.id.mrest_name);
            restManager = itemView.findViewById(R.id.mManagerName);
            mrImage = itemView.findViewById(R.id.mr_img);
            block = itemView.findViewById(R.id.block);
        }

        @Override
        public void onClick(View v) {
            //Todo open Update page
            Restaurant r = restaurants.get(getAdapterPosition());
            Bundle b = new Bundle();
            b.putSerializable("restaurant", r);
            Intent intent = new Intent(mContext, UpdateRestaurantRView.class);
            intent.putExtra("ip", ip);
            intent.putExtra("email", email);
            intent.putExtra("full_name", full_name);
            intent.putExtras(b);
            mContext.startActivity(intent);
        }
    }

    class BlockTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", "192.168.1.13") +
                        "/HI-Food/API/Controller/AdminController/blockRest.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("rest_id", rest_id);
                uData.put("status", status);
                System.out.println("Data: " + uData);
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
                    if (urlConnection.getResponseCode() == HTTP_OK) {
                        System.out.println("Response Message: " + responseMessage);
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
                    } else {
                        return responseMessage;
                    }

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            String message = "";
            int flag = -2;
            try {

                System.out.println("Response :" + response);
                if (response == null || responseCode != 200) {
                    System.out.println("Response : " + response + "\n Message : " + responseMessage);
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                flag = jsonObject.getInt("flag");
                message = jsonObject.getString("message");
                System.out.println(message);
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                if (flag == 1) {

                    restaurants.get(pos).setStatus(status);
                    notifyItemChanged(pos);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
    }
}
