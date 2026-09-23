package com.ecom.product_service.dto;

public class SearchSuggestion {

    private String type;
    private String text;

    public SearchSuggestion() {
    }

    public SearchSuggestion(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}