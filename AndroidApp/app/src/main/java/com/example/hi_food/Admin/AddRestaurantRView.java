package com.example.hi_food.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi_food.R;

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

import static java.net.HttpURLConnection.HTTP_CREATED;

public class AddRestaurantRView extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Context mContext;
    Bitmap FixBitmap;
    private ImageView uploaded_image;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    private int GALLERY = 1, CAMERA = 2;
    TextView addRestName_Value, addRestLoc_Value, addRestPhone_Value, addRestOpenAt_Value, addRestCloseAt_Value;
    CheckBox addRestStatus_Value;
    Button addRestUploadBtn, addRestButton;
    String ConvertImage, full_name, rest_name, rest_loc, rest_phone, rest_openAt, rest_closeAt, rest_status, email;
    Toolbar myBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_restaurant_rview);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        System.out.println("Email "+ email);
        full_name = intent.getStringExtra("full_name");
        myBar = findViewById(R.id.myAppBar);
        setSupportActionBar(myBar);
        getSupportActionBar().setTitle("");
        myBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPress();
            }
        });
        addRestName_Value = findViewById(R.id.addRestName_Value);
        addRestLoc_Value = findViewById(R.id.addRestLoc_Value);
        addRestPhone_Value = findViewById(R.id.addRestPhone_Value);
        addRestOpenAt_Value = findViewById(R.id.addRestOpenAt_Value);
        addRestCloseAt_Value = findViewById(R.id.addRestCloseAt_Value);
        uploaded_image = findViewById(R.id.uploaded_image);
        addRestStatus_Value = findViewById(R.id.addRestStatus_Value);
        addRestUploadBtn = findViewById(R.id.addRestUploadBtn);
        addRestUploadBtn.setOnClickListener(v -> {
            showPictureDialog();
        });
        addRestButton = findViewById(R.id.addRestButton);
        addRestButton.setOnClickListener(v -> {
            UploadImageToServer();
            rest_name = addRestName_Value.getText().toString();
            rest_loc = addRestLoc_Value.getText().toString();
            rest_phone = addRestPhone_Value.getText().toString();
            rest_openAt = addRestOpenAt_Value.getText().toString();
            rest_closeAt = addRestCloseAt_Value.getText().toString();
            if (addRestStatus_Value.isChecked()) {
                rest_status = "1";
            } else {
                rest_status = "0";
            }
            UploadTask uploadTask = new UploadTask();
            uploadTask.execute();
        });
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        Intent intent = new Intent(getApplicationContext(), AdminMainPage.class);
        intent.putExtra("email", email);
        intent.putExtra("full_name", full_name);
        startActivity(intent);
        this.finish();
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
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", "192.168.1.13") +
                        "/HI-Food/API/Controller/RestaurantController/createRestaurant.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("admin_id", email);
                uData.put("restaurant_name", rest_name);
                uData.put("restaurant_address", rest_loc);
                uData.put("restaurant_phone_no", rest_phone);
                uData.put("is_accepted", rest_status);
                uData.put("restaurent_img", rest_name + ".jpg");
                uData.put("open_at", rest_openAt);
                uData.put("close_at", rest_closeAt);
                uData.put("ImageData", ConvertImage);
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
                    if (urlConnection.getResponseCode() == HTTP_CREATED) {
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
                if (response == null) {
                    Toast.makeText(mContext, "Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "Response Message " + responseMessage, Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                flag = jsonObject.getInt("flag");
                message = jsonObject.getString("message");
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UploadImageToServer() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    public void showPictureDialog() {
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        new AlertDialog.Builder(this).setTitle("Select Action")
                .setItems(pictureDialogItems, ((dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallary();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                })).show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    public void takePhotoFromCamera() {
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
                    FixBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
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
}