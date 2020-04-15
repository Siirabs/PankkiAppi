package com.example.pankkiappi.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private byte[] salt;
    private static User u = new User();

    public static User getInstance() {
        return u;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getSalt() { return salt; }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}

