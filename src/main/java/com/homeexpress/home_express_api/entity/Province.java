package com.homeexpress.home_express_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vn_provinces")
public class Province {

    @Id
    @Column(name = "province_code", length = 6)
    private String code;

    @Column(name = "province_name", nullable = false)
    private String name;

    @Column(name = "province_name_en")
    private String nameEn;

    @Column(name = "region")
    private String region;

    @Column(name = "display_order")
    private Integer displayOrder;

    public Province() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
