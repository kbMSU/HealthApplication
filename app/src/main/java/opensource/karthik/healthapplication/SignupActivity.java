package opensource.karthik.healthapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText emailEntry;
    private EditText accessCodeEntry;
    private TextView emailView;
    private TextView codeView;
    private Button getCodeButton;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        emailEntry = (EditText) findViewById(R.id.emailEntry);
        accessCodeEntry = (EditText) findViewById(R.id.accessCodeEntry);
        emailView = (TextView) findViewById(R.id.email);
        codeView = (TextView) findViewById(R.id.accessCode);
        getCodeButton = (Button) findViewById(R.id.getCode);
        verifyButton = (Button) findViewById(R.id.verifyCode);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        hideAccessCodeViews();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void getAccessCodeClick(View v) {
        String email = emailEntry.getText().toString();
        if(email.isEmpty()) {
            Toast.makeText(this,"You must enter an email to receive an access code",Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,"temp1234");
        mAuth.signInWithEmailAndPassword(email,"temp1234");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Failed to send access code", Toast.LENGTH_LONG).show();
                            Log.d("GET CODE ERROR", task.getException().toString());
                        } else {
                            Toast.makeText(SignupActivity.this, "Access code sent", Toast.LENGTH_LONG).show();
                            showAccessCodeViews();
                        }
                    }
                });
    }

    public void verifyAccessCodeClick(View v) {
        String accessCode = accessCodeEntry.getText().toString();
        if(accessCode.isEmpty() || !accessCode.equals("5734")) {
            Toast.makeText(this,"You must enter the correct access code to continue",Toast.LENGTH_LONG).show();
            return;
        }

        Intent goToNextActivity = new Intent(getApplicationContext(), CompleteSignup.class);
        goToNextActivity.putExtra("email", emailEntry.getText().toString());
        startActivity(goToNextActivity);
    }

    private void hideAccessCodeViews() {
        codeView.setVisibility(View.INVISIBLE);
        accessCodeEntry.setVisibility(View.INVISIBLE);
        verifyButton.setVisibility(View.INVISIBLE);
    }

    private void showAccessCodeViews() {
        codeView.setVisibility(View.VISIBLE);
        accessCodeEntry.setVisibility(View.VISIBLE);
        verifyButton.setVisibility(View.VISIBLE);
    }
}
