package com.sxh.system.controller.system;


import com.fasterxml.jackson.databind.ser.Serializers;
import com.sxh.common.core.controller.BaseController;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.ResponseEnum;
import com.sxh.common.core.domain.entity.MaydayEntity;
import com.sxh.common.core.domain.entity.ResShipInfo;
import com.sxh.common.core.domain.entity.ShipEntity;
import com.sxh.common.core.page.ResponsePageInfo;
import com.sxh.system.service.impl.MaydayServiceImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@RestController
@RequestMapping("/mayday-management")
public class MaydayController extends BaseController {
    @Resource
    MaydayServiceImpl maydayService;

    @RequestMapping("/add")
    @ResponseBody
    public Response addMayday(@RequestBody MaydayEntity req) {
        return maydayService.addMayday(req);
    }

    @DeleteMapping("delete/{maydayCodes}")
    @ResponseBody
    public Response deleteMayday(@ApiParam(name = "maydayCodes", value = "编号codes{逗号分隔}", required = true)
                                 @PathVariable Long[] maydayCodes) {
        return maydayService.deleteMayday(maydayCodes);
    }

    @RequestMapping("/update")
    @ResponseBody
    public Response updateMayday(@RequestBody MaydayEntity req) {
        return maydayService.updateMayday(req);
    }

    @RequestMapping("/getAll")
    @ResponseBody
    public ResponsePageInfo getAllMayday() {
        return toResponsePageInfo(maydayService.getAllMayday());
    }

    @RequestMapping("/getValid")
    @ResponseBody
    public ResponsePageInfo getValidMayday() {
        return toResponsePageInfo(maydayService.getValidMayday());
    }

    @RequestMapping("/getPath")
    @ResponseBody
    public Response getPath() {
        return Response.success(maydayService.getPath());
    }

    @GetMapping(value = "/getMayday/{dictCode}")
    @ResponseBody
    public Response getMaydayInfo(@PathVariable("dictCode") Long dictCode
    ) {
        return Response.success(maydayService.getMaydayInfo(dictCode));
    }

    @GetMapping(value = "/getResShip/{dictCode}")
    @ResponseBody
    public Response getResShip(@PathVariable("dictCode") Long dictCode
    ) {
        List<ShipEntity> entityList = maydayService.getResShip(dictCode);
        if (entityList.size() != 0) {
            return Response.success(entityList);
        }
        return Response.error(ResponseEnum.NOT_ENOUGH_RESSHIP);
    }

    @PostMapping(value = "/send")
    @ResponseBody
    public Response sendShip(@RequestBody ResShipInfo req
    ) {
        return maydayService.sendResShip(req);
    }

    @PostMapping(value = "/finish")
    @ResponseBody
    public Response finishMayday(@RequestBody ResShipInfo req
    ) {
        return maydayService.finishMayday(req);
    }
}

