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
        <el-menu-item index="/delivery-management">
          <el-icon><Van /></el-icon>
          <span>配送管理</span>
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
import { DataLine, User, Goods, Tickets, Location, Van, ArrowDown } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const router = useRouter();
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background: linear-gradient(120deg, #f5f6fa 0%, #e9ebf0 100%);
}
.sidebar {
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(16px);
  border-radius: 24px;
  margin: 18px 0 18px 18px;
  box-shadow: 0 8px 32px 0 rgba(60,60,60,0.10);
  padding-top: 12px;
  min-height: calc(100vh - 36px);
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 22px;
  color: #007aff;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 10px;
  font-family: 'San Francisco', -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Arial, 'Segoe UI', 'Microsoft YaHei', sans-serif;
}
.el-menu {
  border-right: none;
  background: transparent;
  width: 100%;
}
.el-menu-item {
  border-radius: 14px !important;
  margin: 6px 12px;
  transition: background 0.2s, color 0.2s;
  font-size: 16px;
  font-weight: 500;
}
.el-menu-item.is-active {
  background: linear-gradient(90deg, #e0e5ec 0%, #f5f6fa 100%);
  color: #007aff !important;
  box-shadow: 0 2px 8px 0 rgba(0,122,255,0.08);
}
.el-menu-item:hover {
  background: #e5e9f2;
  color: #007aff;
}

.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-bottom: none;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(12px);
  border-radius: 0 0 24px 24px;
  margin: 18px 18px 0 0;
  box-shadow: 0 4px 24px 0 rgba(60,60,60,0.08);
  min-height: 64px;
  padding: 0 32px;
}
.el-dropdown-link {
  cursor: pointer;
  color: #222;
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  padding: 8px 18px;
  border-radius: 16px;
  transition: background 0.2s, color 0.2s;
}
.el-dropdown-link:hover {
  background: #e5e9f2;
  color: #007aff;
}
.content {
  padding: 36px 36px 24px 36px;
  background: transparent;
  min-height: 600px;
}
@media (max-width: 900px) {
  .sidebar {
    margin: 0;
    border-radius: 0 0 24px 24px;
    min-height: auto;
  }
  .header {
    margin: 0;
    border-radius: 0;
    padding: 0 10px;
  }
  .content {
    padding: 12px 4px;
  }
}
</style>
