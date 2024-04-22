package br.com.mineradorabr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Jacksonized
@Builder
@Data
@AllArgsConstructor
public class ProposalDTO {
    private Long proposalId;
    private String customer;
    private BigDecimal priceTonne;
}
