package com.example.grocery.models;

public class ModelShop {
    private  String accountType,address,city,country,deliveryFee,email,latitude,longitude,name,online,phone,profileImage,shopName,shopOpen,state,timestamp,uid;

    public ModelShop() {

    }

    public ModelShop(String accountType,String address,String city,String country,String deliveryFee,String email,String latitude,String longitude,String name,String online,String phone,String profileImage,String shopName,String shopOpen,String state,String timestamp,String uid) {
        this.accountType = accountType;
        this.address = address;
        this.city = city;
        this.country = country;
        this.deliveryFee = deliveryFee;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.online = online;
        this.phone = phone;
        this.profileImage = profileImage;
        this.shopName = shopName;
        this.shopOpen = shopOpen;
        this.state = state;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(String shopOpen) {
        this.shopOpen = shopOpen;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
