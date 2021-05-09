package com.example.hi_food.ResataurantManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.hi_food.Admin.AdminMainPage;
import com.example.hi_food.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class SingUpManagerFragment extends Fragment {
    private EditText u_fullName, u_email, u_pass, u_confirmPassword;
    private Button singUp_btn, choose_file;
    private ImageView uploaded_image;
    ByteArrayOutputStream byteArrayOutputStream;
    String email, password, full_name, rest_name, confPass;
    Spinner restNameSpinner;
    byte[] byteArray;
    String ConvertImage;
    private int GALLERY = 1, CAMERA = 2;
    View mView;
    Context mContext;
    Bitmap FixBitmap;
    String ip;
    ArrayList<String> restaurantNames;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.manager_sing_up_fragment, container, false);
        sharedPreferences = mView.getContext().getSharedPreferences("Preferences", mView.getContext().MODE_PRIVATE);
        mContext = mView.getContext();
        u_email = mView.findViewById(R.id.u_s_email);
        u_pass = mView.findViewById(R.id.u_s_pass);
        u_fullName = mView.findViewById(R.id.u_fullName);
        u_confirmPassword = mView.findViewById(R.id.u_s_confPass);
        singUp_btn = mView.findViewById(R.id.singUp_btn);
        choose_file = mView.findViewById(R.id.open_file);
        uploaded_image = mView.findViewById(R.id.user_profile);
        Bundle b = getArguments();
        ip = b.getString("ip");
        restNameSpinner = mView.findViewById(R.id.restNameSpinner);
        new getRestTask().execute();
        restNameSpinner.setOnItemSelectedListener(new CustomOnItemSelectListener());
        choose_file.setOnClickListener(v -> {
            showPictureDialog();
        });
        singUp_btn.setOnClickListener(v -> {
            //Todo singup task
            System.out.println("Singup");
            UploadImageToServer();
            email = u_email.getText().toString();
            password = u_pass.getText().toString();
            confPass = u_confirmPassword.getText().toString();
            full_name = u_fullName.getText().toString();
            if (password.equals(confPass) && isValid(email)) {
                UploadTask uploadTask = new UploadTask();
                uploadTask.execute();
            } else {
                Toast.makeText(mContext, "Password or email are not correct!", Toast.LENGTH_LONG).show();
                int color = mContext.getColor(R.color.Red);
                u_email.setHighlightColor(color);
            }


        });
        if (ContextCompat.checkSelfPermission(mView.getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;
            }
            return false;
        });
        return mView;
    }

    class CustomOnItemSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            rest_name = restNameSpinner.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void addItemToSpinner(List<String> arrayList) {
        ArrayAdapter<String> typeAdapter;
        typeAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, arrayList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restNameSpinner.setAdapter(typeAdapter);
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

    private void showPictureDialog() {
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};

        new AlertDialog.Builder(mContext)
                .setTitle("Select Action").
                setItems(pictureDialogItems,
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    choosePhotoFromGallary();
                                    break;
                                case 1:
                                    takePhotoFromCamera();
                                    break;
                            }
                        }).show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                System.out.println("Path: " + contentURI.getEncodedPath());
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
                    Toast.makeText(mContext, "Image Saved!", Toast.LENGTH_SHORT).show();
                    uploaded_image.setImageBitmap(FixBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            uploaded_image.setImageBitmap(FixBitmap);
        }
    }

    public void UploadImageToServer() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    class UploadTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/singUpManager.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("manager_email", email);
                uData.put("password", password);
                uData.put("manager_name", full_name);
                uData.put("manager_img", email + ".jpg");
                uData.put("ImageData", ConvertImage);
                uData.put("restaurant_name", rest_name);
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
                    if (responseCode != 201) {
                        System.out.println("Error Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                        System.out.println(urlConnection.getErrorStream());
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
                    responseCode = urlConnection.getResponseCode();
                    responseMessage = urlConnection.getResponseMessage();
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
                Toast.makeText(mContext, "THERE WAS AN ERROR Response null", Toast.LENGTH_LONG).show();
                return;
            }
            if (responseCode != 201) {
                Toast.makeText(mContext, "THERE WAS AN ERROR response code is: " + responseCode, Toast.LENGTH_LONG).show();
                return;
            }
            String message = "";
            int flag = -2;

            try {
                JSONObject jsonObject = new JSONObject(response);
                flag = jsonObject.getInt("flag");
                message = jsonObject.getString("message");
                if (flag == 1) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("email", email);
                    intent.putExtra("full_name", full_name);
                    intent.putExtra("imageData", ConvertImage);
                    intent.putExtra("ip", ip);
                    startActivity(intent);
                }

            } catch (Exception e) {
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    class getRestTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/getRestNames.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
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
                        Toast.makeText(mContext, "There was an error! " + responseMessage, Toast.LENGTH_LONG).show();
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
                Toast.makeText(mContext, "THERE WAS AN ERROR Response null", Toast.LENGTH_LONG).show();
                return;
            }
            if (responseCode != 200) {
                Toast.makeText(mContext, "THERE WAS AN ERROR response code is: " + responseCode, Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject Restaurants_Info = jsonObject.getJSONObject("Restaurants_Info");
                JSONArray data = Restaurants_Info.getJSONArray("data");
                restaurantNames = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    restaurantNames.add(object.getString("restaurant_name"));
                }
                addItemToSpinner(restaurantNames);

            } catch (Exception e) {
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}