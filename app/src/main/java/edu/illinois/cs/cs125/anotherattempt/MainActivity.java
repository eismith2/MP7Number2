package edu.illinois.cs.cs125.anotherattempt;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.lang.annotation.Target;
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
//import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.FileNotFoundException;
import java.io.InputStream;

import edu.illinois.cs.cs125.anotherattempt.Main2Activity;
import edu.illinois.cs.cs125.anotherattempt.R;

import static com.google.cloud.vision.v1.ImageAnnotatorClient.*;


/**
 * Main class for our UI design lab.
 */
public final class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "MP7:Main";

    /** Request queue for our API requests. */
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
                startAPICall();
                Log.d(TAG, "startAPICall Finished");
               // if ()
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
     *
     */
    void startAPICall() {

        try {
            Log.d(TAG, "startAPICall has started at try");
            ImageAnnotatorClient vision = create();
            String fileName = getPath(selectedPhotoURI);
            byte[] data;
            ByteString imgBytes = null;

            if (Build.VERSION.SDK_INT >= 26) {
                Path path = Paths.get(fileName);
                data = Files.readAllBytes(path);
                imgBytes = ByteString.copyFrom(data);
            } else {
                Log.d(TAG, "SDK VERSION IS BELOW 26");
            }

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                //  for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
               //      if (Build.VERSION.SDK_INT > 26 ) {
               //           annotation.getAllFields().forEach((k, v);
               //      }
               //  }

            }
        } catch (Exception e) {
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
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            imageViewer.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_LONG).show();
        }



    }
}
