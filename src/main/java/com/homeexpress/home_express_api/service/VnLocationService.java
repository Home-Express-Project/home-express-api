package com.homeexpress.home_express_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.homeexpress.home_express_api.dto.location.DistrictDto;
import com.homeexpress.home_express_api.dto.location.ProvinceDto;
import com.homeexpress.home_express_api.dto.location.WardDto;
import com.homeexpress.home_express_api.exception.ResourceNotFoundException;
import com.homeexpress.home_express_api.repository.VnDistrictRepository;
import com.homeexpress.home_express_api.repository.VnProvinceRepository;
import com.homeexpress.home_express_api.repository.VnWardRepository;

@Service
public class VnLocationService {

    private final VnProvinceRepository provinceRepository;
    private final VnDistrictRepository districtRepository;
    private final VnWardRepository wardRepository;

    public VnLocationService(
            VnProvinceRepository provinceRepository,
            VnDistrictRepository districtRepository,
            VnWardRepository wardRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    public List<ProvinceDto> getAllProvinces() {
        return provinceRepository.findAllByOrderByNameAsc()
                .stream()
                .map(province -> new ProvinceDto(
                        province.getCode(),
                        province.getName()))
                .collect(Collectors.toList());
    }

    public List<DistrictDto> getDistrictsByProvince(String provinceCode) {
        String trimmedCode = normalizeCode(provinceCode);
        if (!provinceRepository.existsById(trimmedCode)) {
            throw new ResourceNotFoundException("Tỉnh/Thành phố", "mã", trimmedCode);
        }

        return districtRepository.findByProvinceCodeOrderByNameAsc(trimmedCode)
                .stream()
                .map(district -> new DistrictDto(
                        district.getCode(),
                        district.getName(),
                        district.getProvinceCode()))
                .collect(Collectors.toList());
    }

    public List<WardDto> getWardsByDistrict(String districtCode) {
        String trimmedCode = normalizeCode(districtCode);
        if (!districtRepository.existsById(trimmedCode)) {
            throw new ResourceNotFoundException("Quận/Huyện", "mã", trimmedCode);
        }

        return wardRepository.findByDistrictCodeOrderByNameAsc(trimmedCode)
                .stream()
                .map(ward -> new WardDto(
                        ward.getCode(),
                        ward.getName(),
                        ward.getDistrictCode()))
                .collect(Collectors.toList());
    }

    private String normalizeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ResourceNotFoundException("Mã địa lý", "code", code);
        }
        return code.trim();
    }
}
