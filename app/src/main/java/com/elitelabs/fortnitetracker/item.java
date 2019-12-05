package com.elitelabs.fortnitetracker;

public class item {

    private String rarity, name, category, price, id, imageLink, imageLink2, type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getImageLink2() {
        return imageLink2;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String  getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String itemName) {
        this.name = itemName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setImageLink2(String imageLink2) {
        this.imageLink2 = imageLink2;
    }

}
