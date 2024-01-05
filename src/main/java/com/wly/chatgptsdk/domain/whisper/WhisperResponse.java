package com.wly.chatgptsdk.domain.whisper;

import lombok.Data;

import java.io.Serializable;

@Data
public class WhisperResponse implements Serializable {
    private String text;
}
