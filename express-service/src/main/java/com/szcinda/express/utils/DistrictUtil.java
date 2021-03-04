package com.szcinda.express.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Component
public class DistrictUtil {

    @Value("${distrct.file.path}")
    private String districtFilePath;

    private final ObjectMapper objectMapper;

    public DistrictUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<District> convertDistrict() {
        try {
            InputStream input = new FileInputStream(districtFilePath);
            return objectMapper.readValue(input, new TypeReference<List<District>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
