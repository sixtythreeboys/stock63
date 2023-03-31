package com.sixtythree.stock63.domestic.kospi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Token {
    @Id
    private String tokenName;
    @Column(length = 500)
    private String value;
    private Date expired;
}
