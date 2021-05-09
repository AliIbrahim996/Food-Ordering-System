package com.example.hi_food.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecyclerViewLogBtnAdapter  extends RecyclerView.Adapter<RecyclerViewLogBtnAdapter.BtnHolderView>{

    private Context mContext;

    public RecyclerViewLogBtnAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BtnHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);

        View  mView = inflater.inflate(R.layout.buttons,parent,false);

        return  new BtnHolderView(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull BtnHolderView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class BtnHolderView extends RecyclerView.ViewHolder {
        FloatingActionButton fb,google,tw;
        float v=0;
        public BtnHolderView(@NonNull View itemView) {
            super(itemView);

            fb=itemView.findViewById(R.id.fab_fb);
            google=itemView.findViewById(R.id.fab_google);
            tw=itemView.findViewById(R.id.fab_twitter);
            fb.setTranslationX(300);
            google.setTranslationX(300);
            fb.setAlpha(v);
            google.setAlpha(v);
            tw.setAlpha(v);
            fb.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();
            google.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
            tw.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        }
    }
}
