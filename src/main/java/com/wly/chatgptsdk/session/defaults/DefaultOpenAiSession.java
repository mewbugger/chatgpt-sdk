package com.wly.chatgptsdk.session.defaults;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wly.chatgptsdk.IOpenAiApi;
import com.wly.chatgptsdk.common.Constants;
import com.wly.chatgptsdk.domain.billing.BillingUsage;
import com.wly.chatgptsdk.domain.billing.Subscription;
import com.wly.chatgptsdk.domain.chat.ChatChoice;
import com.wly.chatgptsdk.domain.chat.ChatCompletionRequest;
import com.wly.chatgptsdk.domain.chat.ChatCompletionResponse;
import com.wly.chatgptsdk.domain.chat.Message;
import com.wly.chatgptsdk.domain.edits.EditRequest;
import com.wly.chatgptsdk.domain.edits.EditResponse;
import com.wly.chatgptsdk.domain.embedd.EmbeddingRequest;
import com.wly.chatgptsdk.domain.embedd.EmbeddingResponse;
import com.wly.chatgptsdk.domain.files.DeleteFileResponse;

import com.wly.chatgptsdk.domain.files.UploadFileResponse;
import com.wly.chatgptsdk.domain.images.ImageEditRequest;
import com.wly.chatgptsdk.domain.images.ImageRequest;
import com.wly.chatgptsdk.domain.images.ImageResponse;
import com.wly.chatgptsdk.domain.other.OpenAiResponse;
import com.wly.chatgptsdk.domain.qa.QACompletionRequest;
import com.wly.chatgptsdk.domain.qa.QACompletionResponse;
import com.wly.chatgptsdk.domain.whisper.TranscriptionsRequest;
import com.wly.chatgptsdk.domain.whisper.TranslationsRequest;
import com.wly.chatgptsdk.domain.whisper.WhisperResponse;
import com.wly.chatgptsdk.session.Configuration;
import com.wly.chatgptsdk.session.OpenAiSession;
import io.reactivex.Single;
import java.io.File;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;


import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;





public class DefaultOpenAiSession implements OpenAiSession {

    /**
     * 配置信息
     */
    private final Configuration configuration;

    /**
     * OpenAI 接口
     */
    private final IOpenAiApi openAiApi;
    /**
     * 工厂事件 定义用于创建事件源的工厂
     */
    private final EventSource.Factory factory;

    public DefaultOpenAiSession(Configuration configuration) {
        this.configuration = configuration;
        this.openAiApi = configuration.getOpenAiApi();
        this.factory = configuration.createRequestFactory();
    }

    /**
     * 同步处理QA完成请求
     * @param qaCompletionRequest 请求信息
     * @return
     */
    @Override
    public QACompletionResponse completions(QACompletionRequest qaCompletionRequest) {
        return this.openAiApi.completions(qaCompletionRequest).blockingGet();
    }

    /**
     * 处理QA完成请求并监听事件源
     * @param qaCompletionRequest 请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public EventSource completions(QACompletionRequest qaCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        // 核心参数校验；不对用户的传参做更改，只返回错误信息。
        if (!qaCompletionRequest.isStream()) {
            throw new RuntimeException("illegal parameter stream is false!");
        }

        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v1_completions))
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), new ObjectMapper().writeValueAsString(qaCompletionRequest)))
                .build();

        // 返回事件结果
        return factory.newEventSource(request, eventSourceListener);
    }

    /**
     * 简化的同步QA完成请求，只需要提供问题字符串
     * @param question 请求信息
     * @return
     */
    @Override
    public QACompletionResponse completions(String question) {
        QACompletionRequest request = QACompletionRequest
                .builder()
                .prompt(question) // 设置问题字符串
                .build();   // 构建QA完成请求
        Single<QACompletionResponse> completions = this.openAiApi.completions(request);
        /**
         * blockingGet()方法通常是与响应式编程库（如RxJava）中的Single、Observable或其他响应式类型一起使用的。
         * 在RxJava中，Single表示一个异步计算的结果，这个结果要么成功返回一个值，要么返回一个错误。
         * blockingGet()是Single类中的一个方法。当你调用这个方法时，它会阻塞当前线程，直到Single完成计算并发出一个结果或错误。
         * 如果Single成功完成，blockingGet()返回计算的结果；如果Single发出错误，blockingGet()将抛出一个异常。
         * blockingGet()的作用是将异步计算转换为同步计算。
         */
        return completions.blockingGet(); // 同步调用API并返回响应
    }

    /**
     *  同步处理聊天完成请求
     * @param chatCompletionRequest 请求信息
     * @return
     */
    @Override
    public ChatCompletionResponse completions(ChatCompletionRequest chatCompletionRequest) {
        return this.openAiApi.completions(chatCompletionRequest).blockingGet();
    }

    /**
     * 处理聊天完成请求并监听事件源
     * @param chatCompletionRequest 请求信息
     * @param eventSourceListener   实现监听；通过监听的 onEvent 方法接收数据
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public EventSource chatCompletions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        return chatCompletions(Constants.NULL, Constants.NULL, chatCompletionRequest, eventSourceListener);
    }

    /**
     * 使用CompletableFuture进行异步处理，返回值是聊天的完成响应
     * @param chatCompletionRequest 请求信息
     * @return
     * @throws InterruptedException
     * @throws JsonProcessingException
     */
    @Override
    public CompletableFuture<String> chatCompletions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException, JsonProcessingException {
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        // 用于构建最终返回的数据
        StringBuffer dataBuffer = new StringBuffer();

        // 调用同名方法，处理聊天完成请求
        chatCompletions(chatCompletionRequest, new EventSourceListener(){
            // 当事件发生时调用
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                // 检查是否完成聊天
                if ("[DONE]".equalsIgnoreCase(data)) {
                    onClosed(eventSource);
                    future.complete(dataBuffer.toString());
                }
                //解析聊天完成响应数据
                ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                // 遍历所有聊天选择
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    // 忽略助手角色的消息
                    if (Constants.Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 检查完成理由，如果为"stop"，则结束会话
                    String finishReason = chatChoice.getFinishReason();
                    if ("stop".equalsIgnoreCase(finishReason)) {
                        onClosed(eventSource);
                        return;
                    }

                    // 尝试将内容追加到dataBuffer
                    try {
                        dataBuffer.append(delta.getContent());
                    } catch (Exception e) {
                        future.completeExceptionally(new RuntimeException("Request closed before completion"));
                    }

                }
            }

            // 当事件源关闭时调用
            @Override
            public void onClosed(EventSource eventSource) {
                future.complete(dataBuffer.toString());
            }

            // 当事件源发生故障时调用
            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }

    // 负责创建和发送聊天完成请求，返回EventSource用于处理聊天流
    @Override
    public EventSource chatCompletions(String apiHostByUser, String apiKeyByUser, ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws JsonProcessingException {
        // 核心参数校验；不对用户的传参做更改，只返回错误信息。
        // 校验是否设置为流式请求
        if (!chatCompletionRequest.isStream()) {
            throw new RuntimeException("illegal parameter stream is false!");
        }

        // 动态设置 Host、Key，便于用户传递自己的信息
        String apiHost = Constants.NULL.equals(apiHostByUser) ? configuration.getApiHost() : apiHostByUser;
        String apiKey = Constants.NULL.equals(apiKeyByUser) ? configuration.getApiKey() : apiKeyByUser;

        // 构建请求到openai的请求信息
        Request request = new Request.Builder()
                // url: https://api.openai.com/v1/chat/completions - 通过 IOpenAiApi 配置的 POST 接口，用这样的方式从统一的地方获取配置信息
                .url(apiHost.concat(IOpenAiApi.v1_chat_completions))
                .addHeader("apiKey", apiKey)
                // 封装请求参数信息，如果使用了 Fastjson 也可以替换 ObjectMapper 转换对象
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), new ObjectMapper().writeValueAsString(chatCompletionRequest)))
                .build();

        // 返回结果信息；EventSource 对象可以取消应答
        return factory.newEventSource(request, eventSourceListener);
    }

    // 处理编辑请求，同步调用API并返回编辑响应
    @Override
    public EditResponse edit(EditRequest editRequest) {
        // 直接同步调用API接口的编辑方法，并获取结果
        return this.openAiApi.edits(editRequest).blockingGet();
    }

    // 根据文本提示生成图片
    @Override
    public ImageResponse genImages(String prompt) {
        // 构建图片请求
        ImageRequest imageRequest = ImageRequest.builder().prompt(prompt).build();
        // 调用重载方法发送图片生成请求并返回响应
        return this.genImages(imageRequest);
    }

    // 根据提供的图片请求生成图片
    @Override
    public ImageResponse genImages(ImageRequest imageRequest) {
        // 发送图片生成请求并等待结果
        return this.openAiApi.genImages(imageRequest).blockingGet();
    }

    // 编辑图片，给定图片文件和文本提示
    @Override
    public ImageResponse editImages(File image, String prompt) {
        // 构建图片编辑请求
        ImageEditRequest imageEditRequest = ImageEditRequest.builder().prompt(prompt).build();
        // 调用重载方法发送图片编辑请求并返回响应
        return this.editImages(image, null, imageEditRequest);
    }

    // 编辑图片，给定图片文件和图片编辑请求
    @Override
    public ImageResponse editImages(File image, ImageEditRequest imageEditRequest) {
        // 调用重载方法发送图片编辑请求并返回响应
        return this.editImages(image, null, imageEditRequest);
    }

    // 编辑图片，给定图片文件、遮罩文件和图片编辑请求
    @Override
    public ImageResponse editImages(File image, File mask, ImageEditRequest imageEditRequest) {
        // 1. imageMultipartBody
        // 创建图片文件的请求体
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(image));
        // 创建图片的multipart部分
        /**
         * multipart/form-data是一种编码类型，它允许你将表单数据分成多个部分（parts），每个部分可以包含不同类型的数据。
         * 这种编码类型通常用于表单提交，尤其是当表单包含文件上传时。
         * 每个部分都与表单的一个字段相对应，对于文件，它包含文件内容和文件名等信息。
         *
         * 对于图片上传，multipart部分是指将图片数据作为一个部分（part）包含在整个multipart/form-data编码的消息体中。在这个部分中，通常会包含：
         * 图片的二进制数据。
         * 与该图片相关的Content-Type头部，通常是image/jpeg、image/png等。
         * 与该图片相关的Content-Disposition头部，它描述了表单字段的名称和文件名。
         */
        MultipartBody.Part imageMultipartBody = MultipartBody.Part.createFormData("image", image.getName(), imageBody);
        // 2. maskMultipartBody
        // 初始化遮罩文件的Multipart部分为null
        MultipartBody.Part maskMultipartBody = null;
        // 如果提供了遮罩文件，则创建其请求体和multipart部分
        if (Objects.nonNull(mask)) {
            RequestBody maskBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mask));
            maskMultipartBody = MultipartBody.Part.createFormData("mask", mask.getName(), maskBody);
        }
        // requestBodyMap
        // 创建存放其他请求数据的map
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        // 添加文本提示到请求数据map
        requestBodyMap.put("prompt", RequestBody.create(MediaType.parse("multipart/form-data"), imageEditRequest.getPrompt()));
        // 添加图片数量到请求数据map
        requestBodyMap.put("n", RequestBody.create(MediaType.parse("multipart/form-data"), imageEditRequest.getN().toString()));
        // 添加图片大小到请求数据map
        requestBodyMap.put("size", RequestBody.create(MediaType.parse("multipart/form-data"), imageEditRequest.getSize()));
        // 添加响应格式到请求数据map
        requestBodyMap.put("response_format", RequestBody.create(MediaType.parse("multipart/form-data"), imageEditRequest.getResponseFormat()));
        // 如果用户信息提供，则添加到请求数据map
        if (!(Objects.isNull(imageEditRequest.getUser()) || "".equals(imageEditRequest.getUser()))) {
            requestBodyMap.put("user", RequestBody.create(MediaType.parse("multipart/form-data"), imageEditRequest.getUser()));
        }
        // 发送图片编辑请求并等待结果
        return this.openAiApi.editImages(imageMultipartBody, maskMultipartBody, requestBodyMap).blockingGet();
    }

    @Override
    public EmbeddingResponse embeddings(String input) {
        // 构建嵌入请求，将单个输入字符串包装在ArrayList中
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder().input(new ArrayList<String>() {{
            add(input); // 添加输入到列表
        }}).build();
        // 调用重载的embeddings方法处理嵌入请求并返回响应
        return this.embeddings(embeddingRequest);
    }

    @Override
    public EmbeddingResponse embeddings(String... inputs) {
        // 构建嵌入请求，使用输入参数数组构建列表
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder().input(Arrays.asList(inputs)).build();
        // 调用重载的embeddings方法处理嵌入请求并返回响应
        return this.embeddings(embeddingRequest);
    }

    @Override
    public EmbeddingResponse embeddings(List<String> inputs) {
        // 构建输入请求，直接使用输入列表
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder().input(inputs).build();
        // 调用重载的embeddings方法处理嵌入请求并返回响应
        return this.embeddings(embeddingRequest);
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest embeddingRequest) {
        // 调用API客户端的embeddings方法，传入嵌入请求，并使用blockingGet方法等待并获取结果
        return this.openAiApi.embeddings(embeddingRequest).blockingGet();
    }

    @Override
    public OpenAiResponse<File> files() {
        // 调用API客户端的files方法获取文件列表，并使用blockingGet等待并获取结果
        return this.openAiApi.files().blockingGet();
    }

    @Override
    public UploadFileResponse uploadFile(File file) {
        // 调用重载的uploadFile方法，传入默认的purpo和文件对象
        return this.uploadFile("fine-tune", file);
    }

    @Override
    public UploadFileResponse uploadFile(String purpose, File file) {
        // 创建文件的请求体，表示为multipart/form-data类型
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // 创建multipart部分，包含文件数据和文件名
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        // 创建表示用途的请求体，例如“fine-tune”
        RequestBody purposeBody = RequestBody.create(MediaType.parse("multipart/form-data"), purpose);
        // 调用API客户端的uploadFile方法上传文件，并使用blockingGet方法等待并获取结果
        return this.openAiApi.uploadFile(multipartBody, purposeBody).blockingGet();
    }

    @Override
    public DeleteFileResponse deleteFile(String fileId) {
        // 调用openAi API客户端的删除文件接口根据文件的id删除文件，并使用blockingGet方法等待并获取结果
        return this.openAiApi.deleteFile(fileId).blockingGet();
    }

    @Override
    public WhisperResponse speed2TextTranscriptions(File file, TranscriptionsRequest transcriptionsRequest) {
        // 1. 语音文件
        // 创建语音文件的请求体，表示为multipart/form-data类型
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // 创建multipart部分，包含文件数据和文件名
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        // 2. 参数封装
        // 创建存放其他请求的map
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        if (StrUtil.isNotBlank(transcriptionsRequest.getLanguage())) {
            // 检查并添加语言参数到请求数据map
            requestBodyMap.put(TranscriptionsRequest.Fields.language, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptionsRequest.getLanguage()));
        }
        if (StrUtil.isNotBlank(transcriptionsRequest.getModel())) {
            // 检查并添加模型参数到请求数据map
            requestBodyMap.put(TranscriptionsRequest.Fields.model, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptionsRequest.getModel()));
        }
        if (StrUtil.isNotBlank(transcriptionsRequest.getPrompt())) {
            // 检查并添加提示参数到请求数据map
            requestBodyMap.put(TranscriptionsRequest.Fields.prompt, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptionsRequest.getPrompt()));
        }
        if (StrUtil.isNotBlank(transcriptionsRequest.getResponseFormat())) {
            // 检查并添加响应格式参数到请求数据map
            requestBodyMap.put(TranscriptionsRequest.Fields.responseFormat, RequestBody.create(MediaType.parse("multipart/form-data"), transcriptionsRequest.getResponseFormat()));
        }
        // 添加温度参数到请求数据map
        requestBodyMap.put(TranscriptionsRequest.Fields.temperature, RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(transcriptionsRequest.getTemperature())));
        return this.openAiApi.speed2TextTranscriptions(multipartBody, requestBodyMap).blockingGet();
    }

    @Override
    public WhisperResponse speed2TextTranslations(File file, TranslationsRequest translationsRequest) {
        // 1. 语音文件
        // 创建语音文件的请求体，表示为multipart/form-data类型
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // 创建multipart部分，包含文件数据和文件名
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        // 2. 参数封装
        // 创建存放其他请求数据的map
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        // 检查并添加模型参数到请求数据map
        if (StrUtil.isNotBlank(translationsRequest.getModel())) {
            requestBodyMap.put(TranslationsRequest.Fields.model, RequestBody.create(MediaType.parse("multipart/form-data"), translationsRequest.getModel()));
        }
        // 检查并添加提示参数到请求数据map
        if (StrUtil.isNotBlank(translationsRequest.getPrompt())) {
            requestBodyMap.put(TranslationsRequest.Fields.prompt, RequestBody.create(MediaType.parse("multipart/form-data"), translationsRequest.getPrompt()));
        }
        // 检查并添加响应格式参数到请求数据map
        if (StrUtil.isNotBlank(translationsRequest.getResponseFormat())) {
            requestBodyMap.put(TranslationsRequest.Fields.responseFormat, RequestBody.create(MediaType.parse("multipart/form-data"), translationsRequest.getResponseFormat()));
        }
        // 添加温度参数到请求数据map，分别添加TranslationsRequest和TranscriptionsRequest的温度
        requestBodyMap.put(TranslationsRequest.Fields.temperature, RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(translationsRequest.getTemperature())));
        requestBodyMap.put(TranscriptionsRequest.Fields.temperature, RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(translationsRequest.getTemperature())));
        // 调用API客户端的speed2TextTranscriptions方法（此处名称可能有误，应该是对应翻译的API）发送请求，并使用blockingGet等待并获取结果
        return this.openAiApi.speed2TextTranscriptions(multipartBody, requestBodyMap).blockingGet();
    }

    @Override
    public Subscription subscription() {
        // 调用API客户端的subscription方法获取当前订阅信息，并使用blockingGet方法等待并获取结果
        return this.openAiApi.subscription().blockingGet();
    }

    @Override
    public BillingUsage billingUsage(@NotNull LocalDate starDate, @NotNull LocalDate endDate) {
        // 调用API客户端的billingUsage获取指定时间内的计费使用情况，并使用blockingGet方法等待并获取结果
        return this.openAiApi.billingUsage(starDate, endDate).blockingGet();
    }


}
