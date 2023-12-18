package com.example.temiproject.inventory;

import com.example.temiproject.R;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public static List<Gender> getGenderList() {
        List<Gender> genderList = new ArrayList<>();

        Gender male = new Gender();
        male.setName("男");
        male.setImage(R.drawable.male);
        genderList.add(male);

        Gender female = new Gender();
        female.setName("女");
        female.setImage(R.drawable.female);
        genderList.add(female);

        return genderList;
    }
}
