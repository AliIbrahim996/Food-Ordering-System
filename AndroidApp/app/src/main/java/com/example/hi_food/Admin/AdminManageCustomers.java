package com.example.hi_food.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hi_food.Adapters.Admin.RecyclerViewBrowseCustomer;
import com.example.hi_food.Model.Customer;
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
import java.util.ArrayList;
import java.util.List;

public class AdminManageCustomers extends Fragment {

    RecyclerView recyclerView;
    Context mContext;
    String ip, email;
    SharedPreferences sharedPreferences;
    RecyclerViewBrowseCustomer adapter;
    List<Customer> customers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_admin_manage_customers, container, false);
        mContext = mView.getContext();
        Bundle b = getArguments();
        ip = b.getString("ip");
        email = b.getString("email");
        mContext = mView.getContext();
        sharedPreferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        recyclerView = mView.findViewById(R.id.adminManageCustomersRView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //Todo get all customers
        new getCustomersTask().execute();
        return mView;
    }

    public class getCustomersTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/AdminController/getAllCustomers.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                try {
                    responseCode = urlConnection.getResponseCode();
                    responseMessage = urlConnection.getResponseMessage();
                    if (urlConnection.getResponseCode() != 200) {
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
                if (flag == 1) {
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Customers_Info");
                    JSONArray data = Restaurants_Info.getJSONArray("data");
                    collectData(data);
                    System.out.println(data);
                } else {
                    Toast.makeText(mContext, message + " Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void collectData(JSONArray data) throws JSONException {
        customers = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            Customer c = new Customer();
            String id = element.get("id").toString();
            String email = element.get("email").toString();
            String full_name = element.get("full_name").toString();
            String address = element.get("address").toString();
            String gender = element.get("gender").toString();
            String phone_number = element.get("phone_number").toString();
            String c_image = element.get("c_image").toString();
            c.setEmail(email);
            c.setC_name(full_name);
            c.setGender(gender);
            c.setAddress(address);
            c.setImageUrl(c_image);
            c.setC_id(Integer.parseInt(id));
            c.setC_phoneNum(phone_number);
            customers.add(c);
        }
        adapter = new RecyclerViewBrowseCustomer(mContext, customers);
        recyclerView.setAdapter(adapter);
    }
}
