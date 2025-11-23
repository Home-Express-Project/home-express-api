package com.homeexpress.home_express_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vn_wards")
public class Ward {

    @Id
    @Column(name = "ward_code", length = 6)
    private String code;

    @Column(name = "ward_name", nullable = false)
    private String name;

    @Column(name = "district_code", length = 6)
    private String districtCode;

    public Ward() {
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

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }
}
