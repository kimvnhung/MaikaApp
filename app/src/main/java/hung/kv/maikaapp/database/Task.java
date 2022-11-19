package hung.kv.maikaapp.database;


import android.util.Log;

import java.util.Date;

public class Task {
    private String name = "";
    private Date from,to;
    private String place = "";

    public Task(String name,String place, Date from, Date to) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getPeriodStart() {
        int hours = from.getHours();
        return (hours-7)+1;
    }

    public boolean isProcessing(){
        Log.d("Task ",from.toString()+" "+new Date().toString()+" "+to.toString());
        Date currentTime = new Date();
        if (from.before(currentTime) && to.after(currentTime)){
            return true;
        }
        return false;
    }

    public boolean isProcessing(Date time){
        if (from.before(time) && to.after(time)){
            return true;
        }
        return false;
    }

    public String getBuoi(){
        if (from.getHours() < 12){
            return "sáng";
        }

        return "chiều";
    }

    public String toString() {
        return "Task : "+name+" "+place+" "+DataManager.GetDayInWeek(from)+" "+getPeriodStart();
    }
}
