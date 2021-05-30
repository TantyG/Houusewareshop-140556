package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;


@Data
//@Builder
@NoArgsConstructor
@ToString
public class Cart {
    private Long productId;
    private String productcode;
    private String productname;
    private int productquantity;
    private double productprice;
    private String productdescription;
    private String productimageUrl;

    private int quantity;


}
