package controllers;

import javafx.scene.control.Label;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Timer;

public class API_Data {
    public static Double progress = 0.0;
    public static Double duration = 0.0;
    public static String clientId = "60a6da4475dc48659a30285bb14bce84";
    public static String clientSecret = "ebd94cd9a50144cd896c3f37a68a09dd";
    public static String accessToken = "";
    public static String refreshToken = "";
    public static String requestTokenURL = "https://accounts.spotify.com/api/token";
    public static String accessURL = "https://accounts.spotify.com/authorize?client_id=" + clientId + "&response_type=code&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback%2F&scope=user-top-read%20user-modify-playback-state%20user-read-currently-playing&state=33fFs29kd09";
    public static String codeToken = "";
    public static String base64idAndSecret = "";
    public static Long refreshTime;
    public static boolean spotifyConnected = false;
    public static boolean timerStarted = false;
    public static Timer timer;
    public static boolean playbackTimerStarted = false;
    public static Thread playbackTimer;
    public static Label loginFailure = new Label();
    public static Label mainScene = new Label();
    public static Label progressOfSong = new Label();


    public static int requestAccessToken(){
        int status = 0;
        try {
            URL url = new URL(requestTokenURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String urlParams = "grant_type=authorization_code&code=" + codeToken + "&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback%2F";
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "application/json");
            String idAndSecret = new String(clientId + ":" + clientSecret);
            base64idAndSecret = Base64.getEncoder().encodeToString(idAndSecret.getBytes());
            con.setRequestProperty("Authorization", "Basic " + base64idAndSecret);
            System.out.println(base64idAndSecret);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParams);
            wr.flush();
            wr.close();
            status = con.getResponseCode();
            System.out.println("Status: " + status + " for requesting access");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            String dataString = content.toString();
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(dataString);
            accessToken = (String) response.get("access_token");
            refreshToken = (String) response.get("refresh_token");
            refreshTime = (Long)response.get("expires_in");
            spotifyConnected = true;
            MyTokenTimer tokenTask = new MyTokenTimer();
            timerStarted = true;
            timer = new Timer("Token Thread");
            timer.schedule(tokenTask, 0, (refreshTime-100)*1000);
            CurrentPlaybackState playbackThread = new CurrentPlaybackState();
            playbackTimerStarted = true;
            playbackTimer =  new Thread(playbackThread);
            playbackTimer.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    public static void refreshToken() {
        try {
            URL url = new URL(requestTokenURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String urlParams = "grant_type=refresh_token&refresh_token=" + refreshToken;
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Basic " + base64idAndSecret);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParams);
            wr.flush();
            wr.close();
            int status = con.getResponseCode();
            System.out.println("Status: " + status + " for new access token");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            String dataString = content.toString();
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(dataString);
            accessToken = (String) response.get("access_token");
            System.out.println("Access token has changed to: " + accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void action(String action) {
        try {
            URL url = new URL("https://api.spotify.com/v1/me/player/" + action);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if (action.equals("next") || action.equals("previous")) {
                con.setRequestMethod("POST");
            } else {
                con.setRequestMethod("PUT");
            }
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.flush();
            wr.close();
            int status = con.getResponseCode();
            System.out.println("Status: " + status + " for " + action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void seek(long pos){
        try {
            System.out.println(pos);
            String position = String.valueOf(pos);
            URL url = new URL("https://api.spotify.com/v1/me/player/seek?position_ms=" + position);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.flush();
            wr.close();
            int status = con.getResponseCode();
            System.out.println("Status: " + status + " for seek");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getPlaybackState() {
        try {
            URL url = new URL("https://api.spotify.com/v1/me/player/currently-playing");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String dataString = content.toString();
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(dataString);
                progress = ((Long) response.get("progress_ms")).doubleValue();
                JSONObject item = (JSONObject) response.get("item");
                duration = ((Long) item.get("duration_ms")).doubleValue();

                double percentage = progress / duration;
                progressOfSong.setText(String.valueOf(percentage));
            } else {
                System.out.println("failed to get playback state with status: " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getUserTopRanked(String type) {
        HashMap<String, String> topRankedMap = new HashMap<String, String>();
        try {
            URL url = new URL("https://api.spotify.com/v1/me/top/" + type);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String dataString = content.toString();
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(dataString);
                JSONArray items = (JSONArray) response.get("items");
                items.forEach(item -> {
                    JSONObject itemObj = (JSONObject) item;
                    String name = (String) itemObj.get("name");
                    String id = (String) itemObj.get("id");
                    if (type.equals("tracks")) {
                        String artistsString = "";
                        JSONArray artists = (JSONArray) itemObj.get("artists");
                        if (artists.size() > 0) {
                            artistsString += " - ";
                            for (int i = 0; i < artists.size(); i++) {
                                JSONObject artistObj = (JSONObject) artists.get(i);
                                String artistName = (String) artistObj.get("name");
                                if (i == 0) {
                                    artistsString += artistName;
                                } else {
                                    artistsString += ", " + artistName;
                                }
                            }
                            name += artistsString;
                        }
                    }
                    topRankedMap.put(id, name);
                });
            } else {
                System.out.println("Status: " + status + " - Could not retrieve user top ranked data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topRankedMap;
    }

    public static HashMap<String, String> getUser() {
        HashMap<String, String> userMap = new HashMap<String, String>();
        try {
            URL url = new URL("https://api.spotify.com/v1/me");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String dataString = content.toString();
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(dataString);
                JSONObject followerObj = (JSONObject) response.get("followers");
                long followers = (Long) followerObj.get("total");
                JSONArray images = (JSONArray) response.get("images");
                String imageUrl = "";
                if (images.size() > 0) {
                    JSONObject image = (JSONObject) images.get(0);
                    imageUrl = (String) image.get("url");
                }
                String name = (String) response.get("display_name");
                userMap.put("name", name);
                userMap.put("url", imageUrl);
                userMap.put("followers", Long.toString(followers));

            } else {
                System.out.println("Status: " + status + " - Could not retrieve user data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userMap;
    }

    public static HashMap<String, String> search(String query, String type) {
        StringBuilder tempQuery = new StringBuilder();
        char[] queryChars = query.toCharArray();
        for (char c : queryChars){
            if (c == ' '){
                tempQuery.append("%20");
            }
            else {
                tempQuery.append(c);
            }
        }
        String newQuery = tempQuery.toString();
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            URL url;
            if (type.equals("artists")) {
                url = new URL("https://api.spotify.com/v1/search?q=" + newQuery +"&type=artist");
            } else if (type.equals("tracks")) {
                url = new URL("https://api.spotify.com/v1/search?q=" + newQuery +"&type=track&market=US");
            } else {
                url = new URL("https://api.spotify.com/v1/search?q=" + newQuery +"&type=playlist");
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            System.out.println("Status: " + status + " for " + type + " search");
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String dataString = content.toString();
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(dataString);
                JSONObject responseObject =  (JSONObject) response.get(type);
                JSONArray items = (JSONArray) responseObject.get("items");
                for (int i = 0; i < items.size(); i++){
                    JSONObject item = (JSONObject) items.get(i);
                    String name = (String) item.get("name");
                    String id = (String) item.get("id");
                    if (type.equals("tracks")) {
                        String artistName = "";
                        JSONArray artists  = (JSONArray) item.get("artists");
                        if (artists.size() > 0) {
                            JSONObject artist = (JSONObject) artists.get(0);
                            artistName = " - " + (String) artist.get("name");
                        }
                        name = name + artistName;
                    }

                    if (type.equals("playlists")) {
                        String ownerName = "";
                        JSONObject owner = (JSONObject) item.get("owner");
                        ownerName = " - " + (String) owner.get("display_name");
                        name = name + ownerName;
                    }

                    map.put(id, name);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<String, String> getData(String id, String type){
        HashMap<String, String> dataMap = new HashMap<String, String>();
        try {
            URL url;
            if (type.equals("artist")) {
                url = new URL("https://api.spotify.com/v1/artists/" + id);
            } else {
                url = new URL("https://api.spotify.com/v1/playlists/" + id);
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String dataString = content.toString();
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(dataString);
                JSONObject followerObj = (JSONObject) response.get("followers");
                long followers = (Long) followerObj.get("total");
                String name = (String ) response.get("name");
                dataMap.put("name", name);
                dataMap.put("followers", Long.toString(followers));
            } else {
                System.out.println("Status: " + status + " - Could not retrieve " + type + " data for id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    public static ArrayList<String> getIds(String id, String type){
        ArrayList<String> ids = new ArrayList<String>();
        URL url;
        try {
            if (type.equals("album")) {
                url = new URL("https://api.spotify.com/v1/artists/" + id + "/albums");
            } else if (type.equals("track")) {
                url = new URL("https://api.spotify.com/v1/albums/" + id + "/tracks");
            } else {
                url = new URL("https://api.spotify.com/v1/playlists/" + id + "/tracks");
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String dataString = content.toString();
                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(dataString);
                JSONArray items = (JSONArray) response.get("items");
                items.forEach((item) -> {
                    JSONObject itemObj = (JSONObject) item;
                    if (type.equals("playlist")) {
                        itemObj = (JSONObject) itemObj.get("track");
                    }
                    ids.add((String) itemObj.get("id"));
                });

            } else {
                System.out.println("Status: " + status + " - Could not retrieve " + type + " id for " + id);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return ids;
    }

    public static HashMap<String, Double> getAudioFeatures(ArrayList<String> ids) {
        HashMap<String, Double> averageOfFeatures = new HashMap<String, Double>();
        averageOfFeatures.put("energy", 0.0);
        averageOfFeatures.put("danceability", 0.0);
        averageOfFeatures.put("loudness", 0.0);
        averageOfFeatures.put("instrumentalness", 0.0);
        averageOfFeatures.put("valence", 0.0);
        averageOfFeatures.put("tempo", 0.0);

        ArrayList<ArrayList<String>> batches = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < ids.size(); i++) {
            if (i % 100 == 0) {
                batches.add(new ArrayList<String>());
            }
            batches.get(i / 100).add(ids.get(i));
        }
        batches.forEach((batch) -> {
            String idsString = String.join(",", batch);
            try {
                URL url = new URL("https://api.spotify.com/v1/audio-features/?ids=" + idsString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Bearer " + accessToken);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                int status = con.getResponseCode();
                if (status == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    String dataString = content.toString();
                    JSONParser parser = new JSONParser();
                    JSONObject response = (JSONObject) parser.parse(dataString);
                    JSONArray audioFeatures = (JSONArray) response.get("audio_features");
                    audioFeatures.forEach((feature) -> {
                        JSONObject featureObj = (JSONObject) feature;
                        double danceability = ((Number) featureObj.get("danceability")).doubleValue();
                        double energy = ((Number) featureObj.get("energy")).doubleValue();
                        double loudness = ((Number) featureObj.get("loudness")).doubleValue();
                        double instrumentalness = ((Number) featureObj.get("instrumentalness")).doubleValue();
                        double valence = ((Number) featureObj.get("valence")).doubleValue();
                        double tempo = ((Number) featureObj.get("tempo")).doubleValue();
                        averageOfFeatures.put("energy", averageOfFeatures.get("energy") + energy);
                        averageOfFeatures.put("danceability", averageOfFeatures.get("danceability") + danceability);
                        averageOfFeatures.put("loudness", averageOfFeatures.get("loudness") + loudness);
                        averageOfFeatures.put("instrumentalness", averageOfFeatures.get("instrumentalness") + instrumentalness);
                        averageOfFeatures.put("valence", averageOfFeatures.get("valence") + valence);
                        averageOfFeatures.put("tempo", averageOfFeatures.get("tempo") + tempo);
                    });
                } else {
                    System.out.println("Status: " + status + " - Could not retrieve track id's: " + idsString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        averageOfFeatures.put("energy", averageOfFeatures.get("energy") / ids.size());
        averageOfFeatures.put("danceability", averageOfFeatures.get("danceability") / ids.size());
        averageOfFeatures.put("loudness", averageOfFeatures.get("loudness") / ids.size());
        averageOfFeatures.put("instrumentalness", averageOfFeatures.get("instrumentalness") / ids.size());
        averageOfFeatures.put("valence", averageOfFeatures.get("valence") / ids.size());
        averageOfFeatures.put("tempo", averageOfFeatures.get("tempo") / ids.size());

        return averageOfFeatures;
    }

    public static void getTracks(ArrayList<String> ids) {
        ArrayList<ArrayList<String>> batches = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < ids.size(); i++) {
            if (i % 50 == 0) {
                batches.add(new ArrayList<String>());
            }
            batches.get(i / 50).add(ids.get(i));
        }
        batches.forEach((batch) -> {
            System.out.println("new batch with size " + batch.size());
            String idsString = String.join(",", batch);
            try {
                URL url = new URL("https://api.spotify.com/v1/tracks/?ids=" + idsString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Bearer " + accessToken);
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                int status = con.getResponseCode();
                if (status == 200){
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    String dataString = content.toString();
                    JSONParser parser = new JSONParser();
                    JSONObject response = (JSONObject) parser.parse(dataString);
                    JSONArray tracks = (JSONArray) response.get("tracks");
                    tracks.forEach((track) -> {
                        JSONObject trackObj = (JSONObject) track;
                        JSONArray artistsList = (JSONArray) trackObj.get("artists");
                        ArrayList<String> artistNames = new ArrayList<String>();
                        artistsList.forEach((artist) -> {
                            JSONObject artistObj = (JSONObject) artist;
                            artistNames.add((String) artistObj.get("name"));
                        });
                        String artistDisplayString = String.join(", ", artistNames);
                        String name = (String) trackObj.get("name");
                        System.out.println(artistDisplayString + " - " + name);
                    });
                } else {
                    System.out.println("Status: " + status + " - Could not retrieve track id's: " + idsString);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

    }




}
