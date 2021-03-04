package com.szcinda.express.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryFileParams extends PageParams {
    private String path;
}
