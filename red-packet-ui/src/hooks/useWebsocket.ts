import { ref, onMounted, onUnmounted } from 'vue';
/**
 * @description websocket hook
 * @param url websocket地址：ws://139.198.163.91:8500/api/websocket/{token}
 * @param onMessage 收到消息的回调
 * @param heartbeatInterval 心跳间隔时间
 */
export default function useWebsocket(url:string,onMessage:(res:any)=>void,heartbeatInterval = 3000,){
      // 用来存放websocket实例
    const socketTask = ref(null as any);
    // 连接是否处于断开状态的标识
    const isDisconnect = ref(true);
    // 心跳定时器
    let heartbeatTimer = null as any;

    const connect=()=>{
        if(isDisconnect.value){
            isDisconnect.value = false;
            console.log('WebSocket连接中...',url)
            console.log('socketTask',socketTask)
            socketTask.value = new WebSocket(url);
            socketTask.value.onopen =()=>{
                console.log('WebSocket连接已打开');
                startHeartbeat();
            };


            socketTask.value.onClose=()=>{
                console.log('WebSocket连接已关闭');
                if(isDisconnect.value){
                    setTimeout(()=>{
                        console.log('WebSocket尝试重新连接');
                        connect(); 
                    },1000)
                }
            };
            
            socketTask.value.onerror =(error:any) => {
                console.error('WebSocket连接发生错误:', error);
                isDisconnect.value = true;
            } 

            socketTask.value.onmessage = (res:{data:string}) => {
                let data = JSON.parse(res.data);
                console.log('收到服务器消息:', data)
                if (onMessage) {
                  onMessage(data);
                }
            };
        }
    }

    const disconnect= () => {
        console.log('主动断开')
        if (socketTask.value) {
          isDisconnect.value = false;
          socketTask.value.close();
          stopHeartbeat();
          socketTask.value = null;
        }
    }

    const startHeartbeat = () =>{
        heartbeatTimer = setInterval(()=>{
            if (socketTask.value) {
                console.log('发送心跳')
                socketTask.value.send({});
              }
        },heartbeatInterval);
    }

    const stopHeartbeat = () => {
        clearInterval(heartbeatTimer);
    }
    
    const sendMessage = (message:any)=>{
        console.log('发送消息:socketTask.value', socketTask.value)
        if(socketTask.value){
            socketTask.value.send({
                data: JSON.stringify(message)
            })
        }
    }

    // 组件挂载时连接
  onMounted(() => {
    connect();
  });
  // 组件卸载时断开连接
  onUnmounted(() => {
    disconnect();
  });

  return {
    socketTask,
    connect,
    disconnect,
    sendMessage,
  };
    
}

