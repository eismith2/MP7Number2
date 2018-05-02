package edu.illinois.cs.cs125.anotherattempt;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Main class for our UI design lab.
 */
public final class MainActivity extends AppCompatActivity {

    /**
     * Default logging tag for messages from the main activity.
     */
    private static final String TAG = "anotherattempt:Main";
    /**
     * will hold image base64 string
     */
    public static String imageAsString = "";

    /**
     * Request queue for our API requests.
     */
    private static RequestQueue requestQueue;

    /**
     * Uri used for selected photo.
     */
    public Uri selectedPhotoURI;


    /**
     * will hold API return string parsed results
     */
    public static String birdResults = "";
    /**
     * holds image bitmap to be converted to 64base string
     */
    public static Bitmap image = null;


    /**
     * Run when this activity comes to the foreground.
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
                try {
                    bitmapToBase64(image);
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
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
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

        String output =  new Task1().execute().get();
        System.out.println(output);
        JsonParser parser = new JsonParser();
        if (output == null) {
            Log.d(TAG, "null json return");
        }
        JsonObject object = parser.parse(output).getAsJsonObject();
       // System.out.println(isJSONValid(output));
        JsonArray responses = object.getAsJsonArray("responses");
        JsonObject labelAnnotations = responses.get(0).getAsJsonObject();
       JsonArray first = labelAnnotations.get("labelAnnotations").getAsJsonArray();
       String results = "";

        for (int i = 0; i < first.size(); i++) {
            JsonObject element = first.get(i).getAsJsonObject();
            if (element.get("description").getAsString() != null) {
                if (element.get("score").getAsDouble() >= .75) {
                    System.out.println(element.get("description"));
                    results += (element.get("description") + ", ");
                }
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

    /**
     * used to validate if Json was accurate. not really used in code
     */
    private static final Gson gson = new Gson();

    public static boolean isJSONValid(String jsonInString) {
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;

        }

    }

    private String bitmapToBase64(Bitmap image) {
         if (image == null) {
            Log.d(TAG, "Please download an image of your bird");
             return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        imageAsString =  Base64.encodeToString(byteArray, Base64.DEFAULT);
        // System.out.println(imageAsString);
        return imageAsString;
    }



}