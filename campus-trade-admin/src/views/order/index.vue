<template>
  <div class="order-management">
    <h2>订单管理</h2>
    <div class="toolbar">
      <el-input
        v-model="searchOrderId"
        placeholder="按订单ID搜索"
        clearable
        @clear="fetchOrders"
        @keyup.enter="fetchOrders"
        style="width: 300px; margin-right: 10px;"
      >
        <template #append>
          <el-button :icon="Search" @click="fetchOrders" />
        </template>
      </el-input>
    </div>

    <el-table :data="orders" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="订单ID" width="100" />
      <el-table-column label="商品信息" min-width="250">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <el-image 
              :src="fullImageUrl(scope.row.productImage)" 
              fit="cover" 
              style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0;"
              lazy
            />
            <span style="margin-left: 10px">{{ scope.row.productTitle }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="totalPrice" label="价格" width="100">
        <template #default="scope">￥{{ scope.row.totalPrice }}</template>
      </el-table-column>
       <el-table-column prop="buyerNickname" label="买家" />
       <el-table-column prop="sellerNickname" label="卖家" />
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.orderStatus)">
            {{ formatStatus(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ new Date(scope.row.createTime).toLocaleString() }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllOrdersAdmin } from '../../api/admin';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

const orders = ref([]);
const loading = ref(false);
const searchOrderId = ref('');

const backendUrl = 'http://localhost:8080';

const fullImageUrl = (relativePath) => {
    if (!relativePath) return '';
    if (relativePath.startsWith('http')) return relativePath;
    return `${backendUrl}${relativePath}`;
};

const fetchOrders = async () => {
  loading.value = true;
  try {
    const params = { orderId: searchOrderId.value };
    const response = await getAllOrdersAdmin(params);
    orders.value = response.data.data;
  } catch (error) {
    ElMessage.error('加载订单列表失败');
  } finally {
    loading.value = false;
  }
};

const statusMap = {
  'AWAITING_MEETUP': { text: '待交易', type: 'warning' },
  'COMPLETED': { text: '已完成', type: 'success' },
  'CANCELLED': { text: '已取消', type: 'info' }
};
const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';

onMounted(fetchOrders);
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
</style>