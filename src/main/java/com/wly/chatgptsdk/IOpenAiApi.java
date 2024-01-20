package com.wly.chatgptsdk;

import com.wly.chatgptsdk.domain.billing.BillingUsage;
import com.wly.chatgptsdk.domain.billing.Subscription;
import com.wly.chatgptsdk.domain.chat.ChatCompletionRequest;
import com.wly.chatgptsdk.domain.chat.ChatCompletionResponse;
import com.wly.chatgptsdk.domain.edits.EditRequest;
import com.wly.chatgptsdk.domain.edits.EditResponse;
import com.wly.chatgptsdk.domain.embedd.EmbeddingRequest;
import com.wly.chatgptsdk.domain.embedd.EmbeddingResponse;
import com.wly.chatgptsdk.domain.files.DeleteFileResponse;
import com.wly.chatgptsdk.domain.files.UploadFileResponse;
import com.wly.chatgptsdk.domain.images.ImageRequest;
import com.wly.chatgptsdk.domain.images.ImageResponse;
import com.wly.chatgptsdk.domain.other.OpenAiResponse;
import com.wly.chatgptsdk.domain.qa.QACompletionRequest;
import com.wly.chatgptsdk.domain.qa.QACompletionResponse;
import com.wly.chatgptsdk.domain.whisper.WhisperResponse;
import io.reactivex.Single;
import java.io.File;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;


import java.time.LocalDate;
import java.util.Map;

/**
 * 定义访问接口，后续在此基础上进行扩展
 * 这个接口是没有对应的硬编码实现类的，它的存在只是定义标准，
 * 之后由 Retrofit 工具包进行创建服务，如：IOpenAiApi openAiApi = new Retrofit.Builder()
 * 可以把这想象成是对 DAO 接口与数据库的连接数据源之间的操作。
 *
 * Retrofit 使用动态代理来自动创建接口的实现。当你调用这个接口的方法时，Retrofit 会自动处理 HTTP 请求和响应。
 * Retrofit retrofit = new Retrofit.Builder()
 *     .baseUrl("https://api.openai.com/")
 *     .addConverterFactory(GsonConverterFactory.create())
 *     .build();
 *
 * IOpenAiApi openAiApi = retrofit.create(IOpenAiApi.class);
 * openAiApi.method();
 * 可以把这想象成是对 DAO 接口与数据库的连接数据源之间的操作。
 *      DAO 模式的关键点：
 *          接口定义：定义了一系列与数据源交互的方法。
 *          实现类：具体实现这些接口的类，处理与数据库的直接交互。
 *          数据源：数据库或其他数据存储。
 *      在使用 Retrofit 的场景中：
 *          IOpenAiApi 接口：定义了一系列与 OpenAI API 交互的方法，类似于 DAO 接口定义数据库操作。
 *          Retrofit 动态实现：Retrofit 动态创建 IOpenAiApi 接口的实现，这与手动编写 DAO 实现类的过程不同。在 Retrofit 中，这部分是自动完成的。
 *          远程 API 服务：在这个情况下，OpenAI API 是数据源，类似于数据库在 DAO 模式中的角色。
 */
public interface IOpenAiApi {

    String v1_completions = "v1/completions";

    /**
     * 文本问答
     *
     * @param qaCompletionRequest 请求信息
     * @return 应答结果
     */
    @POST(v1_completions)
    Single<QACompletionResponse> completions(@Body QACompletionRequest qaCompletionRequest);

    String v1_chat_completions = "v1/chat/completions";

    /**
     * 问答模型；默认 GPT-3.5
     * @param chatCompletionRequest 请求信息
     * @return 应答结果
     */
    @POST(v1_chat_completions)
    Single<ChatCompletionResponse> completions(@Body ChatCompletionRequest chatCompletionRequest);

    /**
     * 文本修复
     *
     * @param editRequest 请求信息；编辑文本的参数
     * @return 应答结果
     */
    @POST("v1/edits")
    Single<EditResponse> edits(@Body EditRequest editRequest);

    /**
     * 生成图片
     * curl https://api.openai.com/v1/images/generations \
     * -H "Content-Type: application/json" \
     * -H "Authorization: Bearer $OPENAI_API_KEY" \
     * -d '{
     * "prompt": "A cute baby sea otter",
     * "n": 2,
     * "size": "1024x1024"
     * }'
     * <p>
     * {
     * "created": 1589478378,
     * "data": [
     * {
     * "url": "https://..."
     * },
     * {
     * "url": "https://..."
     * }
     * ]
     * }
     *
     * @param imageRequest 图片对象
     * @return 应答结果
     */
    @POST("v1/images/generations")
    Single<ImageResponse> genImages(@Body ImageRequest imageRequest);

    /**
     * 修改图片
     * <p>
     * curl https://api.openai.com/v1/images/edits \
     * -H "Authorization: Bearer $OPENAI_API_KEY" \
     * -F image="@otter.png" \
     * -F mask="@mask.png" \
     * -F prompt="A cute baby sea otter wearing a beret" \
     * -F n=2 \
     * -F size="1024x1024"
     * <p>
     * {
     * "created": 1589478378,
     * "data": [
     * {
     * "url": "https://..."
     * },
     * {
     * "url": "https://..."
     * }
     * ]
     * }
     *
     * @param image          图片对象
     * @param mask           图片对象
     * @param requestBodyMap 请求参数
     * @return 应答结果
     */
    @Multipart
    @POST("v1/images/edits")
    Single<ImageResponse> editImages(@Part MultipartBody.Part image, @Part MultipartBody.Part mask, @PartMap Map<String, RequestBody> requestBodyMap);

    /**
     * 向量计算
     * curl https://api.openai.com/v1/images/variations \
     * -H "Authorization: Bearer $OPENAI_API_KEY" \
     * -F image="@otter.png" \
     * -F n=2 \
     * -F size="1024x1024"
     *
     * @param embeddingRequest 请求对象
     * @return 应答结果
     */
    @POST("v1/embeddings")
    Single<EmbeddingResponse> embeddings(@Body EmbeddingRequest embeddingRequest);

    /**
     * 文件列表；在你上传文件到服务端后，可以获取列表信息
     * curl https://api.openai.com/v1/files \
     * -H "Authorization: Bearer $OPENAI_API_KEY"
     *
     * @return 应答结果
     */
    @GET("v1/files")
    Single<OpenAiResponse<File>> files();

    /**
     * 上传文件；上载一个文件，该文件包含要在各种端点/功能中使用的文档。目前，一个组织上传的所有文件的大小最多可达1GB。如果您需要增加存储限制，请与官网联系。
     * curl https://api.openai.com/v1/files \
     * -H "Authorization: Bearer $OPENAI_API_KEY" \
     * -F purpose="fine-tune" \
     * -F file="@mydata.jsonl"
     *
     * @param file    文件
     * @param purpose "fine-tune"
     * @return 应答结果
     */
    @Multipart
    @POST("v1/files")
    Single<UploadFileResponse> uploadFile(@Part MultipartBody.Part file, @Part("purpose") RequestBody purpose);

    /**
     * 删除文件
     * curl https://api.openai.com/v1/files/file-XjGxS3KTG0uNmNOK362iJua3 \
     * -X DELETE \
     * -H "Authorization: Bearer $OPENAI_API_KEY"
     *
     * @param fileId 文件ID
     * @return 应答结果
     */
    @DELETE("v1/files/{file_id}")
    Single<DeleteFileResponse> deleteFile(@Path("file_id") String fileId);

    /**
     * 检索文件
     * curl https://api.openai.com/v1/files/file-XjGxS3KTG0uNmNOK362iJua3 \
     * -H "Authorization: Bearer $OPENAI_API_KEY"
     *
     * @param fileId 文件ID
     * @return 应答结果
     */
    @GET("v1/files/{file_id}")
    Single<File> retrieveFile(@Path("file_id") String fileId);

    /**
     * 检索文件内容信息
     * curl https://api.openai.com/v1/files/file-XjGxS3KTG0uNmNOK362iJua3/content \
     * -H "Authorization: Bearer $OPENAI_API_KEY" > file.jsonl
     *
     * @param fileId 文件ID
     * @return 应答结果
     */
    @Streaming
    @GET("v1/files/{file_id}/content")
    Single<ResponseBody> retrieveFileContent(@Path("file_id") String fileId);

    /**
     * 语音转文字
     * curl https://api.openai.com/v1/audio/transcriptions \
     * -H "Authorization: Bearer $OPENAI_API_KEY" \
     * -H "Content-Type: multipart/form-data" \
     * -F file="@/path/to/file/audio.mp3" \
     * -F model="whisper-1"
     *
     * @param file           语音文件
     * @param requestBodyMap 请求信息
     * @return 应答结果
     */
    @Multipart
    @POST("v1/audio/transcriptions")
    Single<WhisperResponse> speed2TextTranscriptions(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> requestBodyMap);

    /**
     * 语音翻译
     * curl https://api.openai.com/v1/audio/translations \
     * -H "Authorization: Bearer $OPENAI_API_KEY" \
     * -H "Content-Type: multipart/form-data" \
     * -F file="@/path/to/file/german.m4a" \
     * -F model="whisper-1"
     *
     * @param file           语音文件
     * @param requestBodyMap 请求信息
     * @return 应答结果
     */
    @Multipart
    @POST("v1/audio/translations")
    Single<WhisperResponse> speed2TextTranslations(@Part MultipartBody.Part file, @PartMap() Map<String, RequestBody> requestBodyMap);

    /**
     * 账单查询
     *
     * @return 应答结果
     */
    @GET("v1/dashboard/billing/subscription")
    Single<Subscription> subscription();

    /**
     * 消耗查询
     *
     * @param starDate 开始时间
     * @param endDate  结束时间
     * @return  应答数据
     */
    @GET("v1/dashboard/billing/usage")
    Single<BillingUsage> billingUsage(@Query("start_date") LocalDate starDate, @Query("end_date") LocalDate endDate);

}
