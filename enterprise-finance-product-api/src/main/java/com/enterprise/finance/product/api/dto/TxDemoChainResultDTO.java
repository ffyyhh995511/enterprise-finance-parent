package com.enterprise.finance.product.api.dto;

import com.enterprise.finance.common.dto.TxDemoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxDemoChainResultDTO {

    private String tag;
    private List<TxDemoDTO> userRows;
    private List<TxDemoDTO> orderRows;
    private List<TxDemoDTO> productRows;
    private boolean rolledBack;
}
