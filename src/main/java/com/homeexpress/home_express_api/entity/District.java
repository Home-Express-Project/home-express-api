package com.homeexpress.home_express_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vn_districts")
public class District {

    @Id
    @Column(name = "district_code", length = 6)
    private String code;

    @Column(name = "district_name", nullable = false)
    private String name;

    @Column(name = "province_code", length = 6)
    private String provinceCode;

    public District() {
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

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
