
package apps.dina.zeft;

/**
 * Created by Dina on 12/6/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.SENSOR_SERVICE;
import static java.lang.Math.toDegrees;

public class TabFragment1 extends Fragment implements SensorEventListener {

    Intent myIntent;
    Button btnShowLocation;
    private TextView tv;
    private TextView tv_2;
    private TextView tv_3;
    private TextView tv_4;
    private TextView tv_5;
    private TextClock tc;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean button_status;
    private String ConvertedTime;
    private float FinalDistance;
    private String FinalD;
    float[] mGravity;
    float[] mGeomagnetic;
    float pitch;
    float roll;
    private boolean flag=false;
    String N;
    Realm realm;

    BroadcastReceiver BCReceiver;
    private Handler handler = new Handler();
    private Runnable r;

    String Time;

    long lastTime;
    SensorManager sm;

    int dotcounter=0;

    LineChart chart;
    List<Entry> entries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        tv = (TextView) view.findViewById(R.id.ElapsedTimeText);
        tv_2 = (TextView) view.findViewById(R.id.StrokeRateText);
        tv_3 = (TextView) view.findViewById(R.id.SpeedText);
        tv_4 = (TextView) view.findViewById(R.id.DistanceText);
        tv_5 = (TextView) view.findViewById(R.id.TiltingText);
        tc= (TextClock) view.findViewById(R.id.textClock);


        BCReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                float speed = intent.getFloatExtra(GPSTracker.BCSPEED, 0);
                 FinalDistance = intent.getFloatExtra(GPSTracker.BCDISTANCE, 0);
                String N=intent.getStringExtra(MenueActivity.BCNAME);
                tv_3.setText(String.format("%.2f", speed) +" m/s");
                tv_4.setText(String.format("%.2f", FinalDistance ) +" m");
                addSpeed(speed);
            }
        };
        FinalD=String.valueOf(FinalDistance);

        myIntent=new Intent(getActivity(),GPSTracker.class);
        button_status = false;
        //getActivity().startService(myIntent);
        btnShowLocation = (Button) view.findViewById(R.id.Start);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                button_status = !button_status;
                if(button_status){
                    flag=false;
                    btnShowLocation.setText("STOP");
                    getActivity().startService(myIntent);
                    lastTime = System.currentTimeMillis();
                    Time = (String) tc.getFormat24Hour();
                    startCounter();
                }
                else{
                    flag=true;
                    btnShowLocation.setText("START");
                    stopCounter();
                    getActivity().stopService(myIntent);


                    Fragment tab2=new TabFragment2();
                    Bundle bundle = new Bundle();
                    tab2.setArguments(bundle);
                    bundle.putBoolean("Flag",flag);
                    bundle.putString("ElapsedTime",ConvertedTime);
                    bundle.putString("Clock",Time);
                    bundle.putString("Distance",FinalD);


                    realm=Realm.getDefaultInstance();
                    int id = -1;
                    RealmResults<Player> results = realm.where(Player.class).findAll();
                    if(results.size()  == 0) {

                        id = 1;

                    } else {

                        // max is an aggregate function to check maximum value
                        id= results.max("id").intValue() + 1;

                    }
                    realm.beginTransaction();
                    RealmResults<Player> query = realm.where(Player.class).findAll();
                    int nextID = query.size(); //Retrieving a results size.
                    Player p=realm.createObject(Player.class);
                    p.setId(id);
                    p.setName(N);
                    p.setDistance(FinalD);
                    p.setElapsedTime(ConvertedTime);
                    p.setClock(Time);
                    realm.commitTransaction();


                   /* FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                    fragmenttransaction.replace(R.id.tab1,tab2);
                    fragmenttransaction.addToBackStack(null);
                    fragmenttransaction.commit();*/


                    //refreshDataSet();

                    //TabFragment2 tab2 = ((TabFragment2)getActivity().getSupportFragmentManager().findFragmentById(R.id.tab2)).Show();



                    Toast.makeText(getActivity(), "Your Training Data is Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lastTime = System.currentTimeMillis();


        sm = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        chart = (LineChart) view.findViewById(R.id.graph);
        entries = new ArrayList<Entry>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(BCReceiver,new IntentFilter(GPSTracker.BCRESULT));
    }

    @Override

    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(BCReceiver);
        super.onStop();
    }

    public void addSpeed(float speed){
        entries.add(new Entry(dotcounter++,speed));
        LineDataSet dataSet = new LineDataSet(entries, "Speed"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }

    public void initListeners()
    {
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy()
    {
        sm.unregisterListener(this);
        getActivity().stopService(myIntent);
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        initListeners();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        sm.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    mGravity = sensorEvent.values;

        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = sensorEvent.values;
        }

        //Calc pitch and roll
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                pitch = orientation[1];
                roll = orientation[2];
                //dataValue = (int)toDegrees(pitch) +" , "+ (int)toDegrees(roll);
                if((int)toDegrees(roll)>(-20) && (int)toDegrees(roll)<(20))
                    tv_5.setText("Normal");
                else
                    tv_5.setText("Warning");
            }
        }
    }
        //




    public void startCounter(){
        r = new Runnable()
        {   public void run()
        {
            long actualTime = System.currentTimeMillis();
            final long time = actualTime - lastTime;
            // Perform your Action Here..
          ConvertedTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
            tv.setText(String.valueOf(ConvertedTime));
            handler.postDelayed(this, 1000);
        }
        };
        handler.postDelayed(r, 1000);
    }

    public void stopCounter(){
        handler.removeCallbacks(r);
    }

}

