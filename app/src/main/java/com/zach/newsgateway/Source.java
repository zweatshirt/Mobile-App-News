package com.zach.newsgateway;

import java.io.Serializable;

public class Source implements Serializable {
    private String id;
    private String name;
    private String category;

    public Source(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Source)) return false;
        Source s2 = (Source) o;
        if (s2.getCategory().equals(this.getCategory())) {
            if (s2.getId().equals(this.getId())) {
                return s2.getName().equals(this.getName());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getId();
    }

}
