package com.example.temiproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.temiproject.inventory.Gender;

import java.util.List;

public class GenderAdapter extends BaseAdapter {
    private Context context;
    private List<Gender> genderList;

    public GenderAdapter(Context context, List<Gender> genderList) {
        this.context = context;
        this.genderList = genderList;
    }

    @Override
    public int getCount() {
        return genderList != null ? genderList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_gender, viewGroup,false);

        TextView txtName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        txtName.setText(genderList.get(i).getName());
        image.setImageResource(genderList.get(i).getImage());

        return rootView;
    }
}
