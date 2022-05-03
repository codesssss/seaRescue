package com.sxh.system.controller.system;


import com.sxh.common.core.controller.BaseController;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.page.ResponsePageInfo;
import com.sxh.system.dto.system.ShipManagementReq;
import com.sxh.system.service.impl.ShipServiceImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@RestController
@RequestMapping("/ship-management")
public class ShipController extends BaseController {
    @Resource
    ShipServiceImpl shipService;

    @RequestMapping("/add")
    @ResponseBody
    public Response addShip(@RequestBody ShipManagementReq req) {
        return shipService.addShip(req);
    }

    @DeleteMapping("/{shipCodes}")
    @ResponseBody
    public Response deleteShip(@ApiParam(name = "shipCodes", value = "编号codes{逗号分隔}", required = true)
                               @PathVariable Long[] shipCodes) {
        return shipService.deleteShip(shipCodes);
    }

    @RequestMapping("/update")
    @ResponseBody
    public Response updateShip(@RequestBody ShipManagementReq req) {
        return shipService.updateShip(req);
    }

    @RequestMapping("/getAll")
    @ResponseBody
    public ResponsePageInfo getAllShip() {
        return toResponsePageInfo(shipService.getAllShip());
    }

    @GetMapping(value = "/getShip/{dictCode}")
    @ResponseBody
    public Response getShipInfo(@PathVariable("dictCode") Long dictCode
    ) {
        return Response.success(shipService.getShipInfo(dictCode));
    }


}

