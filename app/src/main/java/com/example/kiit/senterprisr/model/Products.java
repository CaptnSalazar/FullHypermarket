package com.example.kiit.senterprisr.model;

public class Products {
    private String name,description,price,image,category,stock;
    public Products()
    {

    }

    public Products(String name, String description, String price, String image, String category, String stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.stock = stock;
    }

    public String getStock() {
        return stock;
    }

    public Products(String name) {
        this.name = name;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Products(String name, String description, String price, String image, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
