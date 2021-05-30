package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
//@Builder
@NoArgsConstructor
@ToString
public class StatusSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String status;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private List<SubCategory> subCategories;
}
