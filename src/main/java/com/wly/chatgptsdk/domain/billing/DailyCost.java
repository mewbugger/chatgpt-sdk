package com.wly.chatgptsdk.domain.billing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 *  金额消耗
 */
public class DailyCost {
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("line_items")
    private List<LineItem> lineItems;

}
