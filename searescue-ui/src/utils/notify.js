export default {
  name: "Asider",
  data() {
    return {
      websocket: null
    }
  },
  methods: {
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
      // 根据服务器推送的消息做自己的业务处理
      window.console.log(event.data);
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
      this.websocket = new WebSocket('ws://localhost:8080/push/websocket');
      this.initWebSocket();
    } else {
      alert('当前浏览器不支持WebSocket!!!')
    }
  }
}
