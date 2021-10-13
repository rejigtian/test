package com.example.test.test;

public class Priority implements Comparable<Priority> {
    private int id;
    private int priority;

    public Priority(int id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    @Override
    public int compareTo(Priority o) {
        return o.priority - priority;
    }

    @Override
    public String toString() {
        return "Priority{" +
                "id=" + id +
                ", priority=" + priority +
                '}';
    }
}
