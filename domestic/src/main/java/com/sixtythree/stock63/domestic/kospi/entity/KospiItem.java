package com.sixtythree.stock63.domestic.kospi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class KospiItem {
    @Id
    @Schema(description = "단축코드")
    private String mkscShrnIscd;
    @Schema(description = "표준코드")
    private String stndIscd;
    @Schema(description = "한글명")
    private String htsKorIsnm;
    @Schema(description = "그룹코드")
    private String scrtGrpClsCode;
    @Schema(description = "한글명")
    private String avlsScalClsCode;
    @Schema(description = "지수업종대분류")
    private String bstpLargDivCode;
    @Schema(description = "지수업종중분류")
    private String bstpMedmDivCode;
    @Schema(description = "지수업종소분류")
    private String bstpSmalDivCode;
    private String mninClsCodeYn;
    private String lowCurrentYn;
    private String sprnStrrNmixIssuYn;
    @Column(name="kospi200_apnt_cls_code")
    private String kospi200ApntClsCode;
    @Column(name="kospi100_issu_yn")
    private String kospi100IssuYn;
    @Column(name="kospi50_issu_yn")
    private String kospi50IssuYn;
    private String krxIssuYn;
    private String etpProdClsCode;
    private String elwPblcYn;
    @Column(name="krx100_issu_yn")
    private String krx100IssuYn;
    private String krxCarYn;
    private String krxSmcnYn;
    private String krxBioYn;
    private String krxBankYn;
    private String etprUndtObjtCoYn;
    private String KrxEnrgChmsYn;
    private String krxStelYn;
    private String shortOverClsCode;
    private String krxMediCmncYn;
    private String krxCnstYn;
    private String krxFnncSvcYn;
    private String krxScrtYn;
    private String krxShipYn;
    private String krxInsuYn;
    private String krxTrnpYn;
    private String sriNmixYn;
    @Schema(description = "기준가")
    private String stckSdpr;
    @Schema(description = "매매수량단위")
    private String frmlMrktDealQtyUnit;
    @Schema(description = "시간외수량단위")
    private String ovtmMrktDealQtyUnit;
    private String trhtYn;
    private String sltrYn;
    private String mangIssuYn;
    private String mrktAlrmClsCode;
    private String mrktAlrmRiskAdntYn;
    private String insnPbntYn;
    private String bypsLstnYn;
    private String flngClsCode;
    private String fcamModClsCode;
    private String icicClsCode;
    private String margRate;
    private String crdtAble;
    private String crdtDays;
    @Schema(description = "전일거래량")
    private String prdyVol;
    @Schema(description = "액면가")
    private String stckFcam;
    @Schema(description = "상장일자")
    private String stckLstnDate;
    @Schema(description = "상장주수")
    private String lstnStcn;
    @Schema(description = "자본금")
    private String cpfn;
    private String stacMonth;
    private String poPrc;
    private String prstClsCode;
    private String sstsHotYn;
    private String stangeRunupYn;
    @Column(name="krx300_issu_yn")
    private String krx300IssuYn;
    private String kospiIssuYn;
    @Schema(description = "매출액")
    private String saleAccount;
    @Schema(description = "영업이익")
    private String bsopPrfi;
    @Schema(description = "경상이익")
    private String opPrfi;
    @Schema(description = "단기순이익")
    private String thtrNtin;
    private String roe;
    private String baseDate;
    @Schema(description = "시가총액")
    private String prdyAvlsScal;
    private String grpCode;
    private String coCrdtLimtOverYn;
    private String secuLendAbleYn;
    private String stlnAbleYn;
}