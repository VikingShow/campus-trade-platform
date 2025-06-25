<template>
  <el-container class="main-layout">
    <el-header class="header">
      <div class="logo-area" @click="$router.push('/')">
        <el-icon :size="30" color="#409EFC"><School /></el-icon>
        <span class="logo-title">校园二手市场</span>
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
import { UserFilled, School } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const router = useRouter();

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
  background-color: #f4f4f5;
}
.main-layout {
  min-height: 100vh;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  background-color: #fff;
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
  font-size: 20px;
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