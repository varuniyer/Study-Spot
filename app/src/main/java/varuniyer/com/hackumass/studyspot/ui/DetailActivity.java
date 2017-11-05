package varuniyer.com.hackumass.studyspot.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import varuniyer.com.hackumass.studyspot.R;
import varuniyer.com.hackumass.studyspot.model.StudySpot;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        StudySpot spot = StudySpotSearchActivity.current;

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(spot.name);

        TextView dist = (TextView)findViewById(R.id.dist);
        dist.setText(spot.dist + " miles");

        TextView quiet = (TextView)findViewById(R.id.volume);
        quiet.setText(spot.volume);

        TextView solo = (TextView)findViewById(R.id.solo);
        solo.setText(spot.solo);

        TextView group = (TextView)findViewById(R.id.group);
        group.setText(spot.group);

        TextView sca = (TextView)findViewById(R.id.sca);
        sca.setText(spot.sca);

        TextView outlets = (TextView)findViewById(R.id.outlets);
        outlets.setText(spot.outlets);

        TextView charging = (TextView)findViewById(R.id.charging);
        charging.setText(spot.charging);

        TextView whiteboard = (TextView)findViewById(R.id.whiteboard);
        whiteboard.setText(spot.whiteboard);

        TextView printer = (TextView)findViewById(R.id.printer);
        printer.setText(spot.printer);
    }

    final double lat = StudySpotSearchActivity.current.lat;
    final double lon = StudySpotSearchActivity.current.lon;
    public void getDirection(View v) {

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
