package com.trading.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Integer id;

    @NotBlank(message = "商品名不能为空")
    private String productName;

    private String productImage;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能为负")
    private BigDecimal price;

    private String description;

    @NotNull(message = "类别不能为空")
    private Integer categoryId;

    private String condition;
}
