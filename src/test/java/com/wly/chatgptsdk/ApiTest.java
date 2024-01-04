package com.wly.chatgptsdk;

import com.wly.chatgptsdk.common.Constants;
import com.wly.chatgptsdk.domain.chat.ChatCompletionRequest;
import com.wly.chatgptsdk.domain.chat.ChatCompletionResponse;
import com.wly.chatgptsdk.domain.chat.Message;
import com.wly.chatgptsdk.domain.qa.QACompletionResponse;
import com.wly.chatgptsdk.session.Configuration;
import com.wly.chatgptsdk.session.OpenAiSession;
import com.wly.chatgptsdk.session.OpenAiSessionFactory;
import com.wly.chatgptsdk.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * 单元测试
 */
@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        // 1. 配置文件 [联系小傅哥获取key]
        // 1.1 官网原始 apiHost https://api.openai.com/ - 官网的Key可直接使用
        // 1.2 三方公司 apiHost https://pro-share-aws-api.zcyai.com/ - 需要找我获得 Key 【支持3.5\4.0流式问答模型调用，有些模型已废弃不对接使用】
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://service-d6wuqy4n-1320869466.cd.apigw.tencentcs.com/");
        configuration.setApiKey("sk-FpBzSH9hJwk1FQQz21Be10DeD6B44e1c905eAaD3759923A7");
        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }


    /**
     * 此对话模型 4.0 接近于官网体验
     */
    @Test
    public void test_chat_completions() {
        // 1. 创建参数
        ChatCompletionRequest chatCompletion = ChatCompletionRequest
                .builder()
                .messages(Collections.singletonList(Message.builder().role(Constants.Role.USER).content("请给用python解答下面的问题：" +
                        "• Given two arrays that have the same values but in a different \n" +
                        "order and having no duplicate elements in it, we need to make \n" +
                        "a second array the same as a first array using the minimum \n" +
                        "number of swaps. \n" +
                        "• Design an algorithm to implement this function.").build()))

                .model(ChatCompletionRequest.Model.GPT_4.getCode())
                .build();
        // 2. 发起请求
        ChatCompletionResponse chatCompletionResponse = openAiSession.completions(chatCompletion);
        // 3. 解析结果
        chatCompletionResponse.getChoices().forEach(e -> {
            log.info("测试结果：{}", e.getMessage());
        });
    }
}
