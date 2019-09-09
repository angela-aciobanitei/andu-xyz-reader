package com.ang.acb.materialme.ui.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ang.acb.materialme.R;


public class MainActivity extends AppCompatActivity {

    private NavigationController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationController = new NavigationController(this);
        navigationController.navigateToArticleList();
    }
}
