package com.scaler.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Token extends BaseModel {

    //this is not JWT Token , it is more like session storage
    private String value;
    @ManyToOne
    private User user;
    private Date expiryAt;



}
