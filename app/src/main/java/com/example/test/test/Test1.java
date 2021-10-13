package com.example.test.test;

public class Test1 {
    public int id = 1;
    private static Test1 instance;

    public static Test1 getInstance() {
        if (instance == null){
            instance = new Test1();
        }
        return instance;
    }

    public int getId(){
        return 1;
    }

}
