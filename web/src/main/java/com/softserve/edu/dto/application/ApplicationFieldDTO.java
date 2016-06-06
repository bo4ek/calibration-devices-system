package com.softserve.edu.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ApplicationFieldDTO {
    private Long id;
    private String designation;

    public ApplicationFieldDTO(Long id, String name) {
        this.id = id;
        this.designation = name;
    }
}
