package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class RetroPhoto {


    @SerializedName("photos")
    private Object title;


    public RetroPhoto(JSONObject title) {

        this.title = title;

    }

    Object getTitle() {
        return title;
    }


}

