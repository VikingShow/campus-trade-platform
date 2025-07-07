package com.campus.trade.service.impl;

import com.campus.trade.exception.CustomException;
import com.campus.trade.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    /**
     * 构造函数支持通过配置文件动态指定上传目录，兼容本地和生产环境。
     */
    public FileStorageServiceImpl(@Value("${file.upload-dir:uploads}") String uploadDir) {
        // 支持相对路径和绝对路径
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new CustomException("无法创建用于存储上传文件的目录！ " + ex.getMessage());
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // 清理并获取原始文件名
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new CustomException("无法存储空文件。");
        }
        // 防止路径遍历攻击
        if (originalFileName.contains("..")) {
            throw new CustomException("文件名包含无效的路径序列 " + originalFileName);
        }

        try {
            // 获取文件扩展名
            String fileExtension = "";
            int lastDot = originalFileName.lastIndexOf('.');
            if (lastDot >= 0) {
                fileExtension = originalFileName.substring(lastDot);
            }
            // 使用UUID生成唯一文件名，防止重名
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // 确定最终保存的目标路径
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);

            // 使用 try-with-resources 确保输入流被自动关闭
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // 返回一个可以通过Web访问的相对URL路径
            return "/uploads/" + newFileName;

        } catch (IOException ex) {
            throw new CustomException("无法存储文件 " + originalFileName + "。请重试！错误: " + ex.getMessage());
        }
    }
}
