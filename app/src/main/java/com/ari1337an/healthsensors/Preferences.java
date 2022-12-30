package com.ari1337an.healthsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class Preferences extends AppCompatActivity {

    private UserSettings settings;
    String[] items = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    Spinner item1Dropdown;
    EditText item1NameE;
    EditText item1DescE;
    EditText item1SufInput;

    Spinner item2Dropdown;
    EditText item2NameE;
    EditText item2DescE;
    EditText item2SufInput;

    Spinner item3Dropdown;
    EditText item3NameE;
    EditText item3DescE;
    EditText item3SufInput;

    Spinner item4Dropdown;
    EditText item4NameE;
    EditText item4DescE;
    EditText item4SufInput;

    EditText startingChar;
    EditText endingChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // Initiate All UI
        initUIElements();

        // Populate all Dropdowns
        populateDropDown(item1Dropdown);
        populateDropDown(item2Dropdown);
        populateDropDown(item3Dropdown);
        populateDropDown(item4Dropdown);

        // Init Settings
        settings = (UserSettings) getApplication();

        // Load all sharedPreferences
        settings.loadAllSharedPreferences();

        // Load Settings Value In UI
        loadValueToUI();
    }

    private void initUIElements() {
        item1Dropdown = findViewById(R.id.item1IndexDropdown);
        item1NameE = findViewById(R.id.item1NameInput);
        item1DescE = findViewById(R.id.item1DescInput);
        item1SufInput = findViewById(R.id.item1SufInput);

        item2Dropdown = findViewById(R.id.item2IndexDropdown);
        item2NameE = findViewById(R.id.item2NameInput);
        item2DescE = findViewById(R.id.item2DescInput);
        item2SufInput = findViewById(R.id.item2SufInput);

        item3Dropdown = findViewById(R.id.item3IndexDropdown);
        item3NameE = findViewById(R.id.item3NameInput);
        item3DescE = findViewById(R.id.item3DescInput);
        item3SufInput = findViewById(R.id.item3SufInput);

        item4Dropdown = findViewById(R.id.item4IndexDropdown);
        item4NameE = findViewById(R.id.item4NameInput);
        item4DescE = findViewById(R.id.item4DescInput);
        item4SufInput = findViewById(R.id.item4SufInput);

        startingChar = findViewById(R.id.startingCharInput);
        endingChar = findViewById(R.id.endingCharInput);
    }

    private void loadValueToUI() {
        item1Dropdown.setSelection(Arrays.asList(items).indexOf(settings.getItem1Index()));
        item1NameE.setText(settings.getItem1Name());
        item1DescE.setText(settings.getItem1Desc());
        item1SufInput.setText(settings.getItem1Suf());

        item2Dropdown.setSelection(Arrays.asList(items).indexOf(settings.getItem2Index()));
        item2NameE.setText(settings.getItem2Name());
        item2DescE.setText(settings.getItem2Desc());
        item2SufInput.setText(settings.getItem2Suf());

        item3Dropdown.setSelection(Arrays.asList(items).indexOf(settings.getItem3Index()));
        item3NameE.setText(settings.getItem3Name());
        item3DescE.setText(settings.getItem3Desc());
        item3SufInput.setText(settings.getItem3Suf());

        item4Dropdown.setSelection(Arrays.asList(items).indexOf(settings.getItem4Index()));
        item4NameE.setText(settings.getItem4Name());
        item4DescE.setText(settings.getItem4Desc());
        item4SufInput.setText(settings.getItem4Suf());

        startingChar.setText(settings.getStartingChar());
        endingChar.setText(settings.getEndingChar());
    }

    public void resetSharedPref(View view){
        SharedPreferences pref = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        pref.edit().clear().apply();
        Toast.makeText(settings, "Reset Done!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void savePreference(View view) {

        // Universal
        SharedPreferences.Editor editor =  getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();

        // Make All Changes
        editor.putString(UserSettings.ITEM1_INDEX, items[item1Dropdown.getSelectedItemPosition()]);
        editor.putString(UserSettings.ITEM1_NAME, String.valueOf(item1NameE.getText()));
        editor.putString(UserSettings.ITEM1_DESC, String.valueOf(item1DescE.getText()));
        editor.putString(UserSettings.ITEM1_SUF, String.valueOf(item1SufInput.getText()));

        editor.putString(UserSettings.ITEM2_INDEX, items[item2Dropdown.getSelectedItemPosition()]);
        editor.putString(UserSettings.ITEM2_NAME, String.valueOf(item2NameE.getText()));
        editor.putString(UserSettings.ITEM2_DESC, String.valueOf(item2DescE.getText()));
        editor.putString(UserSettings.ITEM2_SUF, String.valueOf(item2SufInput.getText()));

        editor.putString(UserSettings.ITEM3_INDEX, items[item3Dropdown.getSelectedItemPosition()]);
        editor.putString(UserSettings.ITEM3_NAME, String.valueOf(item3NameE.getText()));
        editor.putString(UserSettings.ITEM3_DESC, String.valueOf(item3DescE.getText()));
        editor.putString(UserSettings.ITEM3_SUF, String.valueOf(item3SufInput.getText()));

        editor.putString(UserSettings.ITEM4_INDEX, items[item4Dropdown.getSelectedItemPosition()]);
        editor.putString(UserSettings.ITEM4_NAME, String.valueOf(item4NameE.getText()));
        editor.putString(UserSettings.ITEM4_DESC, String.valueOf(item4DescE.getText()));
        editor.putString(UserSettings.ITEM4_SUF, String.valueOf(item4SufInput.getText()));

        if(!(startingChar.getText().length() == 1 && endingChar.getText().length() == 1)) {
            Toast.makeText(settings, "Starting/Ending Character Length Must Be 1 and Non Empty!", Toast.LENGTH_SHORT).show();
        }
        else {
            editor.putString(UserSettings.STARTING_CHAR, String.valueOf(startingChar.getText()));
            editor.putString(UserSettings.ENDING_CHAR, String.valueOf(endingChar.getText()));

            // Universal
            editor.apply();

            // Reload the data
            settings.loadAllSharedPreferences();

            Toast.makeText(settings, "Saved Preferences!", Toast.LENGTH_SHORT).show();
            // Finish the Activity
            finish();
        }

    }

    private void populateDropDown(Spinner dropdown) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

}