package org.techtown.foodtruck.DO;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String idToken; // firebase 고유아이디 key값
    private String email; // 아이디
    private String password; // 패스워드
    private String name; // 이름

    public UserAccount() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
