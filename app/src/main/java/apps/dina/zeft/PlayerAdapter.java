package apps.dina.zeft;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class PlayerAdapter extends RecyclerView.Adapter <PlayerAdapter.ViewHolder>{
    private Realm realm;
   private List <Player> PlayersList;
    private final LayoutInflater inflater;
    private Context context;

    public PlayerAdapter (Context context){
        inflater=LayoutInflater.from(context);
        this.context=context;
        realm=Realm.getDefaultInstance();
        PlayersList = new ArrayList<>();

        RealmQuery< Player> query = realm.where(Player.class);
        RealmResults<Player> realmResults = query.findAll();

        for(Player results : realmResults) {

            PlayersList.add(results);
        }
    }
    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_row,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(PlayerAdapter.ViewHolder holder,final int position) {

        final Player players = PlayersList.get(position);

        holder._name.setText(players.getName());
        holder._stroker.setText(players.getStrokeRate());
        holder._strokec.setText(players.getStrokeCount());
        holder._distance.setText(players.getDistance());
        holder._startt.setText(players.getClock());
        holder._elapsedt.setText(players.getElapsedTime());

        holder._close_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //Set Message and Title
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to remove from the database?.")
                        .setTitle("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Player _players = PlayersList.get(position);
                        realm.beginTransaction();
                        Player currentPlayer = realm.where(Player.class).equalTo("id",_players.getId()).findFirst();
                        currentPlayer.deleteFromRealm();
                        realm.commitTransaction();
                        PlayersList.remove(position);
                        notifyDataSetChanged();
                    }
                });

                //Set When Cancel Button Click
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Dismissing the alertDialog
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {

        return PlayersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView _close_btn;
        TextView _name;
        TextView _strokec;
        TextView _stroker;
        TextView _distance;
        TextView _startt;
        TextView _elapsedt;

        public ViewHolder(View itemView) {
            super(itemView);

            _name = (TextView) itemView.findViewById(R.id.txt_name);
            _strokec = (TextView) itemView.findViewById(R.id.txt_strokec);
            _stroker = (TextView) itemView.findViewById(R.id.txt_stroker);
            _distance = (TextView) itemView.findViewById(R.id.txt_distance);
            _startt= (TextView) itemView.findViewById(R.id.txt_startt);
            _elapsedt = (TextView) itemView.findViewById(R.id.txt_elapsedt);
            _close_btn = (ImageView) itemView.findViewById(R.id.btn_close);
        }
    }
}
