package com.szcinda.express.dto;

import com.szcinda.express.FileType;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreatePathDto implements Serializable {
    private String name;//目录名称
    private String path;
    private FileType type;
}
