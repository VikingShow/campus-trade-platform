package com.campus.trade.service.impl;

import com.campus.trade.exception.CustomException;
import com.campus.trade.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
     * 【最终修正】构造函数不再需要任何注入。
     * 我们将动态地获取项目的当前工作目录，并在此目录下创建uploads文件夹。
     */
    public FileStorageServiceImpl() {
        // 创建一个指向当前工作目录下的 "uploads" 文件夹的路径
        // 【修正】使用相对路径，与WebConfig中的静态资源映射保持一致
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            // 如果这个目录不存在，就创建它
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
