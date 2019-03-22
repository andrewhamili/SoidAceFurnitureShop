package com.project.orderingapp.Models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class ApiResponseObject {

    @Expose
    public int code;

    @Expose
    public JsonObject content;

}
