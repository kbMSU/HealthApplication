package opensource.karthik.healthapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.value;


public class Profile extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference nameRef;
    private DatabaseReference ageRef;
    private DatabaseReference genderRef;
    private DatabaseReference bloodTypeRef;
    private  String userID;
    private String name;
    private String email;
    private String age;
    private String gender;
    private String bloodtype;

    TextView displayEmail;
    TextView displayName;
    TextView displayAge;
    TextView displayGender;
    TextView displayBloodType;
    EditText inputNotes;
    Button bSaveNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        displayEmail = (TextView) findViewById(R.id.tvDisplayEmail);
        displayName = (TextView) findViewById(R.id.tvDisplayName);
        displayAge = (TextView) findViewById(R.id.tvAge);
        displayGender = (TextView) findViewById(R.id.tvGender);
        displayBloodType = (TextView) findViewById(R.id.tvBloodType);
        inputNotes = (EditText) findViewById(R.id.inputNotes);
        bSaveNotes = (Button) findViewById(R.id.bSaveNote);

        //declare the database reference object. This is what we use to access the database.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        final DatabaseReference thisUser = mFirebaseDatabase.getReference(userID);

        nameRef = thisUser.child("name");

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                name = dataSnapshot.getValue(String.class);
                displayName.setText("Name: " +name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ageRef = thisUser.child("age");

        ageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                age = dataSnapshot.getValue(String.class);
                displayAge.setText("Age: " +age);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        genderRef = thisUser.child("gender");

        genderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                gender = dataSnapshot.getValue(String.class);
                displayGender.setText("Gender: " +gender);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        bloodTypeRef = thisUser.child("bloodtype");

        bloodTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                bloodtype = dataSnapshot.getValue(String.class);
                displayBloodType.setText("Blood Type: " +bloodtype);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        bSaveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = inputNotes.getText().toString();
                DatabaseReference noteRef  = thisUser.child("notes");
                noteRef.setValue(notes);
            }
        });
        displayEmail.setText("Email: " +user.getEmail());

    }


}
