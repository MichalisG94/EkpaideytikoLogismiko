package com.michalis.ekpaideytikologismiko;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class UserProfile {

    public String name, surname, userID;
    public int age;

    public UserProfile() {
    }

    public UserProfile(String name, String surname, int age, String userID) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.userID = userID;
    }
}
