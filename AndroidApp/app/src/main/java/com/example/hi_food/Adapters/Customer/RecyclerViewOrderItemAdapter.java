package com.example.hi_food.Adapters.Customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Customer.OrderBell;
import com.example.hi_food.Model.Order;
import com.example.hi_food.R;
import java.util.List;

public class RecyclerViewOrderItemAdapter  extends RecyclerView.Adapter
        <RecyclerViewOrderItemAdapter.OrderItemViewHolder>{

    private Context mContext;
    List<Order> orders;
    private double totalPrice;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice += totalPrice;
        System.out.println("Total Price :=  "+this.totalPrice);
    }

    public RecyclerViewOrderItemAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
        this.totalPrice=0.0;
    }



    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(mContext).inflate(R.layout.order_item,parent,false);

        return new OrderItemViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        if(orders != null && orders.size()>0) {
            String quantity = orders.get(position).getQty() + "";
            String price = orders.get(position).getPrice() + "";
            holder.mealName.setText(orders.get(position).getMealName());
            holder.qty.setText(quantity);
            holder.price.setText(price+" $");
            holder.totalPrice = orders.get(position).getQty() * orders.get(position).getPrice();
        }
        else
            return;

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder{
        TextView mealName,qty,price;
        double totalPrice;
        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName =itemView.findViewById(R.id.mealNameCol);
            qty =itemView.findViewById(R.id.mealQtyCol);
            price =itemView.findViewById(R.id.mealUnitPrice);
            totalPrice= 0.0;



        }
    }
}
