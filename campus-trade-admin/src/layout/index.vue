<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">2Hand 后台管理</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/user-management">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/product-management">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/order-management">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/location-management">
          <el-icon><Location /></el-icon>
          <span>交易地点管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown>
            <span class="el-dropdown-link">
              欢迎您, 管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useAuthStore } from '../stores/authStore';
import { useRouter } from 'vue-router';
import { DataLine, User, Goods, Tickets, Location, ArrowDown } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const router = useRouter();
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.admin-layout { height: 100vh; }
.sidebar { background-color: #304156; }
.logo { height: 60px; line-height: 60px; text-align: center; font-size: 20px; color: #fff; font-weight: bold; }
.el-menu { border-right: none; }
.header { display: flex; justify-content: flex-end; align-items: center; border-bottom: 1px solid #dcdfe6; }
.el-dropdown-link { cursor: pointer; color: var(--el-color-primary); display: flex; align-items: center; }
.content { padding: 20px; background-color: #f0f2f5; }
</style>
