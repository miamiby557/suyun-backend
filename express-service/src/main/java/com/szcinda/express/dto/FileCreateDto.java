package com.szcinda.express.dto;

import com.szcinda.express.FileType;
import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

@Data
public class FileCreateDto implements Serializable {
    private String name;
    private String path;//路径
    private FileType type;
    private InputStream inputStream;// 文件流
}
