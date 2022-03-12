package edu.cmu.project4task1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText number;
    EditText message;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity m = this;
        // finds all the views present in the application
        radioGroup = findViewById(R.id.operation);
        number = findViewById(R.id.phoneNumber);
        message = findViewById(R.id.message);
        textView = findViewById(R.id.details);

        Button submitButton = (Button) findViewById(R.id.submit);

        // shows the message edit text when switching to send message and hides it when in lookup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                if (radioId == R.id.sendMessage) {
                    message.setVisibility(View.VISIBLE);
                } else {
                    message.setVisibility(View.INVISIBLE);
                }
            }
        });
        // Add a listener to the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {
                if (internet_connection()) {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                String numberEntered = ((EditText) findViewById(R.id.phoneNumber)).getText().toString();
                Project4Task1Model model = new Project4Task1Model();
                if (radioId == R.id.sendMessage) {
                    String messageToSend = ((EditText) findViewById(R.id.message)).getText().toString();
                    if (messageToSend.equals("") || !(messageToSend != null)) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Please enter a valid message");
                    } else if (numberEntered.equals("") || !(numberEntered != null)) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Please enter a number");
                    } else {
                        model.sendMessage(numberEntered, messageToSend, m);
                    }

                } else {
                    if (numberEntered.equals("") || !(numberEntered != null)) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Please enter a number");
                    } else {
                        model.lookup(numberEntered, m);
                    }
                }
            } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No internet connection");
                }
            }
        });
    }

    // get the selected radio button
    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }

    // to clear the fields
    public void clearFields() {
        number.setText("");
        message.setText("");
    }

    /*
     * This is called by the Project4Model object after getting the response from the webservice.
     * for lookup operation
     */
    public void lookupDisplay(String data) {
        textView.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        String show = "";
        try {
            jsonObject = (JSONObject) parser.parse(data);
            if(jsonObject.containsKey("error_message")){
                show = jsonObject.get("error_message").toString();
            } else {
                show = "Caller name: " + jsonObject.get("caller_name") +"\n"
                        + "Caller type: " + jsonObject.get("caller_type") +"\n"
                        + "Carrier name: " + jsonObject.get("carrier_name") +"\n"
                        + "Carrier type: " + jsonObject.get("carrier_type") +"\n"
                        + "Country code: " + jsonObject.get("country_code");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textView.setText(show);
        clearFields();
    }

    /*
     * This is called by the Project4Model object after getting the response from the webservice.
     * for send message operation
     */
    public void sendMessageDisplay(String data) {
        textView.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        String show = "";
        try {
            jsonObject = (JSONObject) parser.parse(data);
            if(jsonObject.containsKey("error_message")){
                show = jsonObject.get("error_message").toString();
            } else {
                show = "Date sent: " + jsonObject.get("date_created") +"\n"
                        + "Status: " + jsonObject.get("status");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textView.setText(show);
        clearFields();
    }

    // source from https://stackoverflow.com/questions/37232927/app-crashes-when-no-internet-connection-is-available
    boolean internet_connection(){
        //Check if connected to internet
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }
}