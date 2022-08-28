package top.flagshen.myqq.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import top.flagshen.myqq.common.handler.IgnoreGlobalResponse;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author dc
 * @date 2022/8/12 23:16
 */
@RequestMapping("/image")
@RestController
public class ImageController {

    @GetMapping(value = "/{name}",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    @IgnoreGlobalResponse
    public byte[] image(@PathVariable String name) throws Exception {
        File file = new File("C:\\pic\\" + name);
        try (FileInputStream inputStream = new FileInputStream(file);) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        }
    }
}
