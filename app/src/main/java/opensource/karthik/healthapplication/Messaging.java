package opensource.karthik.healthapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Messaging extends AppCompatActivity {

    private ListView messagesView;
    private TextView noMessagesView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        messagesView = (ListView) findViewById(R.id.messages);
        noMessagesView = (TextView) findViewById(R.id.no_messages);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference thisUser = database.getReference(currentUser.getUid());

        DatabaseReference messageCount = thisUser.child("messageCount");
        // Read message count from the database
        messageCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);
                if(value == 0) {
                    noMessagesView.setVisibility(View.GONE);
                    messagesView.setVisibility(View.VISIBLE);
                } else {
                    noMessagesView.setVisibility(View.VISIBLE);
                    messagesView.setVisibility(View.GONE);
                }
                Log.d("DB_VALUE", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DB_VALUE", "Failed to read value.", error.toException());
            }
        });
    }
}
