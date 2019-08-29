package com.example.mobileapplication.Model;

import com.example.mobileapplication.Model.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Officer extends Data implements Serializable {

        private String OfficerNumber;
        private String OfficerName;
        private String OfficerNationality;
        private String OfficerRole;

        public Officer() {
            OfficerNumber = "";
            OfficerName = "";
            OfficerNationality = "";
            OfficerRole = "";
        }

        public Officer (JSONObject json){
            try{
            OfficerNumber = json.getJSONObject("links").getJSONObject("officer").getString("appointments").replace("/officers/","").replace("/appointments","");
            OfficerName = json.getString("name");
                //OfficerNationality = json.getString("nationality");
                //OfficerRole = json.getString("officer_role");

            } catch (JSONException e){
                OfficerNumber = "";
                OfficerName = "";
                //OfficerNationality = "";
                //OfficerRole = "";
            }
        }

        public String getOfficerNumber() {
            return OfficerNumber;
        }

        public String getOfficerNationality() {
            return OfficerNationality;
        }

        public String getOfficerRole() {
            return OfficerRole;
        }

        public String getName() {
            return OfficerName;
        }

        public String getInitials() {
            return Character.toString(OfficerName.charAt(0)).toUpperCase();
        }

        public String toString() {
            return "\nOfficerNo: " + OfficerNumber + "\nOfficerName: " +OfficerName + "\nNationality: " +OfficerNationality + "\nRole: " +OfficerRole + "\n";
        }

}
