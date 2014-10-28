package com.example.synclists.synclists;

import java.io.Serializable;

/**
 * Created by ethan on 10/27/14.
 */
public class Task implements Serializable {
    private String name = "";
    private int id;
    private boolean completed = false;

    public Task(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }
}
