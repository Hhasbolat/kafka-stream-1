package com.moneypay.KStream.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransaction {
    private Long bankCode;
    private int totalItems;
    private int successItems;
    private Double successRate;
}
