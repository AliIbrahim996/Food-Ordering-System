package com.example.hi_food.ResataurantManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi_food.API.DownloadImage;
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

public class UpdateTable extends AppCompatActivity {
    EditText number, location, numOfSeats, status;
    String ip, email, full_name, num, loc, numofS, st,table_id;
    Toolbar myBar;
    ImageView tab_imag;
    Button open_file, updateTabel;
    TextView barTxt;

    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    private int GALLERY = 1, CAMERA = 2;
    Context mContext;
    Bitmap FixBitmap;
    SharedPreferences sharedPreferences;
    boolean btn_pic_clicked;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_info);
        mContext = UpdateTable.this;
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        Intent intent = getIntent();
        number = findViewById(R.id.uTableNumber);
        location = findViewById(R.id.uTableLocation);
        numOfSeats = findViewById(R.id.uNumofSeats);
        status = findViewById(R.id.t_Status);
        open_file = findViewById(R.id.open_file);
        updateTabel = findViewById(R.id.updateTabel);
        ip = intent.getStringExtra("ip");
        email = intent.getStringExtra("email");
        full_name = intent.getStringExtra("full_name");
        table_id = intent.getStringExtra("table_id");
        myBar = findViewById(R.id.myAppBar);
        barTxt = findViewById(R.id.barTxt);
        barTxt.setText(barTxt.getText() + " " + intent.getStringExtra("table-number"));
        setSupportActionBar(myBar);
        getSupportActionBar().setTitle("");
        number.setText(intent.getStringExtra("table-number"));
        location.setText(intent.getStringExtra("table-location"));
        numOfSeats.setText(intent.getStringExtra("table-numOfSeats"));
        status.setText(intent.getStringExtra("table-status"));

        myBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPress();
            }
        });
        open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();

            }
        });
        tab_imag = findViewById(R.id.tab_image);
        new DownloadImage(tab_imag).execute(intent.getStringExtra("imageUrl"));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
        updateTabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_pic_clicked) {
                    UploadImageToServer();
                } else {
                    getImageData();
                }

                num = number.getText().toString();
                loc = location.getText().toString();
                numofS = numOfSeats.getText().toString();
                st = status.getText().toString();

                new updateTableTask().execute();
            }
        });
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

    private void getImageData() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) tab_imag.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void UploadImageToServer() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
        btn_pic_clicked = true;
    }

    public void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
        btn_pic_clicked = true;
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
                    Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    tab_imag.setImageBitmap(FixBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            tab_imag.setImageBitmap(FixBitmap);
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

    class updateTableTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;
        private ProgressDialog dialog = new ProgressDialog(UpdateTable.this);
        protected void onPreExecute() {
            this.dialog.setMessage("Processing...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/TableController/updateTable.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("table_id",table_id);
                uData.put("restaurant_id", email);
                uData.put("table_number", num);
                uData.put("table_status", st);
                uData.put("table_number_seats", numofS);
                uData.put("table_location", loc);
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
                    Toast.makeText(UpdateTable.this, "Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                    Toast.makeText(UpdateTable.this, "Response Message " + responseMessage, Toast.LENGTH_LONG).show();
                    return;
                }
                if (responseCode == 201) {
                    JSONObject jsonObject = new JSONObject(response);
                    flag = jsonObject.getInt("flag");
                    message = jsonObject.getString("message");

                    if (flag == 1) {
                        dialog.dismiss();
                        Toast.makeText(UpdateTable.this, message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UpdateTable.this, response, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                Toast.makeText(UpdateTable.this, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            dialog.dismiss();
        }
    }
}
