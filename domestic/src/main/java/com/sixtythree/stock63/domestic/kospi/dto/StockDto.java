package com.sixtythree.stock63.domestic.kospi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "코스피 종목 정보")
@AllArgsConstructor
@Data
public class StockDto {
    @Schema(description = "단축코드")
    private String mkscShrnIscd;
    @Schema(description = "한글명")
    private String htsKorIsnm;
    @Schema(description = "전일종가")
    private int stckClpr;
    @Schema(description = "시가총액")
    private String prdyAvlsScal;
    @Schema(description = "전일대비율")
    private double prdyCtrt;
    @Schema(description = "기간대비율")
    private double totalCtrt;
}
