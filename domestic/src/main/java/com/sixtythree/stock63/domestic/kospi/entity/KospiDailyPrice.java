package com.sixtythree.stock63.domestic.kospi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "단축코드")
    private String mkscShrnIscd;
    @Schema(description = "주식영업일자")
    private String stckBsopDate;
    @Schema(description = "주식종가")
    private String stckClpr;
    @Schema(description = "주식시가")
    private String stckOprc;
    @Schema(description = "주식최고가")
    private String stckHgpr;
    @Schema(description = "주식최저가")
    private String stckLwpr;
    @Schema(description = "누적거래량")
    private String acmlVol;
    @Schema(description = "누적거래대금")
    private String acmlTrPbmn;
    @Schema(description = "락구분코드")
    private String flngClsCode;
    @Schema(description = "분할비율")
    private String prttRate;
    @Schema(description = "분할변경여부")
    private String modYn;
    @Schema(description = "전일대비부호")
    private String prdyVrssSign;
    @Schema(description = "전일대비")
    private String prdyVrss;
    @Schema(description = "재평가사유코드")
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
