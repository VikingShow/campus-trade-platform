<template>
  <div class="order-management">
    <div class="toolbar">
      <h2 class="page-title">订单管理</h2>
      <div class="actions">
        <el-input
          v-model="filters.orderId"
          placeholder="按订单ID精确搜索"
          clearable
          @clear="handleFilterChange"
          @keyup.enter="handleFilterChange"
          style="width: 240px; margin-right: 10px;"
        />
        <el-select v-model="filters.deliveryMethod" placeholder="按配送方式筛选" clearable @change="handleFilterChange" style="width: 160px; margin-right: 10px;">
            <el-option label="所有方式" value=""></el-option>
            <el-option label="线下面交" value="MEETUP"></el-option>
            <el-option label="快递配送" value="SHIPPING"></el-option>
        </el-select>
        <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增订单</el-button>
      </div>
    </div>

    <el-table :data="orders" v-loading="loading" style="width: 100%">
      <el-table-column type="expand">
        <template #default="props">
          <div class="order-details">
            <p v-if="props.row.deliveryMethod === 'MEETUP'"><strong>交易地点:</strong> {{ props.row.meetupLocationName || '未指定' }}</p>
            <p v-if="props.row.deliveryMethod === 'MEETUP'"><strong>建议时间:</strong> {{ props.row.meetupTimeSlot || '未填写' }}</p>
            <p v-if="props.row.deliveryMethod === 'SHIPPING'"><strong>快递公司:</strong> {{ props.row.shippingProvider || '待填写' }}</p>
            <p v-if="props.row.deliveryMethod === 'SHIPPING'"><strong>快递单号:</strong> {{ props.row.trackingNumber || '待填写' }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="订单ID" width="100" />
      <el-table-column label="商品信息" min-width="250">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <el-image 
              :src="scope.row.productImage" 
              fit="cover" 
              style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0;"
              lazy
            />
            <span style="margin-left: 10px">{{ scope.row.productTitle }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="buyerNickname" label="买家" />
      <el-table-column prop="sellerNickname" label="卖家" />
      <el-table-column label="配送方式" width="120">
        <template #default="scope">
            <span :class="['status-tag', scope.row.deliveryMethod === 'SHIPPING' ? 'status-success' : 'status-info']">
                {{ scope.row.deliveryMethod === 'SHIPPING' ? '快递配送' : '线下面交' }}
            </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <span :class="['status-tag', getStatusType(scope.row.orderStatus) === 'success' ? 'status-success' : (getStatusType(scope.row.orderStatus) === 'danger' ? 'status-danger' : (getStatusType(scope.row.orderStatus) === 'warning' ? 'status-warning' : 'status-info'))]">
            {{ formatStatus(scope.row.orderStatus) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button 
            size="small" 
            type="danger" 
            @click="handleDelete(scope.row)"
          >
            删除
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

     <!-- 【修改】新增/编辑订单的对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <template v-if="!isEdit">
                <el-form-item label="选择商品" prop="productId">
                    <el-select v-model="form.productId" placeholder="搜索并选择一个在售商品" filterable remote :remote-method="searchProducts" :loading="productSearchLoading" style="width: 100%;">
                        <el-option v-for="p in availableProducts" :key="p.id" :label="p.title" :value="p.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="选择买家" prop="buyerId">
                     <el-select v-model="form.buyerId" placeholder="搜索并选择一个用户" filterable remote :remote-method="searchUsers" :loading="userSearchLoading" style="width: 100%;">
                        <el-option v-for="u in users" :key="u.id" :label="`${u.nickname} (${u.username})`" :value="u.id" />
                    </el-select>
                </el-form-item>
            </template>
            
            <el-form-item label="配送方式" prop="deliveryMethod">
                <el-radio-group v-model="form.deliveryMethod">
                    <el-radio-button label="MEETUP">线下面交</el-radio-button>
                    <el-radio-button label="SHIPPING">快递配送</el-radio-button>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="订单状态" prop="orderStatus">
                <el-select v-model="form.orderStatus" placeholder="选择新的订单状态" style="width: 100%;">
                    <el-option label="待发货" value="AWAITING_SHIPMENT"></el-option>
                    <el-option label="已发货" value="SHIPPED"></el-option>
                    <el-option label="待交易" value="AWAITING_MEETUP"></el-option>
                    <el-option label="已完成" value="COMPLETED"></el-option>
                    <el-option label="已取消" value="CANCELLED"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="订单价格" prop="totalPrice" v-if="isEdit">
                <el-input-number v-model="form.totalPrice" :min="0.01" :precision="2" />
            </el-form-item>

            <template v-if="form.deliveryMethod === 'MEETUP'">
                <el-form-item label="交易地点" prop="meetupLocationId">
                    <el-select v-model="form.meetupLocationId" placeholder="选择交易地点" style="width: 100%;" :loading="locationsLoading">
                        <el-option v-for="loc in locations" :key="loc.id" :label="loc.name" :value="loc.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="建议时间" prop="meetupTimeSlot">
                    <el-input v-model="form.meetupTimeSlot" />
                </el-form-item>
            </template>

            <template v-if="form.deliveryMethod === 'SHIPPING'">
                <el-form-item label="快递公司" prop="shippingProvider">
                    <el-input v-model="form.shippingProvider" />
                </el-form-item>
                <el-form-item label="快递单号" prop="trackingNumber">
                    <el-input v-model="form.trackingNumber" />
                </el-form-item>
            </template>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">确认</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { getAllOrdersAdmin, updateOrderByAdmin, deleteOrderAdmin, createOrderByAdmin, getAllProductsAdmin, getAllUsers, getAllLocationsAdmin } from '../../api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';

const orders = ref([]);
const locations = ref([]);
const availableProducts = ref([]);
const users = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const locationsLoading = ref(false);
const productSearchLoading = ref(false);
const userSearchLoading = ref(false);
const formRef = ref(null);

const filters = reactive({ orderId: '', deliveryMethod: '' });
const pagination = reactive({ page: 1, size: 10, total: 0 });
const form = reactive({
    id: null,
    productId: '',
    buyerId: '',
    orderStatus: 'AWAITING_MEETUP',
    totalPrice: 0.01,
    deliveryMethod: 'MEETUP', // 【新增】
    meetupLocationId: null,
    meetupTimeSlot: '',
    shippingProvider: '',
    trackingNumber: ''
});

const isEdit = computed(() => !!form.id);
const dialogTitle = computed(() => (isEdit.value ? '编辑订单' : '新增订单'));

const rules = {
    productId: [{ required: computed(() => !isEdit.value), message: '请选择一个商品', trigger: 'change' }],
    buyerId: [{ required: computed(() => !isEdit.value), message: '请选择一个买家', trigger: 'change' }],
    deliveryMethod: [{ required: true, message: '请选择配送方式', trigger: 'change' }],
    orderStatus: [{ required: true, message: '请选择订单状态', trigger: 'change' }],
};


const fetchOrders = async () => {
  loading.value = true;
  try {
    const params = { ...filters, page: pagination.page, size: pagination.size };
    const response = await getAllOrdersAdmin(params);
    orders.value = response.data.data.list;
    pagination.total = response.data.data.total;
  } catch (error) {
    ElMessage.error('加载订单列表失败');
  } finally {
    loading.value = false;
  }
};
const fetchLocations = async () => {
    locationsLoading.value = true;
    try {
        const response = await getAllLocationsAdmin();
        locations.value = response.data.data;
    } catch (error) {
        ElMessage.error("加载交易地点失败");
    } finally {
        locationsLoading.value = false;
    }
};

const searchProducts = async (query) => {
    if (!query) return;
    productSearchLoading.value = true;
    try {
        const res = await getAllProductsAdmin({ keyword: query, page: 1, size: 20 });
        availableProducts.value = res.data.data.list.filter(p => p.status === 'AVAILABLE');
    } catch (error) {
    } finally {
        productSearchLoading.value = false;
    }
};

const searchUsers = async (query) => {
    if (!query) return;
    userSearchLoading.value = true;
    try {
        const res = await getAllUsers({ keyword: query, page: 1, size: 20 });
        users.value = res.data.data.list;
    } catch (error) {
    } finally {
        userSearchLoading.value = false;
    }
};

const handleFilterChange = () => { pagination.page = 1; fetchOrders(); };

const handleSizeChange = (newSize) => {
  pagination.size = newSize;
  pagination.page = 1;
  fetchOrders();
};

const handleCurrentChange = (newPage) => {
  pagination.page = newPage;
  fetchOrders();
};

const openDialog = (row) => {
  resetForm();
  if (row) {
    Object.assign(form, row);
  }
  dialogVisible.value = true;
};

const resetForm = () => {
    Object.assign(form, { id: null, productId: '', buyerId: '', orderStatus: 'AWAITING_MEETUP', totalPrice: 0.01, deliveryMethod: 'MEETUP', meetupLocationId: null, meetupTimeSlot: '', shippingProvider: '', trackingNumber: '' });
    if (formRef.value) formRef.value.clearValidate();
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEdit.value) {
          await updateOrderByAdmin(form.id, form);
          ElMessage.success('订单信息更新成功');
        } else {
          await createOrderByAdmin(form);
          ElMessage.success('新增订单成功');
        }
        dialogVisible.value = false;
        fetchOrders();
      } catch (error) {} finally {
        submitting.value = false;
      }
    }
  });
};


const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要永久删除订单 [ID: ${row.id}] 吗? 此操作不可逆。`, '高危操作警告', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'error',
  }).then(async () => {
    try {
      await deleteOrderAdmin(row.id);
      ElMessage.success('删除成功');
      fetchOrders();
    } catch (error) {}
  });
};

const statusMap = { 'AWAITING_MEETUP': { text: '待交易', type: 'warning' }, 'AWAITING_SHIPMENT': { text: '待发货', type: 'primary' }, 'SHIPPED': { text: '已发货', type: 'success' }, 'COMPLETED': { text: '已完成', type: 'success' }, 'CANCELLED': { text: '已取消', type: 'info' } };
const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';

onMounted(() => {
  fetchOrders();
  fetchLocations();
});
</script>

<style>
.order-management {
  padding: 18px 0.5vw 0 0.5vw;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 18px;
  gap: 12px;
}
.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.pagination-container { display: flex; justify-content: center; margin-top: 20px; }
.order-details { padding: 10px 20px; background-color: #fafafa; }
.order-details p { margin: 5px 0; }
</style>