package com.wly.chatgptsdk.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 对话信息
 */
@Data
//官方返回的参数里面的choices多了logprobs，下面的注解作用是忽略未知属性
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatChoice implements Serializable {

    @JsonProperty("index")
    private long index;
    /** stream = true 请求参数里返回的属性是 delta */
    @JsonProperty("delta")
    private Message delta;
    /** stream = false 请求参数里返回的属性是 delta */
    @JsonProperty("message")
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;

}
