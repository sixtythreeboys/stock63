package com.sixtythree.stock63.domestic.kospi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class KospiItem {
    private String mkscShrnIscd;
    @Id
    private String stndIscd;
    private String htsKorIsnm;
    private String scrtGrpClsCode;
    private String avlsScalClsCode;
    private String bstpLargDivCode;
    private String bstpMedmDivCode;
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
    private String krxMediCmncY;
    private String nkrxCnstYn;
    private String krxFnncSvcYn;
    private String krxScrtYn;
    private String krxShipYn;
    private String krxInsuYn;
    private String krxTrnpYn;
    private String sriNmixYn;
    private String stckSdpr;
    private String frmlMrktDealQtyUnit;
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
    private String prdyVol;
    private String stckFcam;
    private String stckLstnDate;
    private String lstnStcn;
    private String cpfn;
    private String stacMonth;
    private String poPrc;
    private String prstClsCode;
    private String sstsHotYn;
    private String stangeRunupYn;
    private String krx300IssuYn;
    private String kospiIssuYn;
    private String saleAccount;
    private String bsopPrfi;
    private String opPrfi;
    private String thtrNtin;
    private String roe;
    private String baseDate;
    private String prdyAvlsScal;
    private String grpCode;
    private String coCrdtLimtOverYn;
    private String secuLendAbleYn;
    private String stlnAbleYn;
}