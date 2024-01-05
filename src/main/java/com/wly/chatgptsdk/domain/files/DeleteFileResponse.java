package com.wly.chatgptsdk.domain.files;

import java.io.Serializable;

/**
 * 删除文件应答
 * {
 *   "id": "file-XjGxS3KTG0uNmNOK362iJua3",
 *   "object": "file",
 *   "deleted": true
 * }
 */
public class DeleteFileResponse implements Serializable {
    /** 文件ID */
    private String id;
    /** 对象；file */
    private String object;
    /** 删除；true */
    private boolean deleted;

}
