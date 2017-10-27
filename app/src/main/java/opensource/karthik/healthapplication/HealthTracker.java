package opensource.karthik.healthapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.concurrent.TimeUnit;

public class HealthTracker extends AppCompatActivity implements SensorEventListener {

    private ToggleButton trackerBtn;
    private Button calcBMI;
    private TextView stepCounter, totalStep, bmiResult;
    private EditText hieght, weight;

    private SensorManager sensorManager;
    private int stepCountBefore, stepCountAfter, totalStepCount=0;
    long timeStart, timeStop;

    boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tracker);
        hieght = (EditText) findViewById(R.id.heightInput);
        weight = (EditText) findViewById(R.id.weightInput);
        calcBMI = (Button) findViewById(R.id.bmiCalc);
        bmiResult = (TextView) findViewById(R.id.bmiCalcResultStatus);
        stepCounter = (TextView) findViewById(R.id.stepCounterResultStatus);
        totalStep = (TextView) findViewById(R.id.totalStepStatus);
        trackerBtn = (ToggleButton) findViewById(R.id.trackerButton);

        calcBMI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calculateBMI();
            }
        });

        trackerBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                   startTracker();
                } else {
                    stopTracker();
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void calculateBMI(){
        double h = Double.parseDouble(hieght.getText().toString())/100;
        double w = Double.parseDouble(weight.getText().toString());
        double bmi = w/(h*h);
        bmiResult.setText("Your BMI score: "+String.format("%.2f", bmi));
        bmiResult.setVisibility(View.VISIBLE);
    }

    public void startTracker(){
        if(!activityRunning) {
            activityRunning = true;
            stepCountBefore = totalStepCount;
            timeStart = System.currentTimeMillis();
        }
    }

    public void stopTracker(){
        if(activityRunning) {
            stepCountAfter = totalStepCount;
            timeStop = System.currentTimeMillis();
            calculateSteps();
            activityRunning = false;
        }
    }

    private void calculateSteps() {
        int tdiff = (int) TimeUnit.MILLISECONDS.toSeconds(timeStop-timeStart);
        String s = "You have taken "+(stepCountAfter-stepCountBefore)+" steps in "+tdiff+" sec";
        stepCounter.setText(s);
        totalStep.setText("Steps taken Today: "+totalStepCount);
        stepCounter.setVisibility(View.VISIBLE);
        totalStep.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        totalStepCount = (int) event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
