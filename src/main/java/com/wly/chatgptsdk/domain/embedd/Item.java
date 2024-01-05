package com.wly.chatgptsdk.domain.embedd;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 条目信息
 */
@Data
public class Item implements Serializable {
    private String object;
    private List<BigDecimal> embedding;
    private Integer index;

}
