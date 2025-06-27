<template>
  <div class="chat-container" v-if="otherUserId">
    <div class="chat-header">与 {{ otherUserInfo.nickname || '用户' }} 的对话</div>
    <el-scrollbar class="message-area" ref="scrollbarRef">
      <div v-for="msg in messages" :key="msg.id" :class="['message-bubble-wrapper', { 'is-me': msg.senderId === currentUser.id }]">
        <el-avatar v-if="msg.senderId !== currentUser.id" :src=otherUserInfo.avatar icon="UserFilled" />
        <div class="message-bubble">{{ msg.content }}</div>
        <el-avatar v-if="msg.senderId === currentUser.id" :src=currentUser.avatar icon="UserFilled" />
      </div>
    </el-scrollbar>
    <div class="input-area">
      <el-input
        v-model="newMessage"
        placeholder="输入消息..."
        @keyup.enter.native="handleSend"
        type="textarea"
        :autosize="{ minRows: 1, maxRows: 4 }"
        resize="none"
      />
      <el-button type="primary" @click="handleSend" :loading="sending">发送</el-button>
    </div>
  </div>
  <el-empty v-else description="请从左侧选择一个会话开始聊天"></el-empty>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, watch } from 'vue';
import { useAuthStore } from '../../stores/authStore';
import { getMessageHistory, sendMessage } from '../../api/message';
import { getUserInfo } from '../../api/user';
import { ElMessage } from 'element-plus';
import { UserFilled } from '@element-plus/icons-vue';

const props = defineProps({ otherUserId: String });
const emit = defineEmits(['new-message']); // 定义一个事件，用于通知父组件

const authStore = useAuthStore();
const currentUser = authStore.user;
const messages = ref([]);
const newMessage = ref('');
const sending = ref(false);
const scrollbarRef = ref(null);
const otherUserInfo = ref({});
let pollingInterval = null;

// const backendUrl = 'http://localhost:8080';

// const fullImageUrl = (relativePath) => {
//     if (!relativePath) return '';
//     if (relativePath.startsWith('http')) return relativePath;
//     return `${backendUrl}${relativePath}`;
// };

const scrollToBottom = () => {
  nextTick(() => {
    if (scrollbarRef.value) {
      scrollbarRef.value.setScrollTop(scrollbarRef.value.wrapRef.scrollHeight);
    }
  });
};

const fetchHistoryAndUserInfo = async () => {
  if (!props.otherUserId) return;
  try {
    const [historyRes, userRes] = await Promise.all([
      getMessageHistory(props.otherUserId),
      getUserInfo(props.otherUserId)
    ]);
    messages.value = historyRes.data.data;
    otherUserInfo.value = userRes.data.data;
    scrollToBottom();
  } catch (error) {
    console.error("加载聊天记录失败:", error);
  }
};

const handleSend = async () => {
  if (!newMessage.value.trim()) return;
  sending.value = true;
  try {
    await sendMessage({
      receiverId: props.otherUserId,
      content: newMessage.value,
    });
    newMessage.value = '';
    await fetchHistoryAndUserInfo();
    emit('new-message'); // 发送事件，通知父组件刷新会话列表
  } catch (error) {
    ElMessage.error('消息发送失败');
  } finally {
    sending.value = false;
  }
};

const startPolling = () => {
    if (pollingInterval) clearInterval(pollingInterval);
    pollingInterval = setInterval(fetchHistoryAndUserInfo, 5000);
}

onMounted(() => {
  fetchHistoryAndUserInfo();
  startPolling();
});

onUnmounted(() => {
  if (pollingInterval) {
    clearInterval(pollingInterval);
  }
});

// 监听路由参数变化，当切换聊天对象时重新加载数据
watch(() => props.otherUserId, () => {
    fetchHistoryAndUserInfo();
    startPolling();
});
</script>

<style scoped>
.chat-container { display: flex; flex-direction: column; height: 100%; }
.chat-header { padding: 15px 20px; font-size: 16px; border-bottom: 1px solid #e6e6e6; flex-shrink: 0; }
.message-area { flex-grow: 1; padding: 20px; background-color: #f9f9f9; }
.message-bubble-wrapper { display: flex; margin-bottom: 20px; gap: 10px; }
.message-bubble { padding: 10px 15px; border-radius: 10px; background-color: #ffffff; border: 1px solid #ebeef5; max-width: 60%; word-wrap: break-word; }
.message-bubble-wrapper.is-me { flex-direction: row-reverse; }
.message-bubble-wrapper.is-me .message-bubble { background-color: #409eff; color: #fff; border: none; }
.input-area { display: flex; padding: 10px; border-top: 1px solid #e6e6e6; gap: 10px; align-items: center; }
</style>