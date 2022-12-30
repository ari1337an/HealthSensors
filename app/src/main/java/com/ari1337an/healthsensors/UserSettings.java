package com.ari1337an.healthsensors;

import android.app.Application;
import android.content.SharedPreferences;

public class UserSettings extends Application {
    public static final String PREFERENCES = "preferences";

    // REFS
    public static final String ITEM1_INDEX = "item1Index";
    public static final String ITEM1_NAME = "item1Name";
    public static final String ITEM1_DESC = "item1Desc";
    public static final String ITEM1_SUF = "item1Suf";

    public static final String ITEM2_INDEX = "item2Index";
    public static final String ITEM2_NAME = "item2Name";
    public static final String ITEM2_DESC = "item2Desc";
    public static final String ITEM2_SUF = "item2Suf";

    public static final String ITEM3_INDEX = "item3Index";
    public static final String ITEM3_NAME = "item3Name";
    public static final String ITEM3_DESC = "item3Desc";
    public static final String ITEM3_SUF = "item3Suf";

    public static final String ITEM4_INDEX = "item4Index";
    public static final String ITEM4_NAME = "item4Name";
    public static final String ITEM4_DESC = "item4Desc";
    public static final String ITEM4_SUF = "item4Suf";

    public static final String STARTING_CHAR = "$";
    public static final String ENDING_CHAR = ";";

    // FIELDS
    private String item1Index;
    private String item1Name;
    private String item1Desc;
    private String item1Suf;

    private String item2Index;
    private String item2Name;
    private String item2Desc;
    private String item2Suf;

    private String item3Index;
    private String item3Name;
    private String item3Desc;
    private String item3Suf;

    private String item4Index;
    private String item4Name;
    private String item4Desc;
    private String item4Suf;

    private String startingChar;
    private String endingChar;

    void loadAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);

        this.setItem1Index(sharedPreferences.getString(UserSettings.ITEM1_INDEX, String.valueOf(7)));
        this.setItem1Name(sharedPreferences.getString(UserSettings.ITEM1_NAME, "Temperature"));
        this.setItem1Desc(sharedPreferences.getString(UserSettings.ITEM1_DESC, "Get to know your temperature around you!"));
        this.setItem1Suf(sharedPreferences.getString(UserSettings.ITEM1_SUF, "°")); // degree sign

        this.setItem2Index(sharedPreferences.getString(UserSettings.ITEM2_INDEX, String.valueOf(5)));
        this.setItem2Name(sharedPreferences.getString(UserSettings.ITEM2_NAME, "Humidity"));
        this.setItem2Desc(sharedPreferences.getString(UserSettings.ITEM2_DESC, "Get to know humidity around you!"));
        this.setItem2Suf(sharedPreferences.getString(UserSettings.ITEM2_SUF, "%")); // degree sign

        this.setItem3Index(sharedPreferences.getString(UserSettings.ITEM3_INDEX, String.valueOf(3)));
        this.setItem3Name(sharedPreferences.getString(UserSettings.ITEM3_NAME, "Sp02"));
        this.setItem3Desc(sharedPreferences.getString(UserSettings.ITEM3_DESC, "Get to know your Sp02!"));
        this.setItem3Suf(sharedPreferences.getString(UserSettings.ITEM3_SUF, "%")); // degree sign

        this.setItem4Index(sharedPreferences.getString(UserSettings.ITEM4_INDEX, String.valueOf(1)));
        this.setItem4Name(sharedPreferences.getString(UserSettings.ITEM4_NAME, "Pulse"));
        this.setItem4Desc(sharedPreferences.getString(UserSettings.ITEM4_DESC, "Get to know your pulse!"));
        this.setItem4Suf(sharedPreferences.getString(UserSettings.ITEM4_SUF, "bpm")); // degree sign

        this.setStartingChar(sharedPreferences.getString(UserSettings.STARTING_CHAR, "$"));
        this.setEndingChar(sharedPreferences.getString(UserSettings.ENDING_CHAR, ";"));
    }

    public String getStartingChar() { return startingChar; }

    public void setStartingChar(String startingChar) { this.startingChar = startingChar; }

    public String getItem2Index() {
        return item2Index;
    }

    public void setItem2Index(String item2Index) {
        this.item2Index = item2Index;
    }

    public String getItem2Name() {
        return item2Name;
    }

    public void setItem2Name(String item2Name) {
        this.item2Name = item2Name;
    }

    public String getItem2Desc() {
        return item2Desc;
    }

    public void setItem2Desc(String item2Desc) {
        this.item2Desc = item2Desc;
    }

    public String getItem2Suf() {
        return item2Suf;
    }

    public void setItem2Suf(String item2Suf) {
        this.item2Suf = item2Suf;
    }

    public String getItem3Index() {
        return item3Index;
    }

    public void setItem3Index(String item3Index) {
        this.item3Index = item3Index;
    }

    public String getItem3Name() {
        return item3Name;
    }

    public void setItem3Name(String item3Name) {
        this.item3Name = item3Name;
    }

    public String getItem3Desc() {
        return item3Desc;
    }

    public void setItem3Desc(String item3Desc) {
        this.item3Desc = item3Desc;
    }

    public String getItem3Suf() {
        return item3Suf;
    }

    public void setItem3Suf(String item3Suf) {
        this.item3Suf = item3Suf;
    }

    public String getItem4Index() {
        return item4Index;
    }

    public void setItem4Index(String item4Index) {
        this.item4Index = item4Index;
    }

    public String getItem4Name() {
        return item4Name;
    }

    public void setItem4Name(String item4Name) {
        this.item4Name = item4Name;
    }

    public String getItem4Desc() {
        return item4Desc;
    }

    public void setItem4Desc(String item4Desc) {
        this.item4Desc = item4Desc;
    }

    public String getItem4Suf() {
        return item4Suf;
    }

    public void setItem4Suf(String item4Suf) {
        this.item4Suf = item4Suf;
    }

    public String getItem1Suf() {
        return item1Suf;
    }

    public void setItem1Suf(String item1Suf) {
        this.item1Suf = item1Suf;
    }

    public String getItem1Desc() {
        return item1Desc;
    }

    public void setItem1Desc(String item1Desc) {
        this.item1Desc = item1Desc;
    }

    public String getItem1Name() {
        return item1Name;
    }

    public void setItem1Name(String item1Name) {
        this.item1Name = item1Name;
    }

    public String getItem1Index() {
        return item1Index;
    }

    public void setItem1Index(String item1Index) {
        this.item1Index = item1Index;
    }

    public String getEndingChar() { return endingChar; }

    public void setEndingChar(String endingChar) { this.endingChar = endingChar; }
}
