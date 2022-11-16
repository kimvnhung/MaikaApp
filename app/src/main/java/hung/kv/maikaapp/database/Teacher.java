package hung.kv.maikaapp.database;

import java.util.ArrayList;

public class Teacher extends SchoolPerson{

    public ArrayList<String> getSubject() {
        return subject;
    }

    public void setSubject(ArrayList<String> subject) {
        this.subject = subject;
    }

    private ArrayList<String> subject;


    public Teacher(String name, int age, ArrayList<String> subject) {
        super(name, age);
        this.subject = subject;
    }

    public Teacher(String name, int age, String username, String password, ArrayList<String> subject) {
        super(name, age, username, password);
        this.subject = subject;
    }
}
