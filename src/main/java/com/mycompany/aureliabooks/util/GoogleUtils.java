package com.mycompany.aureliabooks.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mycompany.aureliabooks.model.GooglePojo;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for Google OAuth 2.0 API connection.
 * @author DungLT
 */
public class GoogleUtils {
    public static String CLIENT_ID;
    public static String CLIENT_SECRET;
    
    static {
        java.util.Properties prop = new java.util.Properties();
        try (java.io.InputStream input = GoogleUtils.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (input != null) {
                prop.load(input);
                CLIENT_ID = prop.getProperty("google.client_id");
                CLIENT_SECRET = prop.getProperty("google.client_secret");
            } else {
                System.err.println("GoogleUtils Warning: oauth.properties not found in classpath!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static final String REDIRECT_URI = "http://localhost:8080/AureliaBooks/login-google";
    public static final String GRANT_TYPE = "authorization_code";

    private static final HttpClient httpClient = HttpClient.newBuilder().build();
    private static final Gson gson = new Gson();

    /**
     * Exchanges Authorization Code for Access Token.
     */
    public static String getToken(String code) throws IOException, InterruptedException {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        
        String parameters = "client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&grant_type=" + URLEncoder.encode(GRANT_TYPE, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(parameters))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            return jsonObject.get("access_token").getAsString();
        } else {
            throw new IOException("Failed to get Access Token from Google: " + response.body());
        }
    }

    /**
     * Uses Access Token to fetch Google User Profile from Google UserInfo API.
     */
    public static GooglePojo getUserInfo(String accessToken) throws IOException, InterruptedException {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userInfoUrl))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), GooglePojo.class);
        } else {
            throw new IOException("Failed to get User Info from Google: " + response.body());
        }
    }
}
