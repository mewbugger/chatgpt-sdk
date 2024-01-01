package com.wly.chatgptsdk.domain.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 对话信息
 */
@Data
public class Choice {

    private long index;
    private String text;
    private Object logprobs;
    @JsonProperty("finish_reason")
    private String finishReason;

}
