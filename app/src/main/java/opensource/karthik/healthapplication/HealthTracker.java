package opensource.karthik.healthapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HealthTracker extends AppCompatActivity implements SensorEventListener {

    private ToggleButton trackerBtn;
    private Button calcBMI;
    private TextView stepCounter, totalStep, bmiResult;
    private EditText hieght, weight;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference profiles, thisUser, healthData, BMI, steps, healthStat;

    private SensorManager sensorManager;
    private int stepCountBefore, stepCountAfter, totalStepCount=0;
    private long timeStart, timeStop;
    private DateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat dateOnly = new SimpleDateFormat("yyyy/MM/dd");
    private DateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");

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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        profiles = database.getReference("profiles");
        thisUser = profiles.child(currentUser.getUid());
        healthData = thisUser.child("healthData");



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

        updateBMI((h*100)+" cm",w+" kg",String.format("%.2f", bmi));
    }

    private void updateBMI(String height, String weight, String bmi) {
        BMI = healthData.child("BMIs");
        healthStat = BMI.child("healthStat "+datetime.format(new Date()));
        DatabaseReference date = healthStat.child("date");
        date.setValue(datetime.format(new Date())+"");
        DatabaseReference value = healthStat.child("bmi");
        value.setValue(bmi);
        DatabaseReference h = healthStat.child("height");
        h.setValue(height);
        DatabaseReference w = healthStat.child("weight");
        w.setValue(weight);
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
        int stepDiff = (stepCountAfter-stepCountBefore);
        String s = "You have taken "+stepDiff+" steps in "+tdiff+" sec";
        stepCounter.setText(s);
        totalStep.setText("Steps taken Today: "+totalStepCount);
        stepCounter.setVisibility(View.VISIBLE);
        totalStep.setVisibility(View.VISIBLE);

        updateSteps(tdiff+" sec", stepDiff+"", totalStepCount+"");
    }

    private void updateSteps(String td, String sd, String ts) {
        steps = healthData.child("stepCounter");
        healthStat = steps.child("healthStat "+datetime.format(new Date()));
        DatabaseReference date = healthStat.child("date");
        date.setValue(datetime.format(new Date())+"");
        DatabaseReference duration = healthStat.child("duration");
        duration.setValue(td);
        DatabaseReference value = healthStat.child("stepsTaken");
        value.setValue(sd);
        DatabaseReference totalsteps = healthStat.child("totalStepsOntheDay");
        totalsteps.setValue(ts);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        totalStepCount = (int) event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
