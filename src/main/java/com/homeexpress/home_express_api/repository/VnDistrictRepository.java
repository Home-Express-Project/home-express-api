package com.homeexpress.home_express_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homeexpress.home_express_api.entity.District;

@Repository
public interface VnDistrictRepository extends JpaRepository<District, String> {

    List<District> findByProvinceCodeOrderByNameAsc(String provinceCode);

    Optional<District> findFirstByNameContainingIgnoreCaseAndProvinceCode(String name, String provinceCode);
}
