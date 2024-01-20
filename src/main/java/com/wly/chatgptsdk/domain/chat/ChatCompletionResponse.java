package com.wly.chatgptsdk.domain.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wly.chatgptsdk.domain.other.Usage;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 对话请求结果信息
 */

/**
 * 官方的请求对话结果响应结构体
 * {
 *   "choices": [
 *     {
 *       "finish_reason": "stop",
 *       "index": 0,
 *       "message": {
 *         "content": "The 2020 World Series was played in Texas at Globe Life Field in Arlington.",
 *         "role": "assistant"
 *       },
 *       "logprobs": null
 *     }
 *   ],
 *   "created": 1677664795,
 *   "id": "chatcmpl-7QyqpwdfhqwajicIEznoc6Q47XAyW",
 *   "model": "gpt-3.5-turbo-0613",
 *   "object": "chat.completion",
 *   "usage": {
 *     "completion_tokens": 17, 完成令牌
 *     "prompt_tokens": 57, 提示令牌
 *     "total_tokens": 74 总量令牌
 *   }
 * }
 */
@Data
public class ChatCompletionResponse implements Serializable{

    /**
     * ID
     */
    private String id;
    /**
     * 对象
     */
    private String object;
    /**
     * 模型
     */
    private String model;
    /**
     * 对话
     */
    private List<ChatChoice> choices;
    /**
     * 创建
     */
    private long created;
    /**
     * 耗材
     */
    private Usage usage;
    /**
     * 该指纹代表模型运行时使用的后端配置。
     * https://platform.openai.com/docs/api-reference/chat
     */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

}
