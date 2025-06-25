<template>
  <div>
    <h3>我卖出的宝贝</h3>
    <el-table :data="orders" style="width: 100%" v-loading="loading">
      <el-table-column label="商品" min-width="200">
        <template #default="scope">
           <div style="display: flex; align-items: center; cursor: pointer;" @click="goToProduct(scope.row.productId)">
            <el-image :src="fullImageUrl(scope.row.productImage)" fit="cover" style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0;"></el-image>
            <span style="margin-left: 10px">{{ scope.row.productTitle }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="totalPrice" label="价格" width="100">
          <template #default="scope">￥{{ scope.row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="buyerNickname" label="买家" width="150" />
      <el-table-column prop="orderStatus" label="订单状态" width="150">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.orderStatus)">{{ formatStatus(scope.row.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleComplete(scope.row)" v-if="scope.row.orderStatus === 'AWAITING_MEETUP'">确认完成</el-button>
          <span v-else>--</span>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && orders.length === 0" description="您还没有卖出任何商品"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMySales, updateOrderStatus } from '../api/order';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const orders = ref([]);
const loading = ref(false);

const backendUrl = 'http://localhost:8080';

const fullImageUrl = (relativePath) => {
    if (!relativePath) return '';
    if (relativePath.startsWith('http')) return relativePath;
    return `${backendUrl}${relativePath}`;
};

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await getMySales();
    orders.value = res.data.data;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const goToProduct = (id) => router.push(`/product/${id}`);

const handleComplete = (row) => {
    ElMessageBox.confirm('请确认您已与买家完成线下交易并收款。', '确认交易', {
        confirmButtonText: '是的，已完成', cancelButtonText: '还没', type: 'success',
    }).then(async () => {
        try {
            await updateOrderStatus(row.id, 'COMPLETED');
            ElMessage.success('交易已完成');
            fetchOrders();
        } catch (error) {}
    });
};

onMounted(fetchOrders);

const statusMap = { 'AWAITING_MEETUP': { text: '待交易', type: 'warning' }, 'COMPLETED': { text: '已完成', type: 'success' }, 'CANCELLED': { text: '已取消', type: 'info' } };
const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';
</script>