package com.chen.product.controller;


import com.chen.product.service.FileUploadService;
import com.chen.search.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Api(tags="文件上传接口")
@RestController
@RequestMapping("admin/product")
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;
    @ApiOperation("文件上传接口")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        //接受前端传入的图片，将接收到的图片返回到阿里云上，通过url
      String url=fileUploadService.uploadFile(file);

        return Result.ok(null);

    }
}
