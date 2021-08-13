package com.cr4zyrocket.foodappserver.Model;

public class Manager {
    private String Name,Password,Phone,Address;

    public Manager() {
    }

    public Manager(String name, String password, String phone, String address) {
        Name = name;
        Password = password;
        Phone = phone;
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
