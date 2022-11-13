package hung.kv.maikaapp.database;

import java.util.Date;

public class Task {
    private String name = "";
    private Date time;
    private String place = "";

    public Task(String name, Date time, String place) {
        this.name = name;
        this.time = time;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
