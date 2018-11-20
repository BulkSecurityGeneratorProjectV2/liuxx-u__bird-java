package com.bird.web.file.upload;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传结果
 *
 * @author liuxx
 * @date 2018/4/25
 */
@Getter
@Setter
public class UploadResult implements Serializable {
    private boolean success;
    private String message;
    private String path;
    private List<String> paths;

    public UploadResult(){
        paths = new ArrayList<>();
    }

    public UploadResult(boolean success,String message,String path) {
        this();

        this.success = success;
        this.message = message;
        this.path = path;
    }

    public static UploadResult success(String path){
        return new UploadResult(true,"上传成功",path);
    }

    public static UploadResult fail(String message){
        return new UploadResult(false,message,null);
    }
}
