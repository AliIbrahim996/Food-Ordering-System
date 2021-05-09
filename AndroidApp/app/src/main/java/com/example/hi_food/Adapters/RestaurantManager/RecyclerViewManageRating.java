package com.example.hi_food.Adapters.RestaurantManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Model.Rate;
import com.example.hi_food.R;

import java.util.List;

public class RecyclerViewManageRating extends RecyclerView.Adapter<RecyclerViewManageRating.ManageRatingViewHolder>{

    private Context mContext;
    private List<Rate> ratings;

    public RecyclerViewManageRating(Context mContext, List<Rate> meals) {
        this.mContext = mContext;
        this.ratings = meals;
    }

    @NonNull
    @Override
    public ManageRatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView=inflater.inflate(R.layout.rating_item,parent,false);

        return new ManageRatingViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManageRatingViewHolder holder, int position) {
        final Rate rate=ratings.get(position);

        holder.ratingValue.setText(rate.getValue()+"");
        holder.customer_name.setText(rate.getC_name());
       // holder.rest_id=rate.getRestaurant_id();

        holder.showFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("FeedBack");
                builder.setMessage(rate.getC_name()+"\nFeedBack: "+rate.getFeedBack());
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public  static  class ManageRatingViewHolder extends RecyclerView.ViewHolder{

        TextView ratingValue,customer_name;
        Button showFeedBack;
      //  int rest_id;

        public ManageRatingViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingValue=itemView.findViewById(R.id.rating_ItemValue);
            showFeedBack= itemView.findViewById(R.id.rating_showFeedBack);
            customer_name= itemView.findViewById(R.id.ratingCName);
        }
    }
}
