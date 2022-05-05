<template>
  <div class="dashboard-editor-container">

    <h2>欢迎来到海洋灾害预警及船舶救援系统</h2>

  </div>
</template>

<script>
import PanelGroup from './dashboard/PanelGroup'
import LineChart from './dashboard/LineChart'
import RaddarChart from './dashboard/RaddarChart'
import PieChart from './dashboard/PieChart'
import BarChart from './dashboard/BarChart'
import {notifyAll} from "../api/system/websocket";


export default {
  name: 'Index',
  components: {},
  data() {
    return {
      websocket: null
    }
  },
  methods: {
    stimulateNotify() {
      notifyAll();
    },
    initWebSocket() {
      // 连接错误
      this.websocket.onerror = this.setErrorMessage
      // 连接成功
      this.websocket.onopen = this.setOnopenMessage
      // 收到消息的回调
      this.websocket.onmessage = this.setOnmessageMessage
      // 连接关闭的回调
      this.websocket.onclose = this.setOncloseMessage
      // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
      window.onbeforeunload = this.onbeforeunload
    },
    setErrorMessage() {
      window.console.log('WebSocket连接发生错误，状态码：' + this.websocket.readyState)
    },
    setOnopenMessage() {
      window.console.log('WebSocket连接成功，状态码：' + this.websocket.readyState)
    },
    setOnmessageMessage(event) {
      window.console.log(event.data)
      // 根据服务器推送的消息做自己的业务处理
      this.$notify({
        title: '提示',
        message: event.data,
        duration:0

      });
    },
    setOncloseMessage() {
      window.console.log('WebSocket连接关闭，状态码：' + this.websocket.readyState)
    },
    onbeforeunload() {
      this.closeWebSocket()
    },
    closeWebSocket() {
      this.websocket.close()
    }
  },
  mounted() {
    if ('WebSocket' in window) {
      this.websocket = new WebSocket('ws://localhost:8081/push/websocket');
      this.initWebSocket();
    } else {
      alert('当前浏览器不支持WebSocket!!!')
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}


</style>

<style>
.el-notification {white-space:pre-wrap !important; }

</style>
