package opensource.karthik.healthapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class Messaging extends AppCompatActivity {

    private ListView messagesView;
    private TextView noMessagesView;

    private FirebaseAuth mAuth;

    DatabaseReference messageCount;
    DatabaseReference messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        messagesView = (ListView) findViewById(R.id.messages);
        noMessagesView = (TextView) findViewById(R.id.no_messages);
        noMessagesView.setText("Loading Messages");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference thisUser = database.getReference(currentUser.getUid());

        messageCount = thisUser.child("messageCount");
        // Read message count from the database
        messageCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);
                if(value > 0) {
                    noMessagesView.setVisibility(View.GONE);
                    messagesView.setVisibility(View.VISIBLE);
                } else {
                    noMessagesView.setVisibility(View.VISIBLE);
                    messagesView.setVisibility(View.GONE);
                }
                noMessagesView.setText("No messages to show !");
                Log.d("DB_VALUE", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DB_VALUE", "Failed to read value.", error.toException());
            }
        });

        messages = thisUser.child("messages");
        messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    updateMessagesList(dataSnapshot);
                }
                Log.d("DB_VALUE", "Value is: TRUE" );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DB_VALUE", "Failed to read value.", error.toException());
            }
        });

    }

    private void updateMessagesList(DataSnapshot snapshot) {
        ArrayList<String> listOfMessages = new ArrayList<>();
        Iterable<DataSnapshot> iter = snapshot.getChildren();
        for(DataSnapshot snap : iter) {
            String message = snap.getValue(String.class);
            listOfMessages.add(message);
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listOfMessages);
        messagesView.setAdapter(itemsAdapter);

        messageCount.setValue(listOfMessages.size());
    }

    public void newMessageClick(View v) {
        Intent goToNextActivity = new Intent(getApplicationContext(), NewMessage.class);
        startActivity(goToNextActivity);
    }
}
