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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.gson.JsonElement;
import com.google.protobuf.ByteString;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

    public static String birdResults = "";

    public Bitmap image = null;

    public static String imageAsString = "";

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                //put in try/catch for NullPointerException??
                bitmapToBase64(image);
                try {
                   birdResults = startAPICall();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "On Restart .....");
    }

    /**
     * Make a call to the API.
     */
    String startAPICall() throws IOException, ExecutionException, InterruptedException {
        System.out.println("hey");
        String output =  new Task1().execute().get();
        System.out.println(output);
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(output).getAsJsonObject();
          System.out.println(isJSONValid(output));
//labelAnnotations
        JsonArray responses = object.getAsJsonArray("responses");
        //System.out.println(responses);
        //JsonArray labelAnnotations = responses.get(0)
        JsonObject labelAnnotations = responses.get(0).getAsJsonObject();
       // System.out.println(labelAnnotations);
        JsonArray first = labelAnnotations.get("labelAnnotations").getAsJsonArray();
       // Jsbject labelAnnotations = responses.get(0).getAsJsonObject();
        System.out.println("FIRST " + first);
        String results = "";
       for (int i = 0; i < first.size(); i++) {
            JsonObject element = first.get(i).getAsJsonObject();
            if (element.get("description").getAsString() != null) {
                    System.out.println(element.get("description"));
                    results += (element.get("description") + ", ");
         }
       }
       return results;
           }



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
            image = BitmapFactory.decodeStream(inputStream);
            imageViewer.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_LONG).show();
        }

    }
    private String bitmapToBase64(Bitmap image) {
        System.out.println("hi");
        if (image == null) {
            Log.d(TAG, "Please download an image of your bird");
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        imageAsString =  Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageAsString;
    }
    //public static String encodeToString() {
    //    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
    //    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    //}
    private static final Gson gson = new Gson();

    public static boolean isJSONValid(String jsonInString) {
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


}
