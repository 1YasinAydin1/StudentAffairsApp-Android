package com.proje.adimadimproje.Model;

import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerModel {
    int  imageView;
    String textView;

    public SpinnerModel(int imageView, String textView) {
        this.imageView = imageView;
        this.textView = textView;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getTextView() {
        return textView;
    }

    public void setTextView(String textView) {
        this.textView = textView;
    }
}
