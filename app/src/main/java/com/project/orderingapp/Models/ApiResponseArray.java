package com.project.orderingapp.Models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;

public class ApiResponseArray {

    @Expose
    public int code;

    @Expose
    public JsonArray content;

}
