package com.learnmicro.bookservice.command.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestModel {
    private String id;

    @NotBlank(message = "Name can not blank")
    @Size(min =  2, max = 30, message = "name length must be between 2 and 30 characters")
    private String name;

    @NotBlank(message = "Author can not blank")
    private String author;
    private Boolean isReady;
}
