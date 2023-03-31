package com.sixtythree.stock63.domestic.kospi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Entity
@NoArgsConstructor
public class KospiDailyPrice {
    @Id
    @GeneratedValue
    private int idKospiDailyPrice;
    private String mkscShrnIscd;
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

    public KospiDailyPrice(Map map){
        this.stckBsopDate = (String) map.get("stck_bsop_date");
        this.stckClpr = (String) map.get("stck_clpr");
        this.stckOprc = (String) map.get("stck_oprc");
        this.stckHgpr = (String) map.get("stck_hgpr");
        this.stckLwpr = (String) map.get("stck_lwpr");
        this.acmlVol = (String) map.get("acml_vol");
        this.acmlTrPbmn = (String) map.get("acml_tr_pbmn");
        this.flngClsCode = (String) map.get("flng_cls_code");
        this.prttRate = (String) map.get("prtt_rate");
        this.modYn = (String) map.get("mod_yn");
        this.prdyVrssSign = (String) map.get("prdy_vrss_sign");
        this.prdyVrss = (String) map.get("prdy_vrss");
        this.revlIssuReas = (String) map.get("revl_issu_reas");
    }
}
