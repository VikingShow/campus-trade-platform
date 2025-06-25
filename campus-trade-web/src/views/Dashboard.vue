<template>
  <el-row :gutter="20">
    <el-col :span="5">
      <el-card>
        <el-menu :default-active="$route.path" router>
          <el-menu-item-group title="交易管理">
            <el-menu-item index="/dashboard/purchases">
              <el-icon><Goods /></el-icon>
              <span>我买到的</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/sales">
              <el-icon><Sell /></el-icon>
              <span>我卖出的</span>
            </el-menu-item>
            <!-- 【关键修正】在这里新增我的收藏菜单项 -->
            <el-menu-item index="/dashboard/favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
          </el-menu-item-group>
           <el-menu-item-group title="账户管理">
            <el-menu-item index="/dashboard/profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
          </el-menu-item-group>
        </el-menu>
      </el-card>
    </el-col>
    <el-col :span="19">
      <el-card>
        <router-view></router-view>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
// 【关键修正】导入 Star 图标
import { Goods, Sell, User, Star } from '@element-plus/icons-vue';
</script>