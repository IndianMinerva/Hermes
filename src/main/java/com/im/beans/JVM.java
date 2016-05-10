package com.im.beans;

public class JVM {
    private int id;
    private String name;

    public JVM(String id, String name) {
        this.id = Integer.parseInt(id);
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
