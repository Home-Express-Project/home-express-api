package com.homeexpress.home_express_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homeexpress.home_express_api.entity.Ward;

@Repository
public interface VnWardRepository extends JpaRepository<Ward, String> {

    List<Ward> findByDistrictCodeOrderByNameAsc(String districtCode);

    Optional<Ward> findFirstByNameContainingIgnoreCaseAndDistrictCode(String name, String districtCode);
}
