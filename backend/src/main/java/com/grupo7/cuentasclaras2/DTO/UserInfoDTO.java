package com.grupo7.cuentasclaras2.DTO;

import java.util.List;

public class UserInfoDTO {
    private String username;
    private List<NotificationDTO> notifications;
    private double balance;

    public UserInfoDTO() {
    }

    public UserInfoDTO(String username, List<NotificationDTO> notifications, double balance) {
        this.username = username;
        this.notifications = notifications;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
