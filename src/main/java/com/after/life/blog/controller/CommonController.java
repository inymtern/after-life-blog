package com.after.life.blog.controller;

import cn.hutool.http.ContentType;
import com.after.life.blog.except.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 16:39
 */
@RestController
@RequestMapping("/api/v1/common")
public class CommonController {

    @Value("${config.upload.path}")
    private String uploadPath;

    @Value("${config.gateway.address}")
    private String gatewayAddress;


    @PostMapping("/upload")
    public ResponseEntity<String> upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CommonException("文件为空");
        }
        try {
            String fileName = getNewName(file.getOriginalFilename());
            File dest = new File(uploadPath + File.separator + fileName);
            file.transferTo(dest);
            return ResponseEntity.ok(getDomain()  + "/api/v1/common/download/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommonException("文件上传失败");
        }
    }


    @PostMapping("/uploadImg")
    public ResponseEntity<String> uploadImg(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CommonException("文件为空");
        }
        try {
            String fileName = getNewName(file.getOriginalFilename());
            File dest = new File(uploadPath + File.separator + fileName);
            file.transferTo(dest);
            return ResponseEntity.ok(getDomain() + "/api/v1/common/images/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommonException("文件上传失败");
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> images(@PathVariable String filename) {
        Path filePath = Paths.get(uploadPath).resolve(filename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new CommonException("获取失败");
        }
    }



    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Path filePath = Paths.get(uploadPath).resolve(filename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }











    @jakarta.annotation.Resource
    private HttpServletRequest request;
    public String getDomain() {
        String contextPath = request.getContextPath();
        return gatewayAddress  + contextPath;
    }

    public String getNewName(String fileName) {
        if(fileName == null) return "";
        int i = fileName.lastIndexOf(".");
        if(i != -1) {
            return System.currentTimeMillis() + fileName.substring(i);
        }else {
            return "";
        }
    }
}
