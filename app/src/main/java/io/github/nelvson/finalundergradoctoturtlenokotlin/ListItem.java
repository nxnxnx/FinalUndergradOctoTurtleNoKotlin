package io.github.nelvson.finalundergradoctoturtlenokotlin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ListItem extends Activity {

@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitem);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        // Here we turn your string.xml in an array
 //       String[] myKeys = getResources().getStringArray(R.array.sections);

 //       TextView myTextView = (TextView) findViewById(R.id.my_textview);
 //       myTextView.setText(myKeys[position]);
    }
}
