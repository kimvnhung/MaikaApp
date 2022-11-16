package hung.kv.maikaapp.database;

public class SchoolPerson extends Person{
    private String username = "";
    private String password = "";

    public SchoolPerson(String name, int age) {
        super(name, age);
    }

    public SchoolPerson(String name, int age, String username, String password) {
        super(name, age);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
