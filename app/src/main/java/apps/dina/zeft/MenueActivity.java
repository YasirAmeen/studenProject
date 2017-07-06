package apps.dina.zeft;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

public class MenueActivity extends AppCompatActivity {

    TextView _Name;
    Button _btn;
    String n;
    public static final String BCR = "apps.dina.brodcast.Result";
    public static final String BCNAME = "apps.dina.brodcast.n";
    LocalBroadcastManager broadcaster1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);
        _Name = (TextView) findViewById(R.id.NameText);
        _btn=(Button) findViewById(R.id.btn);
        _btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n = _Name.getText().toString();
                Prefs.putString("playerName",n);
                finish();

            }
        });

       //
      /*  broadcaster1 = LocalBroadcastManager.getInstance(this);
        sendResult(n);*/

    }

    public void sendResult(String name) {
        Intent intent = new Intent(BCR);
        intent.putExtra(BCNAME, name);

        broadcaster1.sendBroadcast(intent);
    }
}