package com.szcinda.express;

import com.szcinda.express.dto.CreatePathDto;
import com.szcinda.express.dto.FileCreateDto;
import com.szcinda.express.dto.FileDto;
import com.szcinda.express.params.QueryFileParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FileServiceImpl implements FileService {
    @Value("${share.file.path}")
    private String absDirectory;
    @Value("${bak.file.path}")
    private String bakFilePath;

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
    private DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileServiceImpl() {
    }

    @Override
    public List<FileDto> query(QueryFileParams params) {
        List<FileDto> fileDtos = new ArrayList<>();
        File file = new File(params.getPath());
        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
        if (files != null && files.length > 0) {
            // 遍历，目录下的所有文件
            for (File f : files) {
                FileDto fileDto = new FileDto();
                fileDto.setName(f.getName());
                try {
                    Path path = Paths.get(f.getAbsolutePath());
                    BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
                    BasicFileAttributes attr = basicview.readAttributes();
                    long createTimeLong = attr.creationTime().toMillis();
                    LocalDateTime instantToLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createTimeLong), ZoneId.systemDefault());
                    fileDto.setCreateTime(df2.format(instantToLocalDateTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    long modifyTimeLong = file.lastModified();
                    LocalDateTime instantToLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(modifyTimeLong), ZoneId.systemDefault());
                    fileDto.setCreateTime(df2.format(instantToLocalDateTime));
                }
                fileDto.setPath(f.getAbsolutePath().replace(absDirectory, ""));
                if (f.isFile()) {
                    fileDto.setType(FileType.FILE);
                } else if (f.isDirectory()) {
                    fileDto.setType(FileType.DIRECTORY);
                }
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    @Override
    public void delete(CreatePathDto deleteFileDto) {
        String absPath = absDirectory + java.io.File.separator + deleteFileDto.getPath();
        java.io.File ioFile = new java.io.File(absPath);
        if (FileType.DIRECTORY.equals(deleteFileDto.getType())) {
            java.io.File[] fileList = ioFile.listFiles();
            Assert.isTrue(fileList == null || fileList.length == 0, String.format("此[%s]目录下存在文件,不能删除", deleteFileDto.getName()));
            ioFile.delete();
        } else {
            ioFile.delete();
        }
    }

    @Override
    public void deleteFile(CreatePathDto deleteFileDto) {
        String absPath = absDirectory + java.io.File.separator + deleteFileDto.getPath();
        java.io.File ioFile = new java.io.File(absPath);
        if (ioFile.exists()) {
            ioFile.delete();
        }
    }

    @Override
    public void uploadFile(FileCreateDto createDto) throws Exception {
        // 如果出现同名文件,需要备份原来文件,以当天日期时分秒为基础
        String absFilePath = createDto.getPath() + java.io.File.separator + createDto.getName();
        java.io.File existFile = new java.io.File(absFilePath);
        if (existFile.exists()) {
            String time = df.format(LocalDateTime.now());
            String name = existFile.getName();
            String[] strings = name.split("\\.");
            String newFileName = strings[0] + "-" + time + "." + strings[1];
            String newFilePath = bakFilePath + java.io.File.separator + newFileName;
            java.io.File bakFile = new java.io.File(newFilePath);
            copyFile(existFile, bakFile);
            existFile.delete();
        }
        // 复制文件流到文件
        OutputStream outStream = new FileOutputStream(absFilePath);
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = createDto.getInputStream().read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
    }

    private void copyFile(java.io.File source, java.io.File dest) throws Exception {
        Files.copy(source.toPath(), dest.toPath());
    }

    @Override
    public void createPath(CreatePathDto pathDto) {
        String path = absDirectory + java.io.File.separator + pathDto.getPath() + File.separator + pathDto.getName();
        path = path.replace("根目录", "");
        java.io.File filePath = new java.io.File(path);
        Assert.isTrue(!filePath.exists(), String.format("此[%s]目录已经存在", pathDto.getPath() + File.separator + pathDto.getName()));
        filePath.mkdir();
    }
}
