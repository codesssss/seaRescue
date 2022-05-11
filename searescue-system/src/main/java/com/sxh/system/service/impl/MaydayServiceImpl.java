package com.sxh.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.internal.bind.v2.model.core.MaybeElement;
import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.ResponseEnum;
import com.sxh.common.core.domain.entity.*;

import com.sxh.system.mapper.IMaydayDao;
import com.sxh.system.service.MPMaydayService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sxh
 * @since 2022-02-25
 */
@Service
public class MaydayServiceImpl extends ServiceImpl<IMaydayDao, MaydayEntity> implements MPMaydayService {
    @Resource
    ShipServiceImpl shipService;
    @Resource
    WebSocketServiceImpl webSocketService;

    public Response addMayday(MaydayEntity req) {
        if (req.getShipid() != null) {
            MaydayEntity mayday = new MaydayEntity();
            BeanUtils.copyProperties(req, mayday);
            baseMapper.insert(mayday);
            webSocketService.alertRescue();
            return Response.success();
        }
        return Response.error(ResponseEnum.ERROR);
    }

    public Response deleteMayday(Long[] req) {
        int row = 0;
        for (Long num : req) {
            row += baseMapper.deleteById(num);
        }
        if (row > 0) {
            return Response.success();
        }
        return Response.error();
    }


    public Response updateMayday(MaydayEntity req) {
        if (req.getId() != null) {
            MaydayEntity mayday = new MaydayEntity();
            BeanUtils.copyProperties(req, mayday);
            baseMapper.updateById(mayday);
            return Response.success();
        }
        return Response.error(ResponseEnum.ERROR);
    }

    public List<MaydayInfo> getAllMayday() {
        Map<String, Object> map = new HashMap<>();
        List<MaydayInfo> InfoList = new ArrayList<>();
        List<MaydayEntity> MaydayEntities = baseMapper.selectByMap(map);
        for (MaydayEntity entity : MaydayEntities) {
            MaydayInfo info = new MaydayInfo();
            BeanUtils.copyProperties(entity, info);
            ShipEntity shipEntity = new ShipEntity();
            shipEntity = shipService.getById(entity.getShipid());
            ShipEntity resShip = new ShipEntity();
            if (entity.getResship() != null) {
                resShip = shipService.getById(entity.getResship());
                info.setResship(resShip);
            }
            info.setShip(shipEntity);
            InfoList.add(info);
        }
        return InfoList;
    }

    /**
     * 获取有效的求救
     * @return
     */
    public List<MaydayInfo> getValidMayday() {
        Map<String, Object> map = new HashMap<>();
        List<MaydayInfo> InfoList = new ArrayList<>();
        List<MaydayEntity> MaydayEntities = baseMapper.selectByMap(map);
        for (MaydayEntity entity : MaydayEntities) {
            if(entity.getStatus().equals(1)||entity.getStatus().equals(0)){
            MaydayInfo info = new MaydayInfo();
            BeanUtils.copyProperties(entity, info);
            ShipEntity shipEntity = new ShipEntity();
            shipEntity = shipService.getById(entity.getShipid());
            ShipEntity resShip = new ShipEntity();
            if (entity.getResship() != null) {
                resShip = shipService.getById(entity.getResship());
                info.setResship(resShip);
            }
            info.setShip(shipEntity);
            InfoList.add(info);
            }
        }
        return InfoList;
    }


    public MaydayEntity getMaydayInfo(Long id) {
        MaydayEntity MaydayEntities = baseMapper.selectById(id);
        return MaydayEntities;
    }

    public List<ShipEntity> getResShip(Long id) {
        MaydayEntity maydayEntity = baseMapper.selectById(id);
        ShipEntity ship = shipService.getById(maydayEntity.getShipid());
        List<ShipEntity> finList = new ArrayList<>();
        int curLoad = ship.getLoad();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("isbusy", 0);
        List<ShipEntity> shipEntityList = shipService.getBaseMapper().selectList(wrapper);
        for (ShipEntity entity : shipEntityList) {
            if (entity.getCapacity() - entity.getLoad() >= curLoad && !entity.getId().equals(ship.getId())) {
                ShipEntity curShip = new ShipEntity();
                curShip = entity;
                finList.add(curShip);
            }
        }
        return finList;
    }

    public List<pathEntity> getPath() {
        List<pathEntity> res = new ArrayList<>();
        Map queryMap = new HashMap();
        List<MaydayEntity> maydayEntities = baseMapper.selectByMap(queryMap);
        List<ShipEntity> shipEntities = shipService.getBaseMapper().selectByMap(queryMap);
        for (MaydayEntity maydayItem : maydayEntities) {
            for (ShipEntity shipItem : shipEntities) {
                if (maydayItem.getStatus().equals(1) && shipItem.getId().equals(maydayItem.getResship())) {
                    ShipEntity maydayShip = shipService.getBaseMapper().selectById(maydayItem.getShipid());
                    res.add(new pathEntity(asList(shipItem.getLongitude(), shipItem.getLatitude()),
                            asList(maydayShip.getLongitude(), maydayShip.getLatitude())
                    ));
                }
            }

        }
        return res;
    }

    public Response sendResShip(ResShipInfo req) {
        MaydayEntity MaydayEntities = baseMapper.selectById(req.getId());
        ShipEntity resShip = shipService.getBaseMapper().selectById(req.getResship());
        MaydayEntities.setResship(req.getResship());
        MaydayEntities.setStatus(1);
        baseMapper.updateById(MaydayEntities);
        resShip.setIsbusy(1);
        shipService.getBaseMapper().updateById(resShip);
        //TODO:小船操作逻辑
        return Response.success();
    }

    public Response finishMayday(ResShipInfo req) {
        //TODO:修改位置
        MaydayEntity MaydayEntities = baseMapper.selectById(req.getId());
        MaydayEntities.setStatus(2);
        baseMapper.updateById(MaydayEntities);
        ShipEntity maydayShip=shipService.getBaseMapper().selectById(MaydayEntities.getShipid());
        ShipEntity resShip = shipService.getBaseMapper().selectById(req.getResship());
        resShip.setIsbusy(0);
        resShip.setLongitude(maydayShip.getLongitude());
        resShip.setLatitude(maydayShip.getLatitude());
        shipService.getBaseMapper().updateById(resShip);
        return Response.success();
    }
}
