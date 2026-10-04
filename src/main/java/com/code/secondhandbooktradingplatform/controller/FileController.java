package com.code.secondhandbooktradingplatform.controller;

import com.code.secondhandbooktradingplatform.common.Result;
import com.code.secondhandbooktradingplatform.common.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
public class FileController {

    private static final List<String> ALLOWED_EXT = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/api/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("请选择要上传的图片");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.lastIndexOf('.') >= 0) {
            ext = original.substring(original.lastIndexOf('.')).toLowerCase();
        }
        if (!ALLOWED_EXT.contains(ext)) {
            throw new ServiceException("仅支持 jpg/png/gif/webp 格式图片");
        }
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        file.transferTo(dir.resolve(filename).toFile());
        return Result.ok("/upload/" + filename);
    }
}
