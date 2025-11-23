package com.homeexpress.home_express_api.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AssignTransportRequest {

    @NotNull(message = "transport_id is required")
    @JsonProperty("transport_id")
    private Long transportId;

    @JsonProperty("estimated_price")
    private BigDecimal estimatedPrice;

    public Long getTransportId() {
        return transportId;
    }

    public void setTransportId(Long transportId) {
        this.transportId = transportId;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
