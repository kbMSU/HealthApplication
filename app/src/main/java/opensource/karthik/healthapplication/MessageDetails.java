package opensource.karthik.healthapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MessageDetails extends AppCompatActivity {

    TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        contentView = (TextView) findViewById(R.id.content);

        String content = getIntent().getExtras().getString("content");
        contentView.setText(content);
    }
}
