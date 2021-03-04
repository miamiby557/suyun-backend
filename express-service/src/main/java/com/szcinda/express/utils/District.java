package com.szcinda.express.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class District implements Serializable {
    private String value;
    private String label;
    private List<District> children;
}
