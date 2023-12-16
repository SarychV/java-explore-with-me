package ru.practicum.ewm.categories.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
public class CategoryDto {
    private long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
