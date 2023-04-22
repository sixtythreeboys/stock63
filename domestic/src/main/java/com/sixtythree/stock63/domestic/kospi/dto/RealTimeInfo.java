package com.sixtythree.stock63.domestic.kospi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "코스피 종목 정보")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RealTimeInfo {
    private String mkscShrnIscd;
    private String stckCntgHour;
    private String stckPrpr;
    private String prdyVrssSign;
    private String prdyCtrt;
    private String wghnAvrgStckPrc;
    private String stckOprc;
    private String stckHgpr;
    private String stckLwpr;
    private String askp1;
    private String bidp1;
    private String cntgVol;
    private String acmlVol;
    private String acmlTrPbmn;
    private String selnCntgCsnu;
    private String shnuCntgCsnu;

}
