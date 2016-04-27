package com.softserve.edu.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RejectedInfoDTO {
    private Long id;
    private String name;

    public RejectedInfoDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
