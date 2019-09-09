package com.ang.acb.materialme.ui.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ang.acb.materialme.R;

/**
 * An activity that inflates a layout that has a NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
