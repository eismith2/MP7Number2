package edu.illinois.cs.cs125.anotherattempt;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.BuildConfig;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.gson.JsonArray;
import com.google.protobuf.ByteString;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.illinois.cs.cs125.anotherattempt.Main2Activity;
import edu.illinois.cs.cs125.anotherattempt.R;

import static android.content.ContentValues.TAG;
import static com.google.cloud.vision.v1.ImageAnnotatorClient.*;


/**
 * Main class for our UI design lab.
 */
public final class MainActivity extends AppCompatActivity {
    /**
     * Default logging tag for messages from the main activity.
     */
    private static final String TAG = "anotherattempt:Main";

    /**
     * Request queue for our API requests.
     */
    private static RequestQueue requestQueue;
    /**
     * Uri used for selected photo.
     */
    public Uri selectedPhotoURI;

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the queue for our API requests
        //had 85 blocked out below
        //
        //
        // requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        final ImageButton openImage = findViewById(R.id.openFile);
        openImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Open image button");
                startOpenImage();
            }
        });

        final Button startAPICall = findViewById(R.id.getBirdInfo);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                try {
                    startAPICall();
                } catch (IOException e) {
                    Log.d(TAG, "StartAPI call failed");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "startAPICall Finished");
                // if ()
                //had 105 blocked out
                // String json = requestQueue.getCache().toString();
                System.out.println("IS IT WORKING BUBBLES");
                //System.out.println(json);
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);

                //JsonParser parser = new JsonParser();
                //JsonObject object = parser.parse(json).getAsJsonObject();

            }
        });
    }

    /**
     * Run when this activity is no longer visible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Make a call to the API.
     */
    void startAPICall() throws IOException, ExecutionException, InterruptedException {
        String output =  new Task1().execute().get();
        System.out.println(output);
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(output).getAsJsonObject();

//labelAnnotations
        JsonArray responses = object.getAsJsonArray("responses");
        System.out.println(responses);
        //JsonArray labelAnnotations = responses.get(0).getAsJsonArray();
        JsonObject labelAnnotations = responses.get(0).getAsJsonObject();
        System.out.println(labelAnnotations);
        //JsonObject description1 = labelAnnotations.get(0).getAsJsonObject();
       // System.out.println(description1);
    //    for (int i = 0; i < labelAnnotations.size(); i++) {
    //        System.out.println(labelAnnotations.get(i));
    //    }
        String toSearch =output;
        String keyword = "description";
        int index = toSearch.indexOf(keyword);
        while (index >= 0) {
            //System.out.println("INDEX " + index);
           // System.out.println()
            index = toSearch.indexOf(keyword, index+keyword.length());
        }


        // AsyncTask.execute(new Runnable() {
        //   @Override
        //   public void run() {
        /**

        System.out.println("IN doInBackground");
        final String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
        final String API_KEY = "key=AIzaSyCNV4ogtni3RkEiqKA9ZEMnZ885EAAkcN8";
        URL serverUrl = null;
        try {
            Log.d(TAG, "URL server in try");
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
            Log.d(TAG, "stream writer in try before");
            httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()));
            Log.d(TAG, "stream writer in try");
        } catch (IOException e) {
            Log.d(TAG, "output stream writer try/catch");
            e.printStackTrace();
        }
        try {
            httpRequestBodyWriter.write
                    ("{\"requests\":  [{ \"features\":  [ {\"type\": \"LABEL_DETECTION\""
                            + "}], \"image\": {\"source\": { \"imageUri\":"
                            + " \"http://4.bp.blogspot.com/-d_OpcNaNZhI/TuO87-cjUQI/AAAAAAAAAnY/HaBjzl-Z88Q/s1600/DSC_0344b.jpg\"}}}]}");
            Log.d(TAG, "requestbodywriter in try");
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
                return;

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
        if (httpResponseScanner != null) {
            while (httpResponseScanner.hasNext()) {
                String line = httpResponseScanner.nextLine();
                resp += line;
                System.out.println("TEST");
                System.out.println(line);  //  alternatively, print the line of response
            }
        } else {
            System.out.println("Scanner  is null ");
        }
        if (httpResponseScanner != null) {
            httpResponseScanner.close();
        }
        //     }
        //  });
        Log.d(TAG, "END OF START API BIRD");
        return;
    }


**/

    }


        /**
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    TARGET_URL
                            + API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                Log.d(TAG, response.toString(2));
                            } catch (JSONException ignored) { }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
/**
        try {
            Log.d(TAG, "startAPICall has started at try");
            ImageAnnotatorClient vision = create();
            String fileName = getPath(selectedPhotoURI);
            byte[] data;
            ByteString imgBytes = null;
            Log.d(TAG, "At first sdk check = 26");
            if (Build.VERSION.SDK_INT >= 26) {
                Path path = Paths.get(fileName);
                data = Files.readAllBytes(path);
                imgBytes = ByteString.copyFrom(data);
            } else {
                Log.d(TAG, "SDK VERSION IS BELOW 26");
            }
            Log.d(TAG, "At annotateimagerequest check");
            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);
            Log.d(TAG, "At label detection thing");
            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }
                Log.d(TAG, "Annotations about to start");
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    Log.d(TAG, "In annotations for loop");
                    System.out.println("ANNOTATIONS");
                    System.out.println(annotation.getAllFields());
                   // annotation.getAllFields().forEach((k, v) ->
                     //       System.out.printf("%s : %s\n", k, v.toString()));
                }

            }
           // requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d(TAG, "Exception e");
            e.printStackTrace();
        }

        /**
         try {
         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
         Request.Method.POST,
         "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyCNV4ogtni3RkEiqKA9ZEMnZ885EAAkcN8"
         ,
         null,
         new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(final JSONObject response) {
        try {
        Log.d(TAG, response.toString(2));
        } catch (JSONException ignored) { }
        }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(final VolleyError error) {
        Log.e(TAG, error.toString());
        }
        });
         requestQueue.add(jsonObjectRequest);
         } catch (Exception e) {
         e.printStackTrace();
         }
         **/

    void startOpenImage() {
        if(Build.VERSION.SDK_INT < 19) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 42);
    }
    public void onActivityResult(final int requestCode, final int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            selectedPhotoURI = resultData.getData();
            loadSelectedPhoto(selectedPhotoURI);
        }
    }
    public String getPath(Uri currentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(currentUri, proj,null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public void loadSelectedPhoto (final Uri selectedPhotoUri) {
        if (selectedPhotoUri == null) {
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedPhotoUri);
            final ImageView imageViewer = findViewById(R.id.imageView);
            int targetWidth = imageViewer.getWidth();
            int targetHeight = imageViewer.getHeight();
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            imageViewer.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_LONG).show();
        }



    }
}
