package kevlich.fit.bstu.lab4;

public class Contacts {
    public String id;
    public String name, email, location, phone, profile;
    public Contacts(String id, String name, String email, String location, String phone, String profile){
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.phone = phone;
        this.profile = profile;
    }

    public Contacts(){

    }
}
