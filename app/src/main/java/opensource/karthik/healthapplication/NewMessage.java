package opensource.karthik.healthapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewMessage extends AppCompatActivity {

    EditText messageTarget;
    EditText messageContent;
    EditText messageSubject;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference messageCount;
    private DatabaseReference messages;

    private String karthikId = "mBI5KL8viDZg5s3VDcQkZ36Qpil2";
    private String burhanId = "2mDsmDYbfoRBAgYKAJ7liVLyf8x1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        messageTarget = (EditText) findViewById(R.id.message_target);
        messageContent = (EditText) findViewById(R.id.message_content);
        messageSubject = (EditText) findViewById(R.id.message_subject);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void SendMessage(View v) {
        String target = messageTarget.getText().toString();
        String content = messageContent.getText().toString();
        String subject = messageSubject.getText().toString();

        if(target.isEmpty() || content.isEmpty() || subject.isEmpty()) {
            Toast.makeText(NewMessage.this, "You must fill all fields",
                    Toast.LENGTH_LONG).show();
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        String targetId = karthikId;
        if(currentUser.getUid().equals(karthikId)) {
            targetId = burhanId;
        }

        DatabaseReference targetUser = database.getReference(targetId);
        messages = targetUser.child("messages");
        DatabaseReference newMessage = messages.child(currentUser.getUid()+System.currentTimeMillis());
        newMessage.setValue(content);

        Toast.makeText(NewMessage.this, "Message Sent !",
                Toast.LENGTH_LONG).show();

        Intent goToNextActivity = new Intent(getApplicationContext(), Messaging.class);
        startActivity(goToNextActivity);
    }
}
