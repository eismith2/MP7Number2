package edu.illinois.cs.cs125.anotherattempt;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

public class Task1 extends AsyncTask<String, Void, String>  {

     protected String doInBackground(String... params) {

         System.out.println("IN doInBackground");
        final String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
        final String API_KEY = "key=AIzaSyCNV4ogtni3RkEiqKA9ZEMnZ885EAAkcN8";
        URL serverUrl = null;
        try {
            Log.d(TAG,"URL server in try");
            serverUrl = new URL(TARGET_URL + API_KEY);
        } catch (MalformedURLException e) {
            Log.d(TAG, "serverURL try/catch");
            e.printStackTrace();
        }
        URLConnection urlConnection = null;
        try {
            Log.d(TAG, "URL connection in try");
            urlConnection = serverUrl.openConnection();
            Log.d(TAG, "URL connection after openconnection");
        } catch (IOException e) {
            Log.d(TAG, "urlconnection try/catch");
            e.printStackTrace();
        }
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
        try {
            httpConnection.setRequestMethod("POST");
            Log.d(TAG, "htttpConnection in try");
        } catch (ProtocolException e) {
            Log.d(TAG, "setrequest method in try/catch");
            e.printStackTrace();
        }
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setDoOutput(true);
        BufferedWriter httpRequestBodyWriter = null;
        try {
            Log.d(TAG, "request in try before");
            httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()));
            Log.d(TAG, "stream writer in try");
        } catch (IOException e) {
            Log.d(TAG, "outputstreamwriter try/catch");
            e.printStackTrace();
        }
        //https://www.nationalgeographic.com/content/dam/animals/thumbs/rights-exempt/birds/b/bald-eagle_thumb.JPG
         //https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png
        try {
            httpRequestBodyWriter.write
                    ("{\"requests\":  [{ \"features\":  [ {\"type\": \"LABEL_DETECTION\""
                            +"}], \"image\": {\"source\": { \"imageUri\":"
                            +" \"https://www.nationalgeographic.com/content/dam/animals/thumbs/rights-exempt/birds/b/bald-eagle_thumb.JPG\"}}}]}");


        } catch (IOException e) {
            Log.d(TAG, "body writer try/catch");
            e.printStackTrace();
        }
        try {
            httpRequestBodyWriter.close();
        } catch (IOException e) {
            Log.d(TAG, "body writer close try/catch");
            e.printStackTrace();
        }
        try {
            String response = httpConnection.getResponseMessage();
        } catch (IOException e) {
            Log.d(TAG, "responce message try/catch");
            e.printStackTrace();
        }
        try {
            if (httpConnection.getInputStream() == null) {
                System.out.println("No stream");
                return "NO STREAM ERROR";

            }
        } catch (IOException e) {
            Log.d(TAG, "input stream try/catch");
            e.printStackTrace();
        }

        Scanner httpResponseScanner = null;
        try {
            httpResponseScanner = new Scanner(httpConnection.getInputStream());
        } catch (IOException e) {
            Log.d(TAG, "responce scanner new try/catch");
            e.printStackTrace();
        }
        String resp = "";
         String toReturn = "";
        if (httpResponseScanner != null) {

            while (httpResponseScanner.hasNext()) {
                String line = httpResponseScanner.nextLine();
                resp += line;
                //System.out.println("TEST");
               // System.out.println(line);  //  alternatively, print the line of response
                toReturn += line;

            }
            System.out.println("*************JSON FOUND SUCCESSFULL***************************");
            System.out.println("");
            System.out.println("**************************************************");
        } else {
            System.out.println("Scanner  is null ");
        }
        if (httpResponseScanner != null) {
            httpResponseScanner.close();
        }
        //     }
        //  });
        Log.d(TAG, "END OF START API BIRD");
        return toReturn;
    }
}

