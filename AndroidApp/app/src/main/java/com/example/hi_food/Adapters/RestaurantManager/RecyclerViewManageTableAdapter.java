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
import com.example.hi_food.Model.Table;
import com.example.hi_food.R;
import com.example.hi_food.ResataurantManager.UpdateTable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RecyclerViewManageTableAdapter extends RecyclerView.Adapter<RecyclerViewManageTableAdapter.TabaleManagerHolder> {
    private Context mContext;
    private List<Table> tableList;
    String ip, email, table_id, full_name;
    SharedPreferences sharedPreferences;
    int pos;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getIp() {
        return ip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public RecyclerViewManageTableAdapter(Context mContext, List<Table> tableList) {
        this.mContext = mContext;
        this.tableList = tableList;
    }

    @NonNull
    @Override
    public TabaleManagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.table_manage_item, parent, false);
        return new TabaleManagerHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull TabaleManagerHolder holder, int position) {
        final Table table = tableList.get(position);
        holder.tableName.setText(table.getTableNumber());
        holder.tableStatus.setText(table.getTableStatus());
        holder.table_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo delete table
                table_id = tableList.get(position).getId();
                pos = position;
                new DeleteTask().execute();
            }
        });
        new DownloadImage(holder.t_img).execute(table.getImageURL());
        if (table.getTableStatus().toLowerCase().equals("unavailable")
        ) {
            holder.tableStatusImg.setImageResource(R.drawable.unavailable_96px);
        } else {
            holder.tableStatusImg.setImageResource(R.drawable.available_96px);

        }

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
                        "/HI-Food/API/Controller/TableController/deleteTable.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("table_id", table_id);
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
                        tableList.remove(pos);
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
        return tableList.size();
    }

    public class TabaleManagerHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tableName, tableStatus;
        ImageView tableStatusImg, table_delete, t_img;

        public TabaleManagerHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tableName = itemView.findViewById(R.id.mt_num);
            tableStatus = itemView.findViewById(R.id.table_status);
            tableStatusImg = itemView.findViewById(R.id.table_statusImg);
            table_delete = itemView.findViewById(R.id.table_delete);
            t_img = itemView.findViewById(R.id.t_img);
        }


        @Override
        public void onClick(View v) {
            //Todo open update
            Intent intent = new Intent(mContext, UpdateTable.class);
            intent.putExtra("table-number", tableList.get(getAdapterPosition()).getTableNumber());
            intent.putExtra("table-location", tableList.get(getAdapterPosition()).getLocation());
            intent.putExtra("table-numOfSeats", tableList.get(getAdapterPosition()).getNumberOfSeats());
            intent.putExtra("table-status", tableList.get(getAdapterPosition()).getTableStatus());
            intent.putExtra("table_id", tableList.get(getAdapterPosition()).getId());
            intent.putExtra("ip", ip);
            intent.putExtra("email", email);
            intent.putExtra("full_name", full_name);
            intent.putExtra("imageUrl", tableList.get(getAdapterPosition()).getImageURL());
            mContext.startActivity(intent);
        }
    }
}
