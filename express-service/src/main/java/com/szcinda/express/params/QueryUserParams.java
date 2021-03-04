package com.szcinda.express.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUserParams extends PageParams {
    private String account;
    private String name;
}
