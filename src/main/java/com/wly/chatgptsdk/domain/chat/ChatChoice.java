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
    /** stream = false 请求参数里返回的属性是 message */
    @JsonProperty("message")
    private Message message;
    /**
     * finish_reason可能的返回值：
     *  1.stop：API返回了完整的信息，或者一个信息被stop提供的终止序列之一停止
     *  2.length：不完整模型输出由于max_tokens参数的限制或者token限制
     *  3.function_call:模型决定调用一个函数
     *  4.content_filter:由于内容过滤器的标记而省略了内容
     *  5.null:API响应还在过程中或者没有完成
     */
    @JsonProperty("finish_reason")
    private String finishReason;

}
