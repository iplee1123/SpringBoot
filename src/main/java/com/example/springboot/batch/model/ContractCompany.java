package com.example.springboot.batch.model;

import lombok.*;

import java.io.Serializable;

@Data
public class ContractCompany implements Serializable {

    private String houseCode;

    private String vendorCode;

    private String addDate;

    private String addTime;

    private String formType;

    private String formSeq;

    private String agreeType;

    private String agreeDate;

    private String agreeTime;

    private String signValue;

    private String signType;

    private String writerId;

    private String formContent;

    private String nameLoc;

    private String signValueClob;

    private String sysId;

    private String formName;

    private String description;
}
