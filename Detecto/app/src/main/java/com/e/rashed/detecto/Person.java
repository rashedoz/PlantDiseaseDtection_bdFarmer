package com.e.rashed.detecto;

import java.util.ArrayList;
import java.util.List;

class Person {
    String name;
    String age;
    String remedy_url;
    String photo_url;

    Person(String name, String age, String remedy_url, String photo_url) {
        this.name = name;
        this.age = age;
        this.remedy_url = remedy_url;
        this.photo_url = photo_url;
    }

    public String getName() {
        return name;
    }

    public String getStr() {
        return age;
    }

    public String getUrl() {
        return photo_url;
    }



}

