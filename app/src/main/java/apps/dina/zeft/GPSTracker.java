package apps.dina.zeft;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

//location
public class GPSTracker extends Service {
    private LocationManager locManager;
    private myLocationListener locListener = new myLocationListener();
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private Handler handler = new Handler();
    private Runnable r;

    public static final String BCRESULT = "apps.dina.brodcast.result";
    public static final String BCSPEED = "apps.dina.brodcast.speed";
    public static final String BCDISTANCE = "apps.dina.brodcast.distance";
    // public static final String BCFDISTANCE="apps.dina.broadcast.distancee";
    LocalBroadcastManager broadcaster;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(r);
        Toast.makeText(getBaseContext(), "Service Stoped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(), "Service Started", Toast.LENGTH_SHORT).show();
        broadcaster = LocalBroadcastManager.getInstance(this);
        r = new Runnable() {
            public void run() {
                Log.v("Debug", "Hello");
                location();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
        return START_STICKY;
    }

    public void location() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        Log.v("Debug", "in on create.. 2");
        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
            Log.v("Debug", "Enabled..");
        }
        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locListener);
            Log.v("Debug", "Disabled..");
        }
        Log.v("Debug", "in on create..3");
    }

    public class myLocationListener implements LocationListener
    {
        double lat_old=0.0;
        double lon_old=0.0;
        double OldTime=System.currentTimeMillis();
        double lat_new;
        double lon_new;

        double FinalDistance;
        double speed=0.0;
        int K=1;

        @Override
        public void onLocationChanged(final Location location) {
            Log.v("Debug", "in onLocation changed..");
            if(location!=null){
                if (lon_old==0.0 && lat_old==0.0){
                    lat_old=location.getLatitude();
                    lon_old=location.getLongitude();
                    FinalDistance= 0;
                }
                locManager.removeUpdates(locListener);
                lat_new=location.getLatitude();
                lon_new =location.getLongitude();

                double NewTime = System.currentTimeMillis();
                double Time = (NewTime-OldTime)/1000;
                double distance = distance_on_geoid(lat_old, lon_old, lat_new, lon_new);
                speed = (distance/Time);
                FinalDistance=FinalDistance+distance;
                lat_old=lat_new;
                lon_old=lon_new;
                OldTime = NewTime;

                sendResult((float)speed,(float)FinalDistance);
            }

        }


        public void sendResult(float Speed, float Distance){
            Intent intent = new Intent(BCRESULT);
            intent.putExtra(BCSPEED, Speed);
            intent.putExtra(BCDISTANCE, Distance);
            broadcaster.sendBroadcast(intent);
        }

        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    double distance_on_geoid(double lat1, double lon1, double lat2, double lon2) {

        // Convert degrees to radians
        double M_PI=3.14159265358979300000;
        lat1 = lat1 * M_PI / 180.0;
        lon1 = lon1 * M_PI / 180.0;

        lat2 = lat2 * M_PI / 180.0;
        lon2 = lon2 * M_PI / 180.0;

        // radius of earth in metres
        double r = 6378100;

        // P
        double rho1 = r * Math.cos(lat1);
        double z1 = r * Math.sin(lat1);
        double x1 = rho1 * Math.cos(lon1);
        double y1 = rho1 * Math.sin(lon1);

        // Q
        double rho2 = r * Math.cos(lat2);
        double z2 = r * Math.sin(lat2);
        double x2 = rho2 * Math.cos(lon2);
        double y2 = rho2 * Math.sin(lon2);

        // Dot product
        double dot = (x1 * x2 + y1 * y2 + z1 * z2);
        double cos_theta = dot / (r * r);

        double theta = Math.acos(cos_theta);

        // Distance in Metres
        return r * theta;
    }
}
