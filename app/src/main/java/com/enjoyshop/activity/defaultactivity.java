package com.enjoyshop.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enjoyshop.R;

public class defaultactivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defaultlayout);
    }
}
