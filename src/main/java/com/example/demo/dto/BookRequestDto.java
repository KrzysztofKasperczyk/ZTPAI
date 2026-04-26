package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookRequestDto {

    @NotBlank(message = "Tytuł jest wymagany")
    @Size(min = 1, max = 255, message = "Tytuł musi mieć od 1 do 255 znaków")
    private String title;

    @NotBlank(message = "Autor jest wymagany")
    private String author;

    @NotBlank(message = "ISBN jest wymagany")
    @Pattern(regexp = "^[0-9]{10}([0-9]{3})?$", message = "ISBN musi mieć 10 lub 13 cyfr")
    private String isbn;

    @Min(value = 1000, message = "Rok musi być większy niż 1000")
    @Max(value = 2100, message = "Rok musi być mniejszy niż 2100")
    private Integer year;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musi być większa niż 0")
    private Double price;
}