package com.example.hi_food.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi_food.Admin.AdminLogInFragment;
import com.example.hi_food.Customer.CustomerLogInFragment;
import com.example.hi_food.Customer.RatingDialog;
import com.example.hi_food.Interfaces.DialogCallback;
import com.example.hi_food.R;
import com.example.hi_food.ResataurantManager.ManagerLogInFragment;
import com.hsalf.smilerating.SmileRating;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GlobalUtils {
    public final String TAG = "YOUR-TAG-NAME";
    public String rating = "Not given yet";
    public double rValue = 0.0;
    private EditText feedBack;
    String rest_id, c_id, ip, fBack;
    Context mContext;
    SharedPreferences sharedPreferences;

    public GlobalUtils(String rest_id, String c_id, String ip, Context mContext) {
        this.rest_id = rest_id;
        this.c_id = c_id;
        this.ip = ip;
        this.mContext = mContext;

    }


    public double getrValue() {
        return rValue;
    }

    public void showDialogRating(Context mContext, final DialogCallback dialogCallback) {
        final RatingDialog ratingDialog = new RatingDialog(mContext, R.style.CustomeDialog);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.rating_dialog, null);
        sharedPreferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        ratingDialog.setContentView(view);
        feedBack = view.findViewById(R.id.feedBack);
        SmileRating smileRating = ratingDialog.findViewById(R.id.smile_rating);
        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        rating = "Bad";
                        rValue = 2;
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        rating = "Good";
                        rValue = 4;
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        rating = "Great";
                        rValue = 5;
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "OKAY");
                        rating = "OKAY";
                        rValue = 3;
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        rating = "Terrible";
                        rValue = 1;
                        break;
                }
            }
        });
        Button btn_done = ratingDialog.findViewById(R.id.btn_done);
        btn_done.setOnClickListener(v -> {
            if (dialogCallback != null) {
                dialogCallback.callBack(rating);
            }
            ratingDialog.dismiss();
            System.out.println("Rating " + rValue + "\n feedBack" + feedBack.getText().toString());
            //Todo rate restaurant;
            fBack = feedBack.getText().toString();
            new RateRestaurantTask().execute();
        });
        ratingDialog.show();
    }

    class RateRestaurantTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/CustomerController/rateRestaurant.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("customer_id", c_id);
                uData.put("restaurant_id", rest_id);
                uData.put("customer_rate", rValue);
                uData.put("feed_back", rating + " " + fBack);
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
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
