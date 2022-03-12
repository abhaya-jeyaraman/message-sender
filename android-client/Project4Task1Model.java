package edu.cmu.project4task1;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * This class provides capabilities to lookup the details for a number from the webservice.  The method "lookup" and "sendMessage" are the entry to the class.
 * onPostExecution runs in the UI thread, and it calls the lookupDisplay or sendMessageDisplay method accordingly to do the update.
 *
 */

public class Project4Task1Model {
    MainActivity m = null;
    /*
     * lookup is the public Project4Task1Model method.  Its arguments are a phone number , and the MainActivity object that called it.This provides a callback
     * path such that the lookupDisplay method in that object is called when the response is got from webservice.
     */
    public void lookup(String number, MainActivity m) {
        this.m = m;
        new AsyncLookup().execute(number);
    }

    /*
     * sendMessage is the public Project4Task1Model method.  Its arguments are a phone number , a message and the MainActivity object that called it.  This provides a callback
     * path such that the sendMessageDisplay method in that object is called when the response is got from webservice.
     */
    public void sendMessage(String number, String message, MainActivity m) {
        this.m = m;
        new AsyncSendMessage().execute(number,message);
    }

    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * This class calls the web service deployed in heroku
     */
    private class AsyncLookup extends AsyncTask<String, Integer, String>{

        protected String lookup(String number) {
            HttpURLConnection conn;
            int status = 0;
            String result;
            String responseBody = null;
            try {
                URL url = new URL("https://warm-oasis-60714.herokuapp.com/lookup?number=" + number);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                status = conn.getResponseCode();
                result = conn.getResponseMessage();

                if (status == 200) {
                    responseBody = getResponseBody(conn);
                }
                conn.disconnect();
            }
            // handle exceptions
            catch (MalformedURLException e) {
                System.out.println("URL Exception thrown" + e);
            } catch (IOException e) {
                System.out.println("IO Exception thrown" + e);
            } catch (Exception e) {
                System.out.println("Exception thrown" + e);
            }
            return responseBody;
        }
        protected String getResponseBody(HttpURLConnection conn) {
            String responseText = "";
            try {
                String output = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                while ((output = br.readLine()) != null) {
                    responseText += output;
                }
            } catch (IOException e) {
                System.out.println("Exception caught " + e);
            }
            return responseText;
        }
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        protected void onPostExecute(String result) {
            m.lookupDisplay(result);
        }
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            String myString = params[0];
            String result = lookup(myString);
            return result;
        }
    }


    private class AsyncSendMessage extends AsyncTask<String, Integer, String>{
        protected String sendMessage(String number, String message) {
            HttpURLConnection conn;
            int status = 0;
            String result="";
            String responseBody = null;
            try {
                URL url = new URL("https://warm-oasis-60714.herokuapp.com/send?number=" + number + "&message=" + message);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                status = conn.getResponseCode();
                result = conn.getResponseMessage();
                if (status == 200) {
                    System.out.println("fvsfv");
                    responseBody = getResponseBody(conn);
                }
                conn.disconnect();
            }
            // handle exceptions
            catch (MalformedURLException e) {
                System.out.println("URL Exception thrown" + e);
            } catch (IOException e) {
                System.out.println("IO Exception thrown" + e);
            } catch (Exception e) {
                System.out.println("IO Exception thrown" + e);
            }
            return responseBody;
        }
        protected String getResponseBody(HttpURLConnection conn) {
            String responseText = "";
            try {
                String output = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                while ((output = br.readLine()) != null) {
                    responseText += output;
                }
            } catch (IOException e) {
                System.out.println("Exception caught " + e);
            }
            return responseText;
        }
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        protected void onPostExecute(String result) {
            m.sendMessageDisplay(result);
        }
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            String num = params[0];
            String message = params[1];
            String result = sendMessage(num, message);
            return result;
        }
    }
}
