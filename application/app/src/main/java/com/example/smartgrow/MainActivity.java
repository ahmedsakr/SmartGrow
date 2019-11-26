package com.example.smartgrow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 * MainActivity is the entry activity when the user opens
 * the SmartGrow android application.
 *
 * Currently, the Activity shows the real-time sensor information
 * retrieved from the server.
 *
 * @author Ahmed Sakr
 * @since November 23, 2019
 */
public class MainActivity extends AppCompatActivity {

    private RealtimeSensors realtimeSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.realtimeSensors = new RealtimeSensors(this);
    }

    /**
     * When destroying the activity, we must stop the thread that
     * is updating the sensor information.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // End the thread for retrieving the sensor information from the server.
        this.realtimeSensors.interrupt();
    }
}
