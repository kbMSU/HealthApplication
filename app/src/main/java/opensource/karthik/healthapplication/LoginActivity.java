package opensource.karthik.healthapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText emailEntry;
    private EditText passwordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        emailEntry = (EditText) findViewById(R.id.email_entry);
        passwordEntry = (EditText) findViewById(R.id.password_entry);
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

    public void loginClick(View v) {
        String email = emailEntry.getText().toString();
        String password = passwordEntry.getText().toString();
        login(email,password);
    }

    public void signupClick(View v) {
        String email = emailEntry.getText().toString();
        String password = passwordEntry.getText().toString();
        Log.d("EMAIL", email);
        signup(email,password);
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Failed to authenticate that email and password",
                                Toast.LENGTH_LONG).show();
                        Log.d("SIGNIN ERROR", task.getException().toString());
                    } else {
                        // Login succeeded : Move onto Dashboard view
                        Intent goToNextActivity = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(goToNextActivity);
                    }
                }
            });
    }

    public void signup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Failed to create a user with that email and password",
                                Toast.LENGTH_LONG).show();
                        Log.d("SIGNUP ERROR", task.getException().toString());
                    } else {
                        // Show that the user has been created
                        Toast.makeText(LoginActivity.this, "User has been created",
                                Toast.LENGTH_LONG).show();

                        // Create a database reference for that user
                        String id = task.getResult().getUser().getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        DatabaseReference thisUser = database.getReference(id);
                        DatabaseReference thisUserMessages = thisUser.child("messageCount");
                        thisUserMessages.setValue(0);
                    }
                }
            });
    }
}
