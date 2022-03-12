package ds;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Project4Model {

    // Authorization tokens from Twilio
    public static final String ACCOUNT_SID = "<sample-id>";
    public static final String AUTH_TOKEN = "<sample-auth-key>";

    // calls the Twilio Lookup API to get the caller and carrier details (GET request)
    public String lookUp(String number) throws UnsupportedEncodingException {

        number = URLEncoder.encode(number, "UTF-8");
        String lookupURL =
                "https://lookups.twilio.com/v1/PhoneNumbers/"
                        + number
                        +"?Type=caller-name&Type=carrier";
        HttpURLConnection conn;
        int status = 0;
        String responseText = "";
        String responseBody = "";
        try {
            URL url = new URL(lookupURL);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String userpass = ACCOUNT_SID + ":" + AUTH_TOKEN;
            String basicAuth = "Basic :" + new String(Base64.getEncoder().encode(userpass.getBytes()));
            conn.setRequestProperty ("Authorization", basicAuth); // setting basic authorization
            status = conn.getResponseCode();
            responseText = conn.getResponseMessage();

            if (status == 200) {
                responseBody = getResponseBody(conn);
            }

            conn.disconnect();

        }
        catch (MalformedURLException e) {
            System.out.println("URL Exception thrown" + e);
        } catch (IOException e) {
            System.out.println("IO Exception thrown" + e);
        } catch (Exception e) {
            System.out.println("IO Exception thrown" + e);
        }

        return responseBody;
    }

    // calls the Twilio SMS API to send a message to the specified number (POST request)
    public String sendMessage(String number, String message) {
        String sendMessageURL =
                "https://api.twilio.com/2010-04-01/Accounts/"
                        + ACCOUNT_SID
                        +"/Messages.json";

        String urlParameters  = "To=" + number + "&MessagingServiceSid=<message-service-id>&Body=" + message;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        String responseBody = "";
        try {
            URL url = new URL(sendMessageURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // we are sending data with this post request
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String userpass = ACCOUNT_SID + ":" + AUTH_TOKEN;
            String basicAuth = "Basic :" + new String(Base64.getEncoder().encode(userpass.getBytes()));
            conn.setRequestProperty("Authorization", basicAuth); // basic authorization
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            } catch (Exception e) {
                System.out.println("Exception thrown" + e);
            }
            int status = conn.getResponseCode();

            responseBody = getResponseBody(conn);
            conn.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("URL Exception thrown" + e);
        } catch (IOException e) {
            System.out.println("IO Exception thrown" + e);
        } catch (Exception e) {
            System.out.println("Exception thrown" + e);
        }
        return responseBody;
    }

    // Gather up a response body from the connection
    public static String getResponseBody(HttpURLConnection conn) {
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
}
