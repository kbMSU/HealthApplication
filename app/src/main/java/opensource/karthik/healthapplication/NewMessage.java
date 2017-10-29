package opensource.karthik.healthapplication;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewMessage extends AppCompatActivity {

    EditText messageTarget;
    EditText messageContent;
    EditText messageSubject;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference messageCount;
    private DatabaseReference messages;

    private String target;
    private String content;
    private String subject;

    //private String karthikId = "mBI5KL8viDZg5s3VDcQkZ36Qpil2";
    //private String burhanId = "2mDsmDYbfoRBAgYKAJ7liVLyf8x1";

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
        target = messageTarget.getText().toString();
        content = messageContent.getText().toString();
        subject = messageSubject.getText().toString();

        if(target.isEmpty() || content.isEmpty() || subject.isEmpty()) {
            Toast.makeText(NewMessage.this, "You must fill all fields",
                    Toast.LENGTH_LONG).show();
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //String targetId = karthikId;
        //if(currentUser.getUid().equals(karthikId)) {
        //    targetId = burhanId;
        //}

        DatabaseReference profiles = database.getReference("profiles");
        profiles.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                sendMessage(dataSnapshot.getChildren());
                Log.d("DB_VALUE", "Value is: ");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DB_VALUE", "Failed to read value.", error.toException());
                Toast.makeText(NewMessage.this, "Error sending message",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendMessage(Iterable<DataSnapshot> children) {
        boolean found = false;
        for(DataSnapshot child : children) {
            String email = child.child("email").getValue(String.class);
            if(!email.equals(target)) {
                continue;
            } else {
                found = true;
                DatabaseReference targetRef = child.getRef();
                messages = targetRef.child("messages");

                DatabaseReference newMessage = messages.child(currentUser.getUid()+System.currentTimeMillis());
                DatabaseReference fromRef = newMessage.child("from");
                fromRef.setValue(currentUser.getEmail());
                DatabaseReference subjectRef = newMessage.child("subject");
                subjectRef.setValue(subject);
                DatabaseReference contentRef = newMessage.child("content");
                contentRef.setValue(content);

                Toast.makeText(NewMessage.this, "Message Sent !",
                        Toast.LENGTH_LONG).show();

                Intent goToNextActivity = new Intent(getApplicationContext(), Messaging.class);
                startActivity(goToNextActivity);
                break;
            }
        }
        if(!found) {
            Toast.makeText(NewMessage.this, "No such target user exists",
                    Toast.LENGTH_LONG).show();
        }
    }
}
