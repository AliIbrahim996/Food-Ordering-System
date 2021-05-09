package com.example.hi_food.Adapters.RestaurantManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.example.hi_food.Model.Meal;
import com.example.hi_food.R;
import com.example.hi_food.ResataurantManager.UpdateMeal;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RecyclerViewManageMealAdapter extends RecyclerView.Adapter<RecyclerViewManageMealAdapter.ManageMealsViewHolder> {

    private Context mContext;
    String email, ip, full_name, meal_id;

    SharedPreferences sharedPreferences;
    int pos;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    private List<Meal> meals;

    public RecyclerViewManageMealAdapter(Context mContext, List<Meal> meals) {
        this.mContext = mContext;
        this.meals = meals;
    }

    @NonNull
    @Override
    public ManageMealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.manage_meals_item, parent, false);

        return new ManageMealsViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageMealsViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.cat_name.setText(meal.getCat_name());
        holder.price.setText(meal.getPrice());
        holder.kalories.setText(meal.getCalories());
        holder.m_name.setText(meal.getName());
        new DownloadImage(holder.m_img).execute(meal.getImageURL());
    }

    class DeleteTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;
        private ProgressDialog dialog = new ProgressDialog(mContext);

        protected void onPreExecute() {
            this.dialog.setMessage("Processing...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/deleteMeal.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("meal_id", meal_id);
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
                    if (responseCode == 200) {
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
                        System.out.println("Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                        dialog.dismiss();
                        return responseMessage;
                    }

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                dialog.dismiss();
                return null;
            }
        }

        protected void onPostExecute(String response) {
            String message = "";
            int flag = -2;
            try {

                System.out.println("Response :" + response);
                if (response == null) {
                    Toast.makeText(mContext, "Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "Response Message " + responseMessage, Toast.LENGTH_LONG).show();
                    return;
                }
                if (responseCode == 200) {
                    JSONObject jsonObject = new JSONObject(response);
                    flag = jsonObject.getInt("flag");
                    message = jsonObject.getString("message");

                    if (flag == 1) {
                        dialog.dismiss();
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        meals.remove(pos);
                        notifyItemRemoved(pos);
                        notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(mContext, message + "" + response, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            dialog.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public class ManageMealsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView cat_name, kalories, price, m_name;
        ImageView meal_delete, m_img;

        public ManageMealsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cat_name = itemView.findViewById(R.id.mCatValue);
            kalories = itemView.findViewById(R.id.mkaValue);
            price = itemView.findViewById(R.id.mPriceValue);
            meal_delete = itemView.findViewById(R.id.meal_delete);
            m_img = itemView.findViewById(R.id.m_img);
            m_name = itemView.findViewById(R.id.m_name);
            meal_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    meal_id = meals.get(getAdapterPosition()).getId();
                    pos = getAdapterPosition();
                    new DeleteTask().execute();
                }
            });
        }

        @Override
        public void onClick(View v) {
            //Todo open update
            Intent intent = new Intent(mContext, UpdateMeal.class);
            intent.putExtra("meal_name", meals.get(getAdapterPosition()).getName());
            intent.putExtra("meal_calories", meals.get(getAdapterPosition()).getCalories());
            intent.putExtra("meal_price", meals.get(getAdapterPosition()).getPrice());
            intent.putExtra("cat_name", meals.get(getAdapterPosition()).getCat_name());
            intent.putExtra("meal_id", meals.get(getAdapterPosition()).getId());
            intent.putExtra("ip", ip);
            intent.putExtra("email", email);
            intent.putExtra("full_name", full_name);
            intent.putExtra("imageUrl", meals.get(getAdapterPosition()).getImageURL());
            mContext.startActivity(intent);
        }
    }
}
