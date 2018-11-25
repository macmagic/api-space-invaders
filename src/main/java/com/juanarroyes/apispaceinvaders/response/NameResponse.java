package com.juanarroyes.apispaceinvaders.response;

public class NameResponse {

    private String name;
    private String email;

    public NameResponse() {
    }

    public NameResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
