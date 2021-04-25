package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.proje.adimadimproje.Model.SpinnerModel;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SpinnerModel> {
    public SpinnerAdapter (Context context, List<SpinnerModel> spinnerModels){
        super(context,0, spinnerModels);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView (int position,View view,ViewGroup viewGroup){
        if (view==null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.row_spinner,viewGroup,false);
        ImageView imageView = view.findViewById(R.id.SpinnerImageView);
        TextView textView = view.findViewById(R.id.SpinnerTextView);
        SpinnerModel spinnerModel = getItem(position);
        if (spinnerModel !=null){
            imageView.setImageResource(spinnerModel.getImageView());
            textView.setText(spinnerModel.getTextView());
        }
        return view;
    }
}
