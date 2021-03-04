package com.szcinda.express.dto;

import com.szcinda.express.FileType;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileDto implements Serializable {
    private String name;
    private String path;
    private String createTime;
    private FileType type;
}
