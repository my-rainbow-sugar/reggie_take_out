package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取扩展名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名重复导致文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File file1 = new File(basePath);
        //如果目录不存在
        if (!file1.exists()) {
            //目录不存在，需要创建
            file1.mkdirs();
        }
        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath +  File.separator + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //创建输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + File.separator + name));
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);

            //创建输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            outputStream.write(bytes);

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(String name) {
        FileInputStream fileInputStream = null;
        ResponseEntity<byte[]> responseEntity = null;
        try {
            //创建输入流，通过输入流读取文件内容
            fileInputStream = new FileInputStream(new File(basePath + File.separator + name));
            //创建字节数组
            byte[] bytes = new byte[fileInputStream.available()];
            //将流读到字节数组中
            fileInputStream.read(bytes);

            //创建HttpHeaders对象设置响应头信息
            MultiValueMap<String, String> headers = new HttpHeaders();
            //设置要下载方式以及下载文件的名字
            headers.add("Content-Disposition", "attachment;filename=" + name);
            //设置响应状态码
            HttpStatus statusCode = HttpStatus.OK;
            //创建ResponseEntity对象
            responseEntity = new ResponseEntity<>(bytes, headers, statusCode);
            //关闭输入流
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseEntity;
    }
    */
}
