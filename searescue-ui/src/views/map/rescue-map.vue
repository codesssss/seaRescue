<template>
  <div id="app" style="width: 100%; height: 600px;">
    <amap
      ref="map"
      map-style="amap://styles/whitesmoke"
      cache-key="rescue"
      :zoom="zoom"
      :center="position"
    >
      <amap-tool-bar/>
      <amap-control-bar/>
      <amap-marker
        v-for="item in lightHouseList"
        :position=[item.longitude,item.latitude]
        :label="{
          content: '灯塔',
          direction: 'bottom',
        }"
        :icon="lighthouseIcon"
      />
      <!--      <amap-circle-->
      <!--        v-for="item in lightHouseList"-->
      <!--        :center=[item.longitude,item.latitude]-->
      <!--        :radius=item.scope*10 />-->
      <amap-marker
        v-for="item in shipList"
        :position=[item.longitude,item.latitude]
        :label="{
          content: item.name,
          direction: 'bottom',
        }"
        :icon="item.type==='1'?shipIcon:resShipIcon"
      />
      <amap-marker
        v-for="item in maydayList"
        :position=[item.ship.longitude,item.ship.latitude]
        :label="{
          content: '求救点',
          direction: 'top',
        }"
        :icon="maydayIcon"
      />
      <amap-polyline
        v-for="item in pathList"
        :path="[item.start,item.end]"
      />
    </amap>


  </div>
</template>

<script>
import shipIcon from '../../assets/ship/normal_ship.svg'
import resShipIcon from '../../assets/ship/resShip.png'
import maydayIcon from '../../assets/mayday/mayday.png'
import lighthouseIcon from '../../assets/lighthouse/lighthouse.png'
import {loadPlugins} from '@amap/amap-vue'
import {listLighthouse} from "../../api/system/lighthouse";
import {listShip} from "../../api/system/ship";
import {listMayday, listPath, listValidMayday} from "../../api/system/mayday";

export default {
  name: 'RescueMap',
  data() {
    return {
      shipIcon,
      maydayIcon,
      resShipIcon,
      lighthouseIcon,
      position: [121.778349, 28.460477],
      zoom: 10,
      lightHouseList: undefined,
      shipList: undefined,
      maydayList: undefined,
      pathList: undefined
    }
  },
  created() {
    this.loadAllLighthouse()
    this.loadAllShip()
    this.loadAllMayday()
    this.loadAllPath()
  },
  methods: {
    loadAllLighthouse() {
      listLighthouse().then(response => {
          this.lightHouseList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    loadAllShip() {
      listShip().then(response => {
          this.shipList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    loadAllMayday() {
      listValidMayday().then(response => {
          this.maydayList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    loadAllPath() {
      listPath().then(response => {
          this.pathList = response.data;
          console.log(this.pathList)
          this.loading = false;
        }
      );
    },
    async onCarReady(car) {
      await loadPlugins('AMap.MoveAnimation');
      car.moveAlong(this.path, {
        speed: 2000,
        circlable: true,
        autoRotation: true,
      });
    },

    // async onCarReady(car) {
    //   await loadPlugins('AMap.MoveAnimation')
    //   car.moveAlong(path, {
    //     speed: 2000,
    //     circlable: true,
    //     autoRotation: true
    //   })
    // }
  }

}
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
}
</style>

