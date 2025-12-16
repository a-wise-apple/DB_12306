package com.example.railway.web.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateListingRequest {
    private Integer orderId;
    private BigDecimal price;
}
