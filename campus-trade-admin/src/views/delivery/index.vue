<template>
  <div class="delivery-management">
    <!-- 统计卡片区域 -->
    <div class="stats-cards" v-if="deliveryStats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon awaiting">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ deliveryStats.awaitingShipment || 0 }}</div>
                <div class="stat-label">待发货</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon shipped">
                <el-icon><Van /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ deliveryStats.shipped || 0 }}</div>
                <div class="stat-label">已发货</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon completed">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ deliveryStats.completed || 0 }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon total">
                <el-icon><DataBoard /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ deliveryStats.total || 0 }}</div>
                <div class="stat-label">总订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="toolbar">
      <h2>配送管理</h2>
      <div class="actions">
        <el-select v-model="filters.orderStatus" placeholder="订单状态" clearable @change="handleFilterChange" style="width: 140px; margin-right: 10px;">
          <el-option label="所有状态" value=""></el-option>
          <el-option label="待发货" value="AWAITING_SHIPMENT"></el-option>
          <el-option label="已发货" value="SHIPPED"></el-option>
          <el-option label="已完成" value="COMPLETED"></el-option>
        </el-select>
        <el-select v-model="filters.deliveryMethod" placeholder="配送方式" clearable @change="handleFilterChange" style="width: 140px; margin-right: 10px;">
          <el-option label="所有方式" value=""></el-option>
          <el-option label="快递配送" value="SHIPPING"></el-option>
          <el-option label="线下面交" value="MEETUP"></el-option>
        </el-select>
        <el-input
          v-model="filters.orderId"
          placeholder="订单ID搜索"
          clearable
          @clear="handleFilterChange"
          @keyup.enter="handleFilterChange"
          style="width: 160px; margin-right: 10px;"
        />
        <el-button type="primary" :icon="Refresh" @click="fetchOrders">刷新</el-button>
        <el-button type="success" :icon="Van" @click="batchShip" :disabled="selectedOrders.length === 0">
          批量发货 ({{ selectedOrders.length }})
        </el-button>
        <el-button type="warning" :icon="Download" @click="exportOrders">
          导出数据
        </el-button>
      </div>
    </div>

    <el-table 
      :data="orders" 
      v-loading="loading" 
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="订单ID" width="100" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <el-image 
              :src="scope.row.productImage" 
              fit="cover" 
              style="width: 50px; height: 50px; border-radius: 4px; flex-shrink: 0;"
              lazy
            />
            <div style="margin-left: 10px; flex: 1;">
              <div style="font-weight: 500; margin-bottom: 4px;">{{ scope.row.productTitle }}</div>
              <div style="color: #e6a23c; font-size: 14px;">¥{{ scope.row.totalPrice }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="买家信息" width="120">
        <template #default="scope">
          <div>{{ scope.row.buyerNickname }}</div>
        </template>
      </el-table-column>
      <el-table-column label="卖家信息" width="120">
        <template #default="scope">
          <div>{{ scope.row.sellerNickname }}</div>
        </template>
      </el-table-column>
      <el-table-column label="配送信息" min-width="200">
        <template #default="scope">
          <div v-if="scope.row.deliveryMethod === 'SHIPPING'">
            <div style="margin-bottom: 4px;">
              <el-tag size="small" type="success">快递配送</el-tag>
            </div>
            <div v-if="scope.row.recipientName" style="font-size: 13px; color: #606266;">
              <div>收件人: {{ scope.row.recipientName }} {{ scope.row.phone }}</div>
              <div style="margin-top: 2px;">地址: {{ scope.row.fullAddress }}</div>
            </div>
            <div v-else style="font-size: 13px; color: #909399;">暂无收货地址</div>
          </div>
          <div v-else>
            <div style="margin-bottom: 4px;">
              <el-tag size="small" type="primary">线下面交</el-tag>
            </div>
            <div style="font-size: 13px; color: #606266;">
              {{ scope.row.meetupLocationName || '未指定地点' }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="物流信息" width="150">
        <template #default="scope">
          <div v-if="scope.row.shippingProvider">
            <div style="font-size: 13px; margin-bottom: 2px;">{{ scope.row.shippingProvider }}</div>
            <div style="font-size: 12px; color: #909399;">{{ scope.row.trackingNumber }}</div>
          </div>
          <div v-else style="color: #909399; font-size: 13px;">待填写</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.orderStatus)" size="small">
            {{ formatStatus(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button 
            v-if="scope.row.orderStatus === 'AWAITING_SHIPMENT' && scope.row.deliveryMethod === 'SHIPPING'"
            size="small" 
            type="primary" 
            :icon="Van" 
            @click="openShipDialog(scope.row)"
          >
            发货
          </el-button>
          <el-button 
            v-else-if="scope.row.orderStatus === 'SHIPPED'"
            size="small" 
            type="success" 
            @click="markAsCompleted(scope.row)"
          >
            标记完成
          </el-button>
          <el-button 
            size="small" 
            @click="viewOrderDetails(scope.row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container" v-if="pagination.total > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-empty v-if="!loading && orders.length === 0" description="暂无配送订单"></el-empty>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="填写发货信息" width="500px">
      <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
        <el-form-item label="订单信息">
          <div style="background: #f5f7fa; padding: 10px; border-radius: 4px;">
            <div>订单ID: {{ currentOrder?.id }}</div>
            <div>商品: {{ currentOrder?.productTitle }}</div>
            <div>收件人: {{ currentOrder?.recipientName }} {{ currentOrder?.phone }}</div>
            <div>地址: {{ currentOrder?.fullAddress }}</div>
          </div>
        </el-form-item>
        <el-form-item label="快递公司" prop="shippingProvider">
          <el-select v-model="shipForm.shippingProvider" placeholder="选择快递公司" style="width: 100%;">
            <el-option label="顺丰速运" value="顺丰速运"></el-option>
            <el-option label="圆通速递" value="圆通速递"></el-option>
            <el-option label="中通快递" value="中通快递"></el-option>
            <el-option label="申通快递" value="申通快递"></el-option>
            <el-option label="韵达速递" value="韵达速递"></el-option>
            <el-option label="百世快递" value="百世快递"></el-option>
            <el-option label="德邦快递" value="德邦快递"></el-option>
            <el-option label="京东物流" value="京东物流"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="trackingNumber">
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShip" :loading="submitting">确认发货</el-button>
      </template>
    </el-dialog>

    <!-- 批量发货对话框 -->
    <el-dialog v-model="batchShipDialogVisible" title="批量发货" width="600px">
      <div style="margin-bottom: 20px;">
        <el-alert
          title="批量发货说明"
          description="将为选中的 {{ selectedOrders.length }} 个订单填写相同的发货信息"
          type="info"
          show-icon
          :closable="false"
        />
      </div>
      <el-form :model="batchShipForm" :rules="shipRules" ref="batchShipFormRef" label-width="100px">
        <el-form-item label="快递公司" prop="shippingProvider">
          <el-select v-model="batchShipForm.shippingProvider" placeholder="选择快递公司" style="width: 100%;">
            <el-option label="顺丰速运" value="顺丰速运"></el-option>
            <el-option label="圆通速递" value="圆通速递"></el-option>
            <el-option label="中通快递" value="中通快递"></el-option>
            <el-option label="申通快递" value="申通快递"></el-option>
            <el-option label="韵达速递" value="韵达速递"></el-option>
            <el-option label="百世快递" value="百世快递"></el-option>
            <el-option label="德邦快递" value="德邦快递"></el-option>
            <el-option label="京东物流" value="京东物流"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="trackingNumber">
          <el-input v-model="batchShipForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchShipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchShip" :loading="submitting">确认批量发货</el-button>
      </template>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="700px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单ID">{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.orderStatus)">
              {{ formatStatus(currentOrder.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="商品标题">{{ currentOrder.productTitle }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
          <el-descriptions-item label="买家">{{ currentOrder.buyerNickname }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ currentOrder.sellerNickname }}</el-descriptions-item>
          <el-descriptions-item label="配送方式">
            <el-tag :type="currentOrder.deliveryMethod === 'SHIPPING' ? 'success' : 'primary'">
              {{ currentOrder.deliveryMethod === 'SHIPPING' ? '快递配送' : '线下面交' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentOrder.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentOrder.deliveryMethod === 'SHIPPING'" style="margin-top: 20px;">
          <h4>收货地址信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="收件人">{{ currentOrder.recipientName || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentOrder.phone || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ currentOrder.fullAddress || '未填写' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-else style="margin-top: 20px;">
          <h4>面交信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="交易地点">{{ currentOrder.meetupLocationName || '未指定' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="currentOrder.shippingProvider" style="margin-top: 20px;">
          <h4>物流信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="快递公司">{{ currentOrder.shippingProvider }}</el-descriptions-item>
            <el-descriptions-item label="快递单号">{{ currentOrder.trackingNumber }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getAllOrdersAdmin, shipOrderByAdmin, updateOrderByAdmin, getDeliveryStats, exportDeliveryOrders } from '../../api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Van, Refresh, Clock, Check, DataBoard, Download } from '@element-plus/icons-vue';

const orders = ref([]);
const selectedOrders = ref([]);
const loading = ref(false);
const submitting = ref(false);
const shipDialogVisible = ref(false);
const batchShipDialogVisible = ref(false);
const detailDialogVisible = ref(false);
const shipFormRef = ref(null);
const batchShipFormRef = ref(null);
const currentOrder = ref(null);
const deliveryStats = ref(null);

const filters = reactive({ 
  orderId: '', 
  deliveryMethod: '', 
  orderStatus: '' 
});

const pagination = reactive({ 
  page: 1, 
  size: 20, 
  total: 0 
});

const shipForm = reactive({
  orderId: null,
  shippingProvider: '',
  trackingNumber: '',
});

const batchShipForm = reactive({
  shippingProvider: '',
  trackingNumber: '',
});

const shipRules = {
  shippingProvider: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
  trackingNumber: [{ required: true, message: '请输入快递单号', trigger: 'blur' }],
};

const fetchOrders = async () => {
  loading.value = true;
  try {
    const params = { 
      ...filters, 
      page: pagination.page, 
      size: pagination.size 
    };
    const response = await getAllOrdersAdmin(params);
    orders.value = response.data.data.list;
    pagination.total = response.data.data.total;
  } catch (error) {
    ElMessage.error('加载订单列表失败');
  } finally {
    loading.value = false;
  }
};

const fetchDeliveryStats = async () => {
  try {
    const response = await getDeliveryStats();
    deliveryStats.value = response.data.data;
  } catch (error) {
    console.error('获取配送统计数据失败:', error);
  }
};

const exportOrders = async () => {
  try {
    const params = { ...filters };
    const response = await exportDeliveryOrders(params);
    
    // 创建下载链接
    const blob = new Blob([response.data], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `配送订单_${new Date().toISOString().split('T')[0]}.xlsx`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    ElMessage.success('导出成功');
  } catch (error) {
    ElMessage.error('导出失败，请重试');
  }
};

const handleFilterChange = () => {
  pagination.page = 1;
  fetchOrders();
};

const handleSizeChange = (newSize) => {
  pagination.size = newSize;
  pagination.page = 1;
  fetchOrders();
};

const handleCurrentChange = (newPage) => {
  pagination.page = newPage;
  fetchOrders();
};

const handleSelectionChange = (selection) => {
  selectedOrders.value = selection.filter(order => 
    order.orderStatus === 'AWAITING_SHIPMENT' && 
    order.deliveryMethod === 'SHIPPING'
  );
};

const openShipDialog = (order) => {
  currentOrder.value = order;
  shipForm.orderId = order.id;
  shipForm.shippingProvider = '';
  shipForm.trackingNumber = '';
  shipDialogVisible.value = true;
};

const batchShip = () => {
  if (selectedOrders.value.length === 0) {
    ElMessage.warning('请先选择需要发货的订单');
    return;
  }
  batchShipForm.shippingProvider = '';
  batchShipForm.trackingNumber = '';
  batchShipDialogVisible.value = true;
};

const handleShip = async () => {
  if (!shipFormRef.value) return;
  await shipFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        await shipOrderByAdmin(shipForm.orderId, {
          shippingProvider: shipForm.shippingProvider,
          trackingNumber: shipForm.trackingNumber,
        });
        ElMessage.success('发货成功！');
        shipDialogVisible.value = false;
        fetchOrders();
      } catch (error) {
        ElMessage.error('发货失败，请重试');
      } finally {
        submitting.value = false;
      }
    }
  });
};

const handleBatchShip = async () => {
  if (!batchShipFormRef.value) return;
  await batchShipFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const promises = selectedOrders.value.map(order => 
          shipOrderByAdmin(order.id, {
            shippingProvider: batchShipForm.shippingProvider,
            trackingNumber: batchShipForm.trackingNumber,
          })
        );
        await Promise.all(promises);
        ElMessage.success(`成功为 ${selectedOrders.value.length} 个订单发货！`);
        batchShipDialogVisible.value = false;
        fetchOrders();
      } catch (error) {
        ElMessage.error('批量发货失败，请重试');
      } finally {
        submitting.value = false;
      }
    }
  });
};

const markAsCompleted = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要将订单 ${order.id} 标记为已完成吗？`,
      '确认操作',
      { confirmButtonText: '确定', cancelButtonText: '取消' }
    );
    
    await updateOrderByAdmin(order.id, { orderStatus: 'COMPLETED' });
    ElMessage.success('订单已标记为完成');
    fetchOrders();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试');
    }
  }
};

const viewOrderDetails = (order) => {
  currentOrder.value = order;
  detailDialogVisible.value = true;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleString('zh-CN');
};

const statusMap = {
  'AWAITING_MEETUP': { text: '待交易', type: 'warning' },
  'AWAITING_SHIPMENT': { text: '待发货', type: 'primary' },
  'SHIPPED': { text: '已发货', type: 'success' },
  'COMPLETED': { text: '已完成', type: 'success' },
  'CANCELLED': { text: '已取消', type: 'info' }
};

const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';

onMounted(() => {
  fetchOrders();
  fetchDeliveryStats();
});
</script>

<style scoped>
.delivery-management {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.awaiting {
  background: linear-gradient(135deg, #ff9a56, #ff6b6b);
}

.stat-icon.shipped {
  background: linear-gradient(135deg, #4ecdc4, #44a08d);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.total {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.actions {
  display: flex;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.order-detail {
  padding: 20px;
}

.order-detail h4 {
  margin: 20px 0 10px 0;
  color: #303133;
  font-weight: 500;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.el-table th) {
  background-color: #f5f7fa;
}

:deep(.el-descriptions) {
  margin-bottom: 16px;
}
</style>
