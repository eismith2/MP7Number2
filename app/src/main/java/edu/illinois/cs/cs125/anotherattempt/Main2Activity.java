package edu.illinois.cs.cs125.anotherattempt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import edu.illinois.cs.cs125.anotherattempt.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final TextView birdwords = findViewById(R.id.nameResult);
        birdwords.setText(MainActivity.birdResults);
    }
}
