package opensource.karthik.healthapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CompleteSignup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText passwordEntry;
    private EditText nameEntry;
    private EditText ageEntry;
    private TextView email;

    private String savedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_signup);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        passwordEntry = (EditText) findViewById(R.id.passwordEntry);
        nameEntry = (EditText) findViewById(R.id.nameEntry);
        ageEntry = (EditText) findViewById(R.id.ageEntry);
        email = (TextView) findViewById(R.id.email);

        Intent intent = getIntent();
        savedEmail = intent.getExtras().getString("email");
        email.setText(savedEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void submitClick(View v) {
        String password = passwordEntry.getText().toString();
        final String name = nameEntry.getText().toString();
        final String age = ageEntry.getText().toString();

        if(password.isEmpty() || password.length() < 6) {
            Toast.makeText(this,"You must enter a password of at least 6 characters",Toast.LENGTH_LONG).show();
            return;
        }

        if(name.isEmpty() || age.isEmpty()) {
            Toast.makeText(this,"All fields are required",Toast.LENGTH_LONG).show();
            return;
        }

        // Create a database reference for that user
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference profiles = database.getReference("profiles");
        DatabaseReference thisUser = profiles.child(current.getUid());
        DatabaseReference thisUserEmail = thisUser.child("email");
        thisUserEmail.setValue(savedEmail);
        DatabaseReference thisUserMessages = thisUser.child("messageCount");
        thisUserMessages.setValue(0);
        DatabaseReference thisUserName = thisUser.child("name");
        thisUserName.setValue(name);
        DatabaseReference thisUserAge = thisUser.child("age");
        thisUserAge.setValue(age);

        FirebaseAuth.getInstance().getCurrentUser().updatePassword(password);

        Toast.makeText(this,"Signup Complete !",Toast.LENGTH_LONG).show();

        Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goToNextActivity);

    }
}
