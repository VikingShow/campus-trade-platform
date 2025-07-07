<template>
  <div>
    <h3>我买到的宝贝</h3>
    <el-table :data="orders" style="width: 100%" v-loading="loading">
      <el-table-column label="商品" min-width="200">
        <template #default="scope">
          <div style="display: flex; align-items: center; cursor: pointer;" @click="goToProduct(scope.row.productId)">
            <el-image :src=scope.row.productImage fit="cover" style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0;"></el-image>
            <span style="margin-left: 10px">{{ scope.row.productTitle }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="totalPrice" label="价格" width="100">
          <template #default="scope">￥{{ scope.row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="sellerNickname" label="卖家" width="150" />
      <el-table-column prop="orderStatus" label="订单状态" width="150">
        <template #default="scope">
          <span class="status-tag" :class="'status-' + getStatusType(scope.row.orderStatus)">{{ formatStatus(scope.row.orderStatus) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
       <template #default="scope">
          <!-- 【新增】评价按钮 -->
          <el-button size="small" @click="openRatingDialog(scope.row)" v-if="scope.row.orderStatus === 'COMPLETED'" :style="'background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'">评价</el-button>
          <el-button size="small" @click="handleCancel(scope.row)" v-if="scope.row.orderStatus === 'AWAITING_MEETUP'" :style="'background:#ff3b30!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'">取消订单</el-button>
          <span v-if="scope.row.orderStatus !== 'COMPLETED' && scope.row.orderStatus !== 'AWAITING_MEETUP'">--</span>
        </template>
      </el-table-column>
    </el-table>
     <el-empty v-if="!loading && orders.length === 0" description="您还没有购买任何商品"></el-empty>

     <!-- 【新增】评价对话框 -->
    <RatingDialog v-if="ratingDialogVisible" v-model="ratingDialogVisible" :order-data="selectedOrder" @submitted="fetchOrders" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMyPurchases, updateOrderStatus } from '../api/order';
import { ElMessage, ElMessageBox } from 'element-plus';
import RatingDialog from './RatingDialog.vue'; // 【新增】导入评价组件


const router = useRouter();
const orders = ref([]);
const loading = ref(false);

// const backendUrl = 'http://localhost:8080';

// const fullImageUrl = (relativePath) => {
//     if (!relativePath) return '';
//     if (relativePath.startsWith('http')) return relativePath;
//     return `${backendUrl}${relativePath}`;
// };

// 【新增】评价对话框相关状态
const ratingDialogVisible = ref(false);
const selectedOrder = ref(null);

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await getMyPurchases();
    orders.value = res.data.data;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const goToProduct = (id) => router.push(`/product/${id}`);

const handleCancel = (row) => {
    ElMessageBox.confirm('确定要取消这个订单吗？商品将会恢复可售状态。', '警告', {
      confirmButtonText: '确定', cancelButtonText: '再想想', type: 'warning',
    }).then(async () => {
      try {
        await updateOrderStatus(row.id, 'CANCELLED');
        ElMessage.success('订单已取消');
        fetchOrders();
      } catch (error) {}
    });
};

// 【新增】打开评价对话框的方法
const openRatingDialog = (order) => {
    selectedOrder.value = order;
    ratingDialogVisible.value = true;
};

onMounted(fetchOrders);

const statusMap = {
  'AWAITING_MEETUP': { text: '待交易', type: 'warning' },
  'AWAITING_SHIPMENT': { text: '待发货', type: 'warning' },
  'SHIPPED': { text: '已发货', type: 'info' },
  'DELIVERED': { text: '已送达', type: 'success' },
  'COMPLETED': { text: '已完成', type: 'success' },
  'CANCELLED': { text: '已取消', type: 'info' }
};
const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';
</script>