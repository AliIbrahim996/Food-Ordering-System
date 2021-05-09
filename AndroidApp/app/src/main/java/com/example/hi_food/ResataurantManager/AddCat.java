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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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

public class AddCat extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Context mContext;
    Bitmap FixBitmap;
    private ImageView uploaded_image;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    private int GALLERY = 1, CAMERA = 2;
    TextView cat_name;
    CheckBox addRestStatus_Value;
    Button addCatUploadBtn, addCatButton;
    String ConvertImage, name, email, full_name,ip;
    Toolbar myBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        full_name = intent.getStringExtra("full_name");
        ip = intent.getStringExtra("ip");
        uploaded_image = findViewById(R.id.catPic);
        cat_name = findViewById(R.id.cat_name);
        myBar = findViewById(R.id.myAppBar);
        setSupportActionBar(myBar);
        getSupportActionBar().setTitle("");
        myBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPress();
            }
        });
        addCatUploadBtn = findViewById(R.id.open_file);

        addCatUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        addCatButton = findViewById(R.id.addCat);

        addCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageToServer();
                name = cat_name.getText().toString();
                new AddCatTask().execute();
            }
        });

        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    private void backPress() {
        Intent intent = new Intent(getApplicationContext(), MainPage.class);
        intent.putExtra("email", email);
        intent.putExtra("full_name", full_name);
        intent.putExtra("ip", ip);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        backPress();
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

    class AddCatTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/CategoryController/createCategory.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("email", email);
                uData.put("name", name);
                uData.put("cat_image", email + "_" + full_name);
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
                    if (responseCode == 201) {
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
                if (responseCode == 201) {
                    JSONObject jsonObject = new JSONObject(response);
                    flag = jsonObject.getInt("flag");
                    message = jsonObject.getString("message");
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    if (flag == 1) {
                        clearFiled();
                    }
                } else {
                    Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                }


            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearFiled() {
        cat_name.setText("");
        uploaded_image.setImageResource(R.drawable.attach_48px);
    }
}
