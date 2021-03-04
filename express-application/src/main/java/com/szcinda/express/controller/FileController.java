package com.szcinda.express.controller;

import com.szcinda.express.FileService;
import com.szcinda.express.FileType;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.CreatePathDto;
import com.szcinda.express.dto.FileCreateDto;
import com.szcinda.express.dto.FileDto;
import com.szcinda.express.params.QueryFileParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${share.file.path}")
    private String absDirectory;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @UserLoginToken
    @PostMapping("/searchPath")
    public List<FileDto> searchPath(@RequestBody QueryFileParams filePathDto) {
        if ("根目录".equals(filePathDto.getPath())) {
            filePathDto.setPath(absDirectory);
        } else {
            String path = filePathDto.getPath();
            path = path.replace("根目录", "");
            filePathDto.setPath(absDirectory + File.separator + path);
        }
        return fileService.query(filePathDto);
    }

    @UserLoginToken
    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws Exception {
        String name = file.getOriginalFilename();
        FileCreateDto createDto = new FileCreateDto();
        createDto.setName(name);
        createDto.setInputStream(file.getInputStream());
        createDto.setType(FileType.FILE);
        path = path.replace("根目录", "");
        createDto.setPath(absDirectory + File.separator + path);
        fileService.uploadFile(createDto);
        return Result.success();
    }

    @UserLoginToken
    @PostMapping("/deletePath")
    public Result deletePath(@RequestBody CreatePathDto deleteDto) {
        fileService.delete(deleteDto);
        return Result.success();
    }

    @UserLoginToken
    @PostMapping("/deleteFile")
    public Result deleteFile(@RequestBody CreatePathDto deleteDto) {
        fileService.deleteFile(deleteDto);
        return Result.success();
    }

    @UserLoginToken
    @PostMapping("/createPath")
    public Result createPath(@RequestBody CreatePathDto deleteDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream inputStream = request.getInputStream();
        if (inputStream != null) {
            inputStream.close();
        }
        OutputStream os = response.getOutputStream();
        if(os!=null){
            os.close();
        }
        fileService.createPath(deleteDto);
        return Result.success();
    }

    @PostMapping("/download")
    public void download(@RequestParam String path, @RequestParam String name, HttpServletResponse response) throws Exception {
        File file = new File(absDirectory + File.separator + path);
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(name, "UTF-8"));
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
