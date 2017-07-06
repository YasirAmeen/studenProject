package apps.dina.zeft;

import io.realm.RealmObject;

/**
 * Created by Asmaa Elamal on 6/18/2017.
 */

public class Player extends RealmObject {
    private int id;
    private String Name;
    private String StrokeRate;
    private String StrokeCount;
    private String Distance;
    private String ElapsedTime;
    private String Clock;

    public Player(){

    }

    public String getClock() {
        return Clock;
    }

    public void setClock(String clock) {
        Clock = clock;
    }





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStrokeRate() {
        return StrokeRate;
    }

    public void setStrokeRate(String strokeRate) {
        StrokeRate = strokeRate;
    }

    public String getStrokeCount() {
        return StrokeCount;
    }

    public void setStrokeCount(String strokeCount) {
        StrokeCount = strokeCount;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getElapsedTime() {
        return ElapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        ElapsedTime = elapsedTime;
    }


}
