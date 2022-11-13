package hung.kv.maikaapp.database;

import java.util.ArrayList;

public class Student extends Person{
    private String className = "";

    public Student(String name, int age, String className) {
        super(name, age);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


}
