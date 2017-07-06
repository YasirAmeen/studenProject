package apps.dina.zeft;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;


public class TabFragment2 extends Fragment {
   /*private String t;
    private String clock;
    private String N;
    private String Distance;
    private boolean f ;


    private Realm realm;
    private AlertDialog dialog;*/
    PlayerAdapter adapter;
    private View v;


    //BroadcastReceiver BCReceiver1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v= inflater.inflate(R.layout.tab_fragment_2, container, false);

        refreshDataSet();


      /*  Bundle bundle=getActivity().getIntent().getExtras();

        if(bundle!=null) {
            t = bundle.getString("ElapsedTime");
            clock = bundle.getString("Clock");
            Distance = bundle.getString("Distance");
            f=bundle.getBoolean("Flag");
        }

        BCReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

               // float FinalDistance = intent.getFloatExtra(GPSTracker.BCDISTANCE, 0);
                String N=intent.getStringExtra(MenueActivity.BCNAME);
               // Distance=String.valueOf(FinalDistance);

            }
        }; */

      /*  if(f) {
           realm=Realm.getDefaultInstance();
        refreshDataSet();
        RealmResults<Player> query = realm.where(Player.class).findAll();
        int nextID = query.size(); //Retrieving a results size.
        realm.beginTransaction();
        Player p=realm.createObject(Player.class);
        p.setId(nextID + 1);
        p.setName(N);
        p.setDistance(Distance);
        p.setElapsedTime(t);
        p.setClock(clock);
        realm.commitTransaction();
            Toast.makeText(getActivity(), "data ....", Toast.LENGTH_SHORT).show();
        refreshDataSet();
            Toast.makeText(getActivity(), "data2 .....", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "No data saved", Toast.LENGTH_SHORT).show();
        }*/

        Toast.makeText(getActivity(), "You are here", Toast.LENGTH_SHORT).show();
        return v;

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refreshDataSet();
        }
    }

    public  void refreshDataSet(){
        adapter = new PlayerAdapter(getActivity());

        RecyclerView rview=  (RecyclerView) v.findViewById (R.id.my_recycler_view);
        rview.setItemAnimator(new DefaultItemAnimator());
        rview.setAdapter(adapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));
    }






}
