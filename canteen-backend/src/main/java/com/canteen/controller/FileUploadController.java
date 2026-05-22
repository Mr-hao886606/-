package com.canteen.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.canteen.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("请选择文件");
        }

        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String suffix = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : ".png";
        String newFileName = IdUtil.fastSimpleUUID() + suffix;

        // 按日期分目录
        String datePath = cn.hutool.core.date.DateUtil.today().replace("-", "/");
        File dir = new File(uploadPath + datePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        try {
            File dest = new File(dir, newFileName);
            file.transferTo(dest);
            String fileUrl = "/uploads/" + datePath + "/" + newFileName;
            return R.ok("上传成功").put("url", fileUrl);
        } catch (IOException e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }
}
