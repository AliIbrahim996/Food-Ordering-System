package com.example.hi_food.Adapters.Customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Model.Meal;
import com.example.hi_food.Model.Order;
import com.example.hi_food.R;

import java.util.List;

public class RecyclerViewMealAdapter extends RecyclerView.Adapter<RecyclerViewMealAdapter.MealHolder> {

    final Context mContext;
    private List<Meal> meals;
    private List<Order> orders;
    private String rest_ip;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public RecyclerViewMealAdapter(Context mContext, List<Meal> meals) {
        this.mContext = mContext;
        this.meals = meals;
    }

    @NonNull
    @Override
    public MealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        mView = layoutInflater.inflate(R.layout.meal_item, parent, false);

        return new MealHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MealHolder holder, int position) {
        final Meal meal = meals.get(position);
        Order o = orders.get(position);
        holder.name.setText(meal.getName());
        holder.calories.setText(meal.getCalories());
        holder.price.setText(meal.getPrice());
        new DownloadImage(holder.m_img).execute(meal.getImageURL());
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int q = o.getQty() + 1;
                orders.get(position).setQty(q);
                holder.quantity.setText(Integer.toString(q));

            }
        });
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int oldQuantity = Integer.parseInt(holder.quantity.getText().toString());
                if (oldQuantity > 0) {
                    int q = o.getQty() - 1;
                    orders.get(position).setQty(q);
                    holder.quantity.setText(Integer.toString(q));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public class MealHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, calories, price;
        final EditText quantity;
        Button btn_add, btn_minus, btn_book, btn_delivery;
        ImageView m_img;

        public MealHolder(@NonNull View View) {
            super(View);
            View.setOnClickListener(this);
            name = View.findViewById(R.id.m_name);
            calories = View.findViewById(R.id.m_calories);
            price = View.findViewById(R.id.m_price);
            m_img = View.findViewById(R.id.m_img);
            quantity = View.findViewById(R.id.m_quantity);
            quantity.setText("0");
            btn_add = View.findViewById(R.id.m_btnAdd);
            btn_minus = View.findViewById(R.id.m_btnMinus);
            btn_book = View.findViewById(R.id.m_book);
            btn_delivery = View.findViewById(R.id.m_delivery);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
