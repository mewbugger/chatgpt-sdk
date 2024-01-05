package com.wly.chatgptsdk.domain.embedd;

import com.wly.chatgptsdk.domain.other.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 反馈对象
 */
@Data
public class EmbeddingResponse implements Serializable {
    private String object;
    private List<Item> data;
    private String model;
    private Usage usage;
}
