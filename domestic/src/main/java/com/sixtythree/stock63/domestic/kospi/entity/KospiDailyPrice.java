package com.sixtythree.stock63.domestic.kospi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class KospiDailyPrice {
    @Id
    @GeneratedValue
    private int idKospiDailyPrice;
    private String stckBsopDate;
    private String stckClpr;
    private String stckOprc;
    private String stckHgpr;
    private String stckLwpr;
    private String acmlVol;
    private String acmlTrPbmn;
    private String flngClsCode;
    private String prttRate;
    private String modYn;
    private String prdyVrssSign;
    private String prdyVrss;
    private String revlIssuReas;
}
