package com.example.hi_food.Adapters.Customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Customer.BrowseMeals;
import com.example.hi_food.Model.Category;
import com.example.hi_food.R;

import java.util.List;

public class RecyclerViewCategoryAdapter
        extends RecyclerView.Adapter<RecyclerViewCategoryAdapter.CategoryHolder> {

    private Context mContext;
    private List<Category> categories;
    String email, rest_id, rest_email, ip;

    public RecyclerViewCategoryAdapter(Context mContext, List<Category> categories) {
        this.mContext = mContext;
        this.categories = categories;
    }

    public RecyclerViewCategoryAdapter(Context applicationContext, List<Category> categories,
                                       String c_email, String rest_email, String rest_id, String ip) {
        this.mContext = applicationContext;
        this.categories = categories;
        this.email = c_email;
        this.rest_id = rest_id;
        this.rest_email = rest_email;
        this.ip = ip;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.category, parent, false);
        return new CategoryHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category category = categories.get(position);
        new DownloadImage(holder.cat_img).execute(category.getImageURL());
        holder.cat_name.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public  class CategoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView cat_img;
        TextView cat_name;
        CardView cat;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cat_img = itemView.findViewById(R.id.cat_img);
            cat_name = itemView.findViewById(R.id.cat_name);
            cat = itemView.findViewById(R.id.cat_id);
        }

        @Override
        public void onClick(View v) {
            Intent intent  =  new Intent(mContext, BrowseMeals.class);
            intent.putExtra("rest_id",rest_id);
            intent.putExtra("rest_email",rest_email);
            intent.putExtra("c_email",email);
            intent.putExtra("cat_id",categories.get(getAdapterPosition()).getId());
            intent.putExtra("ip",ip);
            mContext.startActivity(intent);
        }
    }
}
