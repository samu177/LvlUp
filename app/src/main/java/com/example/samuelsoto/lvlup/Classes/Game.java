package com.example.samuelsoto.lvlup.Classes;

public class Game {

    //property basics
    private String id;
    private String name;



    //constructor
    public Game(String id, String name){
        this.id = id;
        this.name = name;

    }

    //getters
    public String getId() { return id; }
    public String getName() {return name; }


}