package fr.wcs.teamwinsoft.winnews;

public class UserModel {
    private String firstname;
    private String lastname;
    private String societe;

    public UserModel() {
    }

    public UserModel(String firstname, String lastname, String societe) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.societe = societe;
    }

    public String getFirstname() {
        return firstname;
    }

    public UserModel setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public UserModel setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getSociete() {
        return societe;
    }

    public UserModel setSociete(String societe) {
        this.societe = societe;
        return this;
    }
}
