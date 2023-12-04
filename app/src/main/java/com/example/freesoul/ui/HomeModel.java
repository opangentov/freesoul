package com.example.freesoul.ui;

public class HomeModel {
    private String name, number, group, instagram;
    public HomeModel(String name, String number, String group, String instagram) {
        this.name = name;
        this.number = number;
        this.group = group;
        this.instagram = instagram;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {

        this.number = number;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getInstagram() {
        return instagram;
    }
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
