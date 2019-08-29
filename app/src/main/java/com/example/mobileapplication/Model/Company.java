package com.example.mobileapplication.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

// company class
public class Company extends Data implements Serializable {
    private String CompanyNumber;
    private String CompanyName;
    private String CompanyDesc;
    private String CompanyAdd;

    public Company() {
        CompanyNumber = "";
        CompanyName = "";
        CompanyDesc = "";
        CompanyAdd = "";
    }

    // get company number, name, desc, add with json
    public Company (JSONObject json){
        try{
            if(json.has("appointed_to")) {
                CompanyNumber = json.getJSONObject("appointed_to").getString("company_number");
                CompanyName = json.getJSONObject("appointed_to").getString("company_name");

            }else{
                CompanyNumber = json.getString("company_number");
                CompanyName = json.getString("title");
                CompanyDesc = json.getString("description");
                CompanyAdd = json.getString("address_snippet");
            }

        } catch (JSONException e){
            CompanyNumber = "";
            CompanyName = "";
            CompanyDesc = "";
            CompanyAdd = "";
        }
    }

    public String getCompanyDesc() {
        return CompanyDesc;
    }

    public String getCompanyAdd() {
        return CompanyAdd;
    }

    public String getCompanyNumber() {
        return CompanyNumber;
    }

    public String getName() {
        return CompanyName;
    }

    public String getInitials() {
        return Character.toString(CompanyName.charAt(0)).toUpperCase();
    }

    public String toString() {
        return "\nName: " + CompanyName + "\nCompanyNo: " +CompanyNumber + "\nDescription: " +CompanyDesc + "\nAddress: " +CompanyAdd + "\n";
    }
}
