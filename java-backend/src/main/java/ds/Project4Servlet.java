package ds;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.*;
import com.mongodb.client.model.Indexes;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "Project4Servlet",
        urlPatterns = {"/","/lookup","/send"})
public class Project4Servlet extends HttpServlet {
    Project4Model p = null;
    MongoClient mongoClient = null;
    @Override
    public void init() {
        p = new Project4Model();
        mongoClient = initializeDb();
    }

    public JSONObject getLogs(String type) {
        // function to retrieve raw logs
        JSONObject data = new JSONObject();
        MongoDatabase database = mongoClient.getDatabase("dashboard");
        MongoCollection<Document> sampleCollection = database.getCollection("data");
        FindIterable<Document> iterDoc = sampleCollection.find(eq("type", type));
        JSONArray jsonArray = new JSONArray();
        Iterator it = iterDoc.iterator();
        while (it.hasNext()) {
            jsonArray.add(it.next());
        }
        data.put(type, jsonArray);
        return data;
    }

    //Operational analytics 1
    public JSONObject getTypeCount() {
        // retrieve lookup and send message count
        JSONObject count = new JSONObject();
        MongoDatabase database = mongoClient.getDatabase("dashboard");
        MongoCollection<Document> sampleCollection = database.getCollection("data");
        long lookupCount = sampleCollection.countDocuments(eq("type","lookup"));
        long sendMessageCount = sampleCollection.countDocuments(eq("type","send_message"));
        count.put("lookup",lookupCount);
        count.put("send_message",sendMessageCount);
        return  count;
    }
    // Operational analytics 2
    public JSONObject phoneNumbers() {
        // get the phone numbers and the number of messages sent to that phone number
        MongoDatabase database = mongoClient.getDatabase("dashboard");
        MongoCollection<Document> sampleCollection = database.getCollection("data");
        Bson match = Aggregates.match(eq("type", "send_message"));
        Bson group = Aggregates.group("$to", Accumulators.sum("count", 1));
        Bson sort = Aggregates.sort(Indexes.descending("count"));
        List<Document> results = sampleCollection.aggregate(Arrays.asList(match, group, sort))
                .into(new ArrayList<>());
        String output = JSONArray.toJSONString(results);
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) parser.parse(output);
            json.put("max", jsonArray);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Operational analytics 3
    public JSONObject countries() {
        // gets the country code and the count of numbers looked up in that country
        JSONObject countries = new JSONObject();
        MongoDatabase database = mongoClient.getDatabase("dashboard");
        MongoCollection<Document> sampleCollection = database.getCollection("data");
        Bson match = Aggregates.match(eq("type", "lookup"));
        Bson group = Aggregates.group("$country_code", Accumulators.sum("count", 1));
        List<Document> results = sampleCollection.aggregate(Arrays.asList(match, group))
                .into(new ArrayList<>());
        String output = JSONArray.toJSONString(results);
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) parser.parse(output);
            json.put("countryData", jsonArray);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json;
    }

    // connects to mongoDB atlas
    public MongoClient initializeDb() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://<username>:<password>@cluster0.mbjif.mongodb.net/test?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if(request.getServletPath().equals("/")){
            // view and attributes for the web dashboard are defined in here
            JSONObject count = new JSONObject();
            count = getTypeCount();
            request.setAttribute("lookupCount",count.get("lookup"));
            request.setAttribute("sendMessageCount",count.get("send_message"));
            request.setAttribute("countryData",countries().get("countryData"));
            request.setAttribute("max",phoneNumbers().get("max"));
            request.setAttribute("lookupLog", getLogs("lookup").get("lookup"));
            request.setAttribute("sendMessageLog", getLogs("send_message").get("send_message"));

            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);
        }

        String userAgent = request.getHeader("User-Agent");

        String number = request.getParameter("number");
        if(number != null) {
            String result = p.lookUp(number);
            // to convert the string response to an object
            JSONParser parser = new JSONParser();
            JSONObject lookupDetails = new JSONObject();
            JSONObject responseData = new JSONObject();
            try {
                if (!result.equals("")) {
                    lookupDetails = (JSONObject) parser.parse(result);
                    // gets the specific details required from the API (caller name,caller type,
                    // carrier name, carrier type and country code)
                    if (!lookupDetails.isEmpty()) {
                        if (lookupDetails.get("caller_name") != null) {
                            JSONObject caller = (JSONObject) lookupDetails.get("caller_name");
                            responseData.put("caller_name", caller.get("caller_name"));
                            responseData.put("caller_type", caller.get("caller_type"));
                        } else {
                            responseData.put("caller_name", "unknown");
                            responseData.put("caller_type", "unknown");
                        }
                        if (lookupDetails.get("carrier") != null) {
                            JSONObject carrier = (JSONObject) lookupDetails.get("carrier");
                            responseData.put("carrier_name", carrier.get("name"));
                            responseData.put("carrier_type", carrier.get("type"));
                        } else {
                            responseData.put("carrier_name", "unknown");
                            responseData.put("carrier_type", "unknown");
                        }
                        if (lookupDetails.get("country_code") != null) {
                            responseData.put("country_code", lookupDetails.get("country_code"));
                        } else {
                            responseData.put("country_code", "unknown");
                        }
                    }
                } else {
                    responseData.put("error_message", "Not valid input");
                }
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println(responseData);
                out.flush();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // passes all the data from the response into the mongoDB
            // also adds a type key specific to lookup
            if(!lookupDetails.isEmpty()) {
                lookupDetails.put("type", "lookup");
                lookupDetails.put("agent", userAgent);
                MongoDatabase database = mongoClient.getDatabase("dashboard");
                MongoCollection<Document> sampleCollection = database.getCollection("data");
                Document text = new Document(lookupDetails);
                sampleCollection.insertOne(text);
            }
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String userAgent = request.getHeader("User-Agent");

        String number = request.getParameter("number");
        String message = request.getParameter("message");

        if(number != null && message != null) {
            String result = p.sendMessage(number, message);
            // to convert the string response to an object
            JSONParser parser = new JSONParser();
            JSONObject messageDetails = new JSONObject();
            JSONObject responseData = new JSONObject();
            try {
                if (!result.equals("")) {
                    messageDetails = (JSONObject) parser.parse(result);
                    // gets the date and the status of the message sent from the API
                    if (!messageDetails.isEmpty()) {
                        if (messageDetails.get("error_message") != null) {
                            responseData.put("error_message", messageDetails.get("error_message"));
                        } else {
                            if (messageDetails.get("date_created") != null) {
                                responseData.put("date_created", messageDetails.get("date_created"));
                            } else {
                                responseData.put("date_created", "unknown");
                            }
                            if (messageDetails.get("status") != null) {
                                responseData.put("status", messageDetails.get("status"));
                            } else {
                                responseData.put("status", "unknown");
                            }
                        }
                    }
                } else {
                    responseData.put("error_message", "Not valid input");
                }
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println(responseData);
                out.flush();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // passes all the data from the response into the mongoDB
            // also adds a type key specific to send message
            if(!messageDetails.isEmpty()) {
                messageDetails.put("type", "send_message");
                messageDetails.put("agent", userAgent);
                MongoDatabase database = mongoClient.getDatabase("dashboard");
                MongoCollection<Document> sampleCollection = database.getCollection("data");
                Document text = new Document(messageDetails);
                sampleCollection.insertOne(text);
            }
        }
    }
}
