<template>
  <div class="messages-layout">
    <el-card>
      <el-container style="height: calc(100vh - 120px);">
        <el-aside width="300px" class="conversation-list">
          <div class="list-header">会话列表</div>
          <el-scrollbar>
            <div v-if="loading" class="loading-placeholder">加载中...</div>
            <div
              v-for="conv in conversations"
              :key="conv.otherUserId"
              :class="['conversation-item', { 'is-active': $route.params.otherUserId === conv.otherUserId }]"
              @click="selectConversation(conv.otherUserId)"
            >
              <el-avatar :size="50" :src="fullImageUrl(conv.otherUserAvatar)" icon="UserFilled" />
              <div class="conv-info">
                <div class="conv-nickname">{{ conv.otherUserNickname }}</div>
                <div class="conv-last-msg">{{ conv.lastMessageContent }}</div>
              </div>
            </div>
            <el-empty v-if="!loading && conversations.length === 0" description="暂无会话" :image-size="80"></el-empty>
          </el-scrollbar>
        </el-aside>
        <el-main class="chat-window-wrapper">
          <router-view :key="$route.params.otherUserId" @new-message="handleNewMessage"></router-view>
        </el-main>
      </el-container>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getConversations } from '../../api/message';

const router = useRouter();
const conversations = ref([]);
const loading = ref(false);

const backendUrl = 'http://localhost:8080';
const fullImageUrl = (relativePath) => {
    if (!relativePath) return '';
    if (relativePath.startsWith('http')) return relativePath;
    return `${backendUrl}${relativePath}`;
};

const fetchConversations = async () => {
  loading.value = true;
  try {
    const response = await getConversations();
    conversations.value = response.data.data;
  } catch (error) {
    console.error("加载会话列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const selectConversation = (userId) => {
  router.push(`/messages/${userId}`);
};

// 当子组件（聊天窗口）发送新消息时，刷新会话列表
const handleNewMessage = () => {
    fetchConversations();
};

onMounted(fetchConversations);
</script>

<style scoped>
.messages-layout .el-card, .messages-layout .el-card >>> .el-card__body {
  padding: 0;
  height: calc(100vh - 100px);
}
.conversation-list { border-right: 1px solid #e6e6e6; display: flex; flex-direction: column; }
.list-header { padding: 20px; font-size: 18px; font-weight: bold; border-bottom: 1px solid #e6e6e6; flex-shrink: 0; }
.conversation-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}
.conversation-item:hover { background-color: #f5f7fa; }
.conversation-item.is-active { background-color: #ecf5ff; }
.conv-info { margin-left: 15px; overflow: hidden; }
.conv-nickname { font-weight: 500; color: #303133; }
.conv-last-msg {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 5px;
}
.loading-placeholder { text-align: center; padding: 20px; color: #909399; }
.chat-window-wrapper { padding: 0; display: flex; flex-direction: column; }
</style>