package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ResponseHandler {

    Gson gson = new Gson(); //Gson to work with JSONs
    static HttpClient client; //HttpClient to make requests
    static int TIMEOUT = 4; //Duration of timeout

    String login;
    String password;
    static String url;

    public ResponseHandler(){
        Properties prop = new Properties();
        String fileName = "src/data.conf";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
            url = prop.getProperty("URL");
            login = prop.getProperty("LOGIN");
            password = prop.getProperty("PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserToken(){
        String params = "{\"username\": \""+login+"\", \"password\": \""+password+"\"}";
        JsonObject jsonObject = postJsonResponseFromRequest("auth/login", params, null);
        return getFieldFromResponse("userToken", jsonObject).getAsString();
    }
    public static JsonObject getJsonResponseFromRequest(String uri, String userToken) {
        //GET request to API
        HttpGet httpGet = new HttpGet(url+uri);
        client = HttpClients.createDefault();
        try {
            //Send request
            if (userToken != null)
                //Add user token if needed
                httpGet.addHeader("x-client-token", userToken);
            HttpResponse response = client.execute(httpGet);
            //Get response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //Parse response
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                //Make a json object with code and body of request
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                //Close request
                entity.getContent().close();
                //Timeout just in case
                TimeUnit.SECONDS.sleep(TIMEOUT);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static JsonObject postJsonResponseFromRequest(String uri, String params, String userToken) {
        //POST request to API
        HttpPost httpPost = new HttpPost(url+uri);
        client = HttpClients.createDefault();
        try {
            if (params != null) {
                // Set JSON body of request
                httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
                httpPost.setHeader("content-Type","application/json");
            }
            //Send request
            if (userToken != null)
                httpPost.addHeader("x-client-token", userToken);
            //Execute request and get response
            HttpResponse response = client.execute(httpPost);
            //Get body of response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //Parse bytes from response body
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                //Convert response to json object
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                //Close request
                entity.getContent().close();
                //Timeout just in case
                TimeUnit.SECONDS.sleep(TIMEOUT);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static JsonObject patchJsonResponseFromRequest(String uri, String params, String userToken) {
        //POST request to API
        HttpPatch httpPatch = new HttpPatch(url+uri);
        client = HttpClients.createDefault();
        try {
            if (params != null) {
                // Set JSON body of request
                httpPatch.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
                httpPatch.setHeader("content-Type","application/json");
            }
            //Send request
            if (userToken != null)
                httpPatch.addHeader("x-client-token", userToken);
            //Execute request and get response
            HttpResponse response = client.execute(httpPatch);
            //Get body of response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //Parse bytes from response body
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                //Convert response to json object
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                //Close request
                entity.getContent().close();
                //Timeout just in case
                TimeUnit.SECONDS.sleep(TIMEOUT);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public int getCodeFromResponse(JsonObject jsonObject){
        //Get status from response
        return jsonObject.get("statusCode").getAsInt();
    }

    public JsonObject getBodyFromResponse(JsonObject jsonObject){
        //Get body of response
        return gson.fromJson(jsonObject.get("response").getAsString(), JsonObject.class);
    }

    public JsonArray getArrayFromResponse(JsonObject jsonObject){
        //Get array body of response
        return gson.fromJson(jsonObject.get("response").getAsString(), JsonArray.class);
    }

    public JsonElement getFieldFromResponse(String field, JsonObject json){
        //Get field from request response
        return gson.fromJson(json.get("response").getAsString(), JsonObject.class).get(field);
    }

    public String EmployeeToJson(Employee employee){
        //Convert Employee object into JSON
        return gson.toJson(employee);
    }

    public Employee JsonToEmployee(String json){
        //Convert JSON into Employee object
        return gson.fromJson(json, Employee.class);
    }

    public String CompanyToJson(Company company){
        //Convert Company object into JSON
        return gson.toJson(company);
    }

    public Company JsonToCompany(JsonObject json){
        //Convert JSON into Company object
        return gson.fromJson(json, Company.class);
    }
}
