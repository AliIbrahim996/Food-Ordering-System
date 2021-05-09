package com.example.hi_food.ResataurantManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Model.Category;
import com.example.hi_food.R;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;

public class UpdateMeal extends AppCompatActivity {
    Button open_file, updateMeal;
    TextView barTxt;
    Toolbar myBar;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    List<Category> categories;
    ImageView mImage;
    private int GALLERY = 1, CAMERA = 2;
    Context mContext;
    Bitmap FixBitmap;
    String cat_id;
    int menu_id;
    SharedPreferences sharedPreferences;
    boolean btn_pic_clicked;
    String ip, email, full_name, mName, mCalories, mPrice, mCat_name, meal_id;
    Spinner addMealCategory;
    EditText meal_name, meal_price, cat_name, meal_calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_meal);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        full_name = intent.getStringExtra("full_name");
        ip = intent.getStringExtra("ip");
        meal_id = intent.getStringExtra("meal_id");
        myBar = findViewById(R.id.myAppBar);
        barTxt = findViewById(R.id.barTxt);
        barTxt.setText(barTxt.getText() + " " + intent.getStringExtra("meal_name"));
        setSupportActionBar(myBar);
        getSupportActionBar().setTitle("");
        addMealCategory = findViewById(R.id.uMCategory);
        new getCategoriesTask().execute();
        addMealCategory.setOnItemSelectedListener(new CustomOnItemSelectListener());
        meal_name = findViewById(R.id.uMNameValue);
        meal_name.setText(intent.getStringExtra("meal_name"));
        meal_calories = findViewById(R.id.uMCaloriesValue);
        meal_calories.setText(intent.getStringExtra("meal_calories"));
        meal_price = findViewById(R.id.aMPriceValue);
        meal_price.setText(intent.getStringExtra("meal_price"));
        cat_name = findViewById(R.id.cat_name);
        cat_name.setText(intent.getStringExtra("cat_name"));
        mImage = findViewById(R.id.mImage);
        open_file = findViewById(R.id.open_file);
        updateMeal = findViewById(R.id.updateMeal);
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
        open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();

            }
        });
        new DownloadImage(mImage).execute(intent.getStringExtra("imageUrl"));
        updateMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_pic_clicked) {
                    UploadImageToServer();
                } else {
                    getImageData();
                }
                mName = meal_name.getText().toString();
                mCalories = meal_calories.getText().toString();
                mPrice = meal_price.getText().toString();
                mCat_name = cat_name.getText().toString();
                new updateMealTask().execute();
            }
        });
    }

    class updateMealTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", "192.168.1.13") +
                        "/HI-Food/API/Controller/RestaurantManagerController/updateMeal.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("meal_id", meal_id);
                uData.put("meal_name", mName);
                uData.put("calories", mCalories);
                uData.put("category_id", cat_id);
                uData.put("price", mPrice);
                uData.put("meal_image", email + "_" + full_name + "_" + mCat_name + "_" + mName);
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

                    if (flag == 1) {
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
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

    private void addToSpinner(ArrayList<String> names) {
        ArrayAdapter<String> typeAdapter;
        typeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, names);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addMealCategory.setAdapter(typeAdapter);
    }

    class CustomOnItemSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            cat_id = categories.get(position).getId();
            menu_id = categories.get(position).getMenu_id();
            cat_name.setText(categories.get(position).getName());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void getImageData() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
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
                    Toast.makeText(mContext, "Image Saved!", Toast.LENGTH_SHORT).show();
                    mImage.setImageBitmap(FixBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            mImage.setImageBitmap(FixBitmap);
        }
    }

    class getCategoriesTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/CategoryController/getAllCategories.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("email", email);
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Categories_Info");
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
        categories = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            String cat_id = element.getString("cat_id");
            String cat_name = element.getString("cat_name");
            String cat_imgUrl = element.getString("cat_imgUrl");
            String menu_id = element.getString("menu_id");
            categories.add(new
                    Category(cat_name, cat_id, cat_imgUrl, Integer.parseInt(menu_id)));
            names.add(cat_name);
        }
        addToSpinner(names);
    }
}
