package com.tiny.album;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_PHOTO_CODE = 200;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        AlbumActivity.launch(this,REQUEST_PHOTO_CODE,true,5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK&&requestCode == REQUEST_PHOTO_CODE){
            ArrayList<String> paths = data.getStringArrayListExtra(AlbumActivity.REQUEST_DATA);
            if (paths!=null){
                mTextView.setText(paths.toString());
            }
        }
    }
}
