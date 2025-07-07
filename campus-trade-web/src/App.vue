<template>
  <el-container class="main-layout">
    <el-header class="header">
      <div class="logo-area" @click="$router.push('/')">
        <el-icon :size="30" color="#67C23A"><SwitchFilled /></el-icon>
        <span class="logo-title">又又</span>
      </div>
      <div class="menu-area">
         <el-menu mode="horizontal" :router="true" :default-active="$route.path" background-color="transparent" :ellipsis="false">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/publish">发布商品</el-menu-item>
        </el-menu>
      </div>
      <div class="user-area">
        <div v-if="authStore.isAuthenticated">
          <el-dropdown>
            <el-avatar :icon="UserFilled" :src="authStore.user?.avatar || ''" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>{{ authStore.user?.nickname || '用户' }}</el-dropdown-item>
                <el-dropdown-item divided @click="$router.push('/dashboard')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/dashboard/favorites')">我的收藏</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/messages')">我的消息</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div v-else>
           <el-button @click="$router.push('/login')">登录/注册</el-button>
        </div>
        <el-button class="btn-info" @click="toggleTheme">
          <el-icon style="margin-right:4px;">
            <Sunny v-if="theme==='light'" />
            <Moon v-else />
          </el-icon>
          {{ theme==='dark' ? '白天模式' : '夜间模式' }}
        </el-button>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { useAuthStore } from './stores/authStore';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { UserFilled, SwitchFilled, Moon, Sunny } from '@element-plus/icons-vue';
import { onMounted, ref } from 'vue';

const authStore = useAuthStore();
const router = useRouter();

const theme = ref('light');

const setTheme = (val) => {
  theme.value = val;
  document.documentElement.setAttribute('data-theme', val);
  localStorage.setItem('theme', val);
};
const toggleTheme = () => {
  setTheme(theme.value === 'dark' ? 'light' : 'dark');
};
onMounted(() => {
  if (authStore.isAuthenticated) {
      authStore.fetchFavoriteIds();
  }
  // 读取主题
  const saved = localStorage.getItem('theme');
  setTheme(saved === 'dark' ? 'dark' : 'light');
  document.documentElement.setAttribute('data-theme', theme.value);
});

const handleLogout = () => {
    authStore.logout();
    ElMessage.success('已成功退出登录');
    router.push('/login');
};
</script>

<style>
body {
  margin: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  /* background-color: #f4f4f5; */
}
.main-layout {
  min-height: 100vh;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--color-border);
  background-color: var(--color-bg-card);
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}
.logo-area {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.logo-title {
  font-size: 22px;
  font-weight: bold;
  margin-left: 10px;
}
.menu-area {
    flex-grow: 1;
    display: flex;
    justify-content: center;
}
.el-menu--horizontal {
    border-bottom: none;
}
.el-menu-item {
    font-size: 16px;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-area .el-dropdown {
    cursor: pointer;
}
.main-content {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
}
</style>