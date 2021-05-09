package com.example.hi_food.Adapters.Customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.API.TableReservation;
import com.example.hi_food.Customer.AddInfo;
import com.example.hi_food.Customer.BrowseMeals;
import com.example.hi_food.Interfaces.DialogCallback;
import com.example.hi_food.Model.Order;
import com.example.hi_food.Model.Table;
import com.example.hi_food.R;
import com.example.hi_food.utils.GlobalUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewTableAdapter extends RecyclerView.Adapter<RecyclerViewTableAdapter.TableHolder> {

    private Context mContext;
    private List<Table> tables;
    List<Order> orders;
    String ip, rest_email, c_email, rest_id,full_name;
    String table_id;
    SharedPreferences sharedPreferences;
    int meal_id;
    int quantity;

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRest_email() {
        return rest_email;
    }

    public void setRest_email(String rest_email) {
        this.rest_email = rest_email;
    }

    public String getC_email() {
        return c_email;
    }

    public void setC_email(String c_email) {
        this.c_email = c_email;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = new ArrayList<>();
        this.orders.addAll(orders);
    }

    public RecyclerViewTableAdapter(Context mContext, List<Table> tables) {
        this.mContext = mContext;
        this.tables = tables;
    }

    @NonNull
    @Override
    public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.table_item, parent, false);
        return new TableHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableHolder holder, int position) {
        Table table = tables.get(position);
        holder.number.setText(table.getTableNumber());
        holder.numOfChaires.setText(table.getNumberOfSeats());
        holder.location.setText(table.getLocation());
        new DownloadImage(holder.t_img).execute(table.getImageURL());
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public class TableHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView number, location, numOfChaires;
        Button book_table;
        ImageView t_img;

        public TableHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            number = itemView.findViewById(R.id.t_num);
            location = itemView.findViewById(R.id.t_loc);
            numOfChaires = itemView.findViewById(R.id.t_chairs);
            book_table = itemView.findViewById(R.id.t_book);
            t_img = itemView.findViewById(R.id.t_img);
        }

        @Override
        public void onClick(View v) {
            table_id = tables.get(getAdapterPosition()).getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Add Detail's");
            builder.setMessage("Do you want to add information about your reservation!");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Todo open Add information
                    AddInfo addInfo = new AddInfo();
                    Bundle bundle = new Bundle();
                    System.out.println("Size of orders " + orders.size());
                    bundle.putSerializable("orders", (Serializable) orders);
                    Intent intent = new Intent(mContext, addInfo.getClass());
                    intent.putExtra("ip", ip);
                    intent.putExtra("rest_email", rest_email);
                    intent.putExtra("c_email", c_email);
                    intent.putExtra("table_id", table_id);
                    intent.putExtra("rest_id", rest_id);
                    intent.putExtra("full_name", full_name);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Todo reserve table and meal and then show rating dialog

                    System.out.println("Orders Size: " + orders.size());
                    new TableReservation(
                            orders, mContext, ip
                            , rest_email, c_email
                            , table_id,
                            sharedPreferences
                    ).execute();
                    GlobalUtils g = new GlobalUtils(rest_id, c_email, ip, mContext);
                    g.showDialogRating(mContext, new DialogCallback() {
                        @Override
                        public void callBack(String ratings) {
                        }
                    });
                }
            }).show();
        }
    }
}
