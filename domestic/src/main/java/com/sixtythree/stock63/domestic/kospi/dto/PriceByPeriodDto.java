package com.sixtythree.stock63.domestic.kospi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Schema(description = "코스피 종목 정보")
@AllArgsConstructor
@Data
public class PriceByPeriodDto {
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
    @Schema(description = "전일대비율")
    private double prdyCtrt;


    public PriceByPeriodDto(Map map1){
        this.stckBsopDate = (String) map1.get("stck_bsop_date");
        this.stckClpr = (String) map1.get("stck_clpr");
        this.stckOprc = (String) map1.get("stck_oprc");
        this.stckHgpr = (String) map1.get("stck_hgpr");
        this.stckLwpr = (String) map1.get("stck_lwpr");
        this.acmlVol = (String) map1.get("acml_vol");
        this.acmlTrPbmn = (String) map1.get("acml_tr_pbmn");
        this.flngClsCode = (String) map1.get("flng_cls_code");
        this.prttRate = (String) map1.get("prtt_rate");
        this.modYn = (String) map1.get("mod_yn");
        this.prdyVrssSign = (String) map1.get("prdy_vrss_sign");
        this.prdyVrss = (String) map1.get("prdy_vrss");
        this.revlIssuReas = (String) map1.get("revl_issu_reas");
    }
}
