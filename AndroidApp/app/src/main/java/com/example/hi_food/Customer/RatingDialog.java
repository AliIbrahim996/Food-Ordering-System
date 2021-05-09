package com.example.hi_food.Customer;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

public class RatingDialog extends Dialog {

    public RatingDialog(@NonNull Context context) {
        super(context);
        this.setCancelable(false);
    }

    public RatingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.setCancelable(false);
    }


    @Override
    public void onBackPressed() {
        this.dismiss();
    }
}
