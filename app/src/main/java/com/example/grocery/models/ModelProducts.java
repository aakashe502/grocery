package com.example.grocery.models;

public class ModelProducts {

private  String discountAvailable,discountNote,discountPrice,
        originalPrice,productCategory,productDescription,productIcon,productId,productQuantity,productTitle,timestamp,uid;

    public ModelProducts() {
    }

    public ModelProducts(String discountAvailable,String discountNote,String discountPrice,String originalPrice,String productCategory,String productDescription,String productIcon,String productId,String productQuantity,String productTitle,String timestamp,String uid) {
        this.discountAvailable = discountAvailable;
        this.discountNote = discountNote;
        this.discountPrice = discountPrice;
        this.originalPrice = originalPrice;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.productIcon = productIcon;
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.productTitle = productTitle;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
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

