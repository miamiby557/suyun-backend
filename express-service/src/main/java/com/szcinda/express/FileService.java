package com.szcinda.express;

import com.szcinda.express.dto.CreatePathDto;
import com.szcinda.express.dto.FileCreateDto;
import com.szcinda.express.dto.FileDto;
import com.szcinda.express.params.QueryFileParams;

import java.util.List;

public interface FileService {
    List<FileDto> query(QueryFileParams params);

    void delete(CreatePathDto deleteFileDto);
    void deleteFile(CreatePathDto deleteFileDto);

    void uploadFile(FileCreateDto createDto) throws Exception;

    void createPath(CreatePathDto pathDto);
}
