package com.cr4zyrocket.foodappserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cr4zyrocket.foodappserver.Model.Manager;
import com.cr4zyrocket.foodappserver.Model.Request;

public class Common {
    public static Request currentRequest;
    public static String currentLanguage="en";
    public static String getCurrentManagerPhone(){
        if (currentManager!=null)
            return currentManager.getPhone();
        else return "null";
    }
    public static String getCurrentManagerName(){
        if (currentManager!=null)
            return currentManager.getName();
        else return "null";
    }
    public static Manager currentManager;
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if (info!=null){
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
    public static String convertCodeToStatus(String status){
        switch (status) {
            case "0":
                return "Placed";
            case "1":
                return "Processing";
            case "2":
                return "On my way";
            case "3":
                return "Shipped";
            default:
                return "Cancel";
        }
    }
    public static final String DELETE="Delete";
    public static final String EDIT="Edit";
    public static final String UPDATE="Update";
    public static final String SHOW_DETAILS="Show details";
}
