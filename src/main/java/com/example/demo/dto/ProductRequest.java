package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private Double price;

    public ProductRequest() {
    }

    public ProductRequest(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}