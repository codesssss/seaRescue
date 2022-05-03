package com.sxh.system.controller.system;

import com.sxh.common.core.controller.BaseController;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.entity.WeatherWarning;
import com.sxh.common.core.domain.entity.WeatherWarningEntity;
import com.sxh.system.service.impl.WebSocketServiceImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 气象相关
 *
 * @author sxh
 * @date 2022/04/07 21:36
 **/

@RestController
@RequestMapping("/weather")
public class WeatherController extends BaseController {
    @Resource
    RestTemplate restTemplate;
    @Resource
    WebSocketServiceImpl webSocketService;

    @PostMapping("/predict")
    public Response predictModel(@RequestParam("file") MultipartFile file)
            throws IllegalStateException {
        try {
            //获取文件后缀，因此此后端代码可接收一切文件，上传格式前端限定
            String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                    .toLowerCase();
            // 重构文件名称
            System.out.println("前端传递的保存路径：");
            String pikId = UUID.randomUUID().toString().replaceAll("-", "");
            String newFileName = pikId + "." + fileExt;
            System.out.println("重构文件名防止上传同名文件：" + newFileName);
            //保存视频
//            File fileSave = new File(SavePath, newFileName);
//            file.transferTo(fileSave);
            String url = "http://localhost:8000/predict";
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
            byte[] body = responseEntity.getBody();
            FileOutputStream fileOutputStream = new FileOutputStream(new File("./searescue-admin/src/resources/pictures/predict.jpg"));
            fileOutputStream.write(body);
            //关闭流
            fileOutputStream.close();
            return Response.success(body);
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            //保存视频错误则设置返回码为400
            Response.error();

        }
        return Response.error();

    }

    @PostMapping("/test-warning")
    public Response testWarning() {
        WeatherWarningEntity entity=new WeatherWarningEntity();
        List<WeatherWarning> warningList=new ArrayList<>();
        WeatherWarning warningItem=new WeatherWarning();
        warningItem.setLevel("橙色预警");
        warningItem.setPubTime("2022/4/11 15:33");
        warningItem.setSender("台州市气象局");
        warningItem.setText("据我市气象局报告，预期未来两天会有暴雨");
        warningItem.setTypeName("暴雨");
        warningItem.setTitle("我市未来两天将有大规模降雨");
        warningList.add(warningItem);
        entity.setWarning(warningList);
        webSocketService.alertWarning(entity);
        return Response.success();

    }
}