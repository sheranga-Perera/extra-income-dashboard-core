package com.phx.ei.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdSummaryResponse {
    private long pending;
    private long approved;
    private long rejected;
    private long active;
}
