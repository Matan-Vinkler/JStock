package com.example.jstock;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String email;
    private List<String> favoriteStocks;

    public User() {
        this.username = "";
        this.email = "";
        this.favoriteStocks = new ArrayList<String>();

        this.favoriteStocks.add("");
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.favoriteStocks = new ArrayList<String>();

        this.favoriteStocks.add("");
    }

    public User(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.favoriteStocks = new ArrayList<String>(user.getFavoriteStocks());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFavoriteStocks() {
        return new ArrayList<String>(this.favoriteStocks);
    }

    public void addFavoriteStock(String stock) {
        this.favoriteStocks.add(stock);
    }

    public void removeFavoriteStock(String stock) {
        this.favoriteStocks.remove(stock);
    }
}
