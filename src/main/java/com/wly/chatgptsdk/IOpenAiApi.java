package com.wly.chatgptsdk;

import com.wly.chatgptsdk.domain.chat.ChatCompletionRequest;
import com.wly.chatgptsdk.domain.chat.ChatCompletionResponse;
import com.wly.chatgptsdk.domain.qa.QACompletionRequest;
import com.wly.chatgptsdk.domain.qa.QACompletionResponse;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 定义访问接口，后续在此基础上进行扩展
 * 这个接口是没有对应的硬编码实现类的，它的存在只是定义标准，
 * 之后由 Retrofit 工具包进行创建服务，如：IOpenAiApi openAiApi = new Retrofit.Builder()
 * 可以把这想象成是对 DAO 接口与数据库的连接数据源之间的操作。
 */
public interface IOpenAiApi {

    String v1_completions = "v1/completions";

    String v1_chat_completions = "v1/chat/completions";



    /**
     * 文本问答
     * @param qaCompletionRequest 请求信息
     * @return                    返回结果
     */
    @POST("v1/completions")
    Single<QACompletionResponse> completions(@Body QACompletionRequest qaCompletionRequest);

    /**
     * 默认 GPT-3.5 问答模型
     * @param chatCompletionRequest 请求信息
     * @return                      返回结果
     */
    @POST("v1/chat/completions")
    Single<ChatCompletionResponse> completions(@Body ChatCompletionRequest chatCompletionRequest);
}
