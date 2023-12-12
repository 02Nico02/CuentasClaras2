package com.grupo7.cuentasclaras2.DTO;

public class Credentials {
    private String token;
    private int expiration_in_sec;
    private String userName;

    public Credentials() {
    }

    public Credentials(String token, int expiration_in_sec, String userName) {
        this.token = token;
        this.expiration_in_sec = expiration_in_sec;
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiration_in_sec() {
        return expiration_in_sec;
    }

    public void setExpiration_in_sec(int expiration_in_sec) {
        this.expiration_in_sec = expiration_in_sec;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
