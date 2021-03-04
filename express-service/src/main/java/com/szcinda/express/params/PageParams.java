package com.szcinda.express.params;

import lombok.Data;

import java.io.Serializable;

@Data
class PageParams implements Serializable {
    private int page = 1;
    private int pageSize = 20;
    private String sort;
}
