package com.codunite.myrecharge.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("Category")
    @Expose
    private String category;
    @SerializedName("CategoryDetails")
    @Expose
    private List<CategoryDetail> categoryDetails = null;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<CategoryDetail> getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(List<CategoryDetail> categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

}
