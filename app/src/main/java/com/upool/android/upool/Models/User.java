package com.upool.android.upool.Models;

/**
 * Created by Darren on 7/19/2017.
 */

public class User {
    private String userID;
    private String email;
    private String password;
    private boolean isAuthenticated;
    private String title;
    private String firstName;
    private String lastName;

    public User() { }

    public User(String userID, String email, String password, boolean isAuthenticated, String title, String firstName, String lastName) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.isAuthenticated = isAuthenticated;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
