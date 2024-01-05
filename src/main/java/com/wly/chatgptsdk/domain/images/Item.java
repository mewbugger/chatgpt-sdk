package com.wly.chatgptsdk.domain.images;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 条目
 */
@Data
public class Item {
    private String url;
    //    @JsonProperty("b64_json")
//    private String b64Json;
    @JsonProperty("revised_prompt")
    private String revisedPrompt;

}
