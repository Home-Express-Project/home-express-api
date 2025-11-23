package com.homeexpress.home_express_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homeexpress.home_express_api.entity.Province;

@Repository
public interface VnProvinceRepository extends JpaRepository<Province, String> {

    List<Province> findAllByOrderByNameAsc();

    Optional<Province> findFirstByNameContainingIgnoreCase(String name);
}
