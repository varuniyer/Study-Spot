package varuniyer.com.hackumass.studyspot.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import varuniyer.com.hackumass.studyspot.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.volume);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vol_levels, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFCF00")));
    }

    public void getFilter(View v) {
        String filter = "";
        try {
            double dist = Double.parseDouble(((EditText) findViewById(R.id.dist)).getText().toString());
            for (double i = 0.0; i <= dist && i <= 0.4; i = (double) ((int) ((i + 0.1) * 10)) / 10) {
                if (i == 0.0) filter += "(Distance:0";
                else filter += "Distance:" + i;
                if (i >= dist || i >= 0.4) filter += ") AND ";
                else filter += " OR ";
            }
        }catch(Exception e) {

        }

        Spinner spinner = (Spinner) findViewById(R.id.volume);

        if( !spinner.getSelectedItem().toString().equals("Any") )
            filter += "Volume:" + spinner.getSelectedItem().toString() + " AND ";
        if( ((Switch)findViewById(R.id.solo)).isChecked() )
            filter += "\"Solo Study\":Yes AND ";
        if( ((Switch)findViewById(R.id.group)).isChecked() )
            filter += "\"Group Study\":Yes AND ";
        if( ((Switch)findViewById(R.id.outlets)).isChecked() )
            filter += "Outlets:Yes AND ";
        if( ((Switch)findViewById(R.id.charging)).isChecked() )
            filter += "Charging:Yes AND ";
        if( ((Switch)findViewById(R.id.whiteboard)).isChecked() )
            filter += "Whiteboard:Yes AND ";
        if( ((Switch)findViewById(R.id.sca)).isChecked() )
            filter += "\"Student Computer Access\":Yes AND ";
        if( ((Switch)findViewById(R.id.printer)).isChecked() )
            filter += "Printer:Yes AND ";
        if(filter.length() > 0) filter = filter.substring(0, filter.length() - 5);
        Log.i("Filter", filter);

        StudySpotSearchActivity.filter = filter;

        Intent i = new Intent(this, StudySpotSearchActivity.class);
        startActivity(i);
    }
}
