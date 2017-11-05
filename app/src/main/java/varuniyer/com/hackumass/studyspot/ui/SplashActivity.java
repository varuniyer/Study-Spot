package varuniyer.com.hackumass.studyspot.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import varuniyer.com.hackumass.studyspot.R;

public class SplashActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Start home activity

        startActivity(new Intent(SplashActivity.this, MainActivity.class));

        // close splash activity

        finish();

    }

}
