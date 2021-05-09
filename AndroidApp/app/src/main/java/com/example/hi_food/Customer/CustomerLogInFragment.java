package com.example.hi_food.Customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.hi_food.R;
import com.example.hi_food.ResataurantManager.MainPage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class CustomerLogInFragment extends Fragment {

    String email;
    String password;
    Button login, cancel;
    EditText emailText, passwordText;
    View mView;
    SharedPreferences sharedPreferences;
    Context mContext;
    String ip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.c_singin_fragment, container, false);
        Bundle b = getArguments();
        ip = b.getString("ip");
        mContext = mView.getContext();
        sharedPreferences = mView.getContext().getSharedPreferences("Preferences", mView.getContext().MODE_PRIVATE);
        emailText = mView.findViewById(R.id.emailValue);
        passwordText = mView.findViewById(R.id.passValue);
        login = mView.findViewById(R.id.logIn);
        cancel = mView.findViewById(R.id.cancel);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo login Task
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(mContext, "please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isValid(email)) {
                    Toast.makeText(mContext, "please Enter Valid Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(mContext, "please Enter password", Toast.LENGTH_LONG).show();
                    return;
                }
                new LoginCustomerTask().execute();

            }
        });
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo clear fields
            }
        });
        return mView;
    }

    class LoginCustomerTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" +
                        sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/CustomerController/login.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("email", email);
                uData.put("password", password);
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
                    if (responseCode != 200) {
                        System.out.println("Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                        return null;
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
            if (response == null) {
                Toast.makeText(mContext, "THERE WAS AN ERROR! " + responseMessage, Toast.LENGTH_LONG).show();
                return;
            }
            if (responseCode != 200) {
                Toast.makeText(mContext, "THERE WAS AN ERROR response code is: " + responseCode, Toast.LENGTH_LONG).show();
                return;
            }
            String message = "";
            int flag = -2;

            try {
                JSONObject jsonObject = new JSONObject(response);
                flag = jsonObject.getInt("flag");
                message = jsonObject.getString("message");
                Toast.makeText(mContext,
                        message, Toast.LENGTH_LONG).show();
                if (flag == 1) {
                    String full_name = jsonObject.getString("full_name");
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("full_name", full_name);
                    intent.putExtra("ip", ip);
                    mContext.startActivity(intent);
                }

            } catch (Exception e) {
                Toast.makeText(mContext,
                        "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isValid(String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +

                "[a-zA-Z0-9_+&*-]+)*@" +

                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +

                "A-Z]{2,7}$";


        Pattern pat = Pattern.compile(emailRegex);

        if (email == null)

            return false;

        return pat.matcher(email).matches();

    }
}
