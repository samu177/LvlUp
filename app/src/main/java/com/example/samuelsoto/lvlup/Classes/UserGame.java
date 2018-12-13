package com.example.samuelsoto.lvlup.Classes;

public class UserGame {

    //property basics
    private String id;
    private String name;
    private String summary;
    private String platforms;
    private String cost;
    private String comment;


    //constructor
    public UserGame(String id, String name){
        this.id = id;
        this.name = name;

    }

    //getters
    public String getId() { return id; }
    public String getName() {return name; }


}