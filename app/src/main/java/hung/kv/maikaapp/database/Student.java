package hung.kv.maikaapp.database;

import java.util.ArrayList;

public class Student extends SchoolPerson{
    private String className = "";

    public Student(String name, int age, String className) {
        super(name, age);
        this.className = className;
    }

    public Student(String name, int age,String className, String username, String password) {
        super(name, age, username, password);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String toString(){
        String task = "";
        for (int i=0;i<getTasks().size();i++){
            task += getTasks().get(i).toString() + "\n";
        }
        return getName()+" "+getAge()+" "+getClassName()+" "+getUsername()+" "+getPassword()+" "+task;
    }
}
