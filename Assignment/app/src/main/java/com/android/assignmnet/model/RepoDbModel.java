package com.android.assignmnet.model;



public class RepoDbModel {
    private long id;
    private String data;

    public RepoDbModel(long id, String data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RepoDbModel{" +
                "id=" + id +
                ", data='" + data + '\'' +
                '}';
    }
}
