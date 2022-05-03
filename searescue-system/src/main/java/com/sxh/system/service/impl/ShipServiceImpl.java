package com.sxh.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.ResponseEnum;
import com.sxh.common.core.domain.entity.ShipEntity;
import com.sxh.common.core.page.ResponsePageInfo;
import com.sxh.system.dto.system.ShipManagementReq;
import com.sxh.system.mapper.IShipDao;
import com.sxh.system.service.MPShipService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@Service
public class ShipServiceImpl extends ServiceImpl<IShipDao, ShipEntity> implements MPShipService {
    public Response addShip(ShipManagementReq req) {
        if (req.getName()!= null) {
            ShipEntity ship = new ShipEntity();
            BeanUtils.copyProperties(req, ship);
            baseMapper.insert(ship);
            return Response.success();
        }
        return Response.error(ResponseEnum.ERROR);
    }

    public Response deleteShip(Long[] shipCodes) {
        int row = 0;
        for (Long code : shipCodes) {
            row += baseMapper.deleteById(code);
        }
        if (row > 0) {
            return Response.success();
        }
        return Response.error();
    }


    public Response updateShip(ShipManagementReq req) {
        if (req.getId() != null) {
            ShipEntity ship = new ShipEntity();
            BeanUtils.copyProperties(req, ship);
            baseMapper.updateById(ship);
            return Response.success();
        }
        return Response.error(ResponseEnum.ERROR);
    }

    public List<ShipEntity> getAllShip() {
        ShipEntity ship = new ShipEntity();
        Map<String, Object> map = new HashMap<>();
        List<ShipEntity> shipEntities = baseMapper.selectByMap(map);
        return shipEntities;

    }

    public ShipEntity getShipInfo(Long shipId){
        return baseMapper.selectById(shipId);
    }


}
