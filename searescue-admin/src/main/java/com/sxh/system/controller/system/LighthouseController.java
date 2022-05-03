package com.sxh.system.controller.system;



import com.sxh.common.core.controller.BaseController;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.entity.LighthouseEntity;
import com.sxh.common.core.page.ResponsePageInfo;
import com.sxh.system.service.impl.LighthouseServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@RestController
@RequestMapping("/lighthouse-management")
public class LighthouseController extends BaseController {
    @Resource
    LighthouseServiceImpl lighthouseService;

    @RequestMapping("/add")
    @ResponseBody
    public Response addLightHouse(@RequestBody LighthouseEntity req){
        return lighthouseService.addLighthouse(req);
    }

    @DeleteMapping("/delete/{lighthouseCodes}")
    @ResponseBody
    public Response deleteLightHouse(@PathVariable Long[] lighthouseCodes){
        return lighthouseService.deleteLighthouse(lighthouseCodes);
    }

    @RequestMapping("/update")
    @ResponseBody
    public Response updateLightHouse(@RequestBody LighthouseEntity req){
        return lighthouseService.updateLighthouse(req);
    }

    @RequestMapping("/getAll")
    @ResponseBody
    public ResponsePageInfo getAllLightHouse(){
        return toResponsePageInfo(lighthouseService.getAllLighthouse());
    }

    @GetMapping(value = "/getLighthouse/{dictCode}")
    @ResponseBody
    public Response getLighthouseInfo(@PathVariable("dictCode") Long dictCode
    ) {
        return Response.success(lighthouseService.getLighthouseInfo(dictCode));
    }
}

