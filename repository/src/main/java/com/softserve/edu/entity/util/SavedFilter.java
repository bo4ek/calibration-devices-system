package com.softserve.edu.entity.util;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.softserve.edu.entity.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "SAVED_FILTER")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SavedFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long savedFilterId;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonManagedReference
    private User user;

    private String locationUrl;
    private String filter;
    private String name;

    public SavedFilter(User user,String locationUrl,String filter,String name){
        this.user=user;
        this.locationUrl=locationUrl;
        this.filter=filter;
        this.name=name;
    }

}
