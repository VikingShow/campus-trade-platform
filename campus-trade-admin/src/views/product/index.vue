<template>
  <div class="product-management">
    <h2>商品管理</h2>
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="按商品标题搜索"
        clearable
        @clear="fetchProducts"
        @keyup.enter="fetchProducts"
        style="width: 300px; margin-right: 10px;"
      >
        <template #append>
          <el-button :icon="Search" @click="fetchProducts" />
        </template>
      </el-input>
    </div>

    <el-table :data="products" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品信息" min-width="250">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <el-image 
              :src=scope.row.coverImage
              fit="cover" 
              style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0;"
              lazy
            />
            <span style="margin-left: 10px">{{ scope.row.title }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="sellerNickname" label="卖家" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="scope">￥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button 
            size="small" 
            type="danger" 
            @click="handleDelist(scope.row)" 
            v-if="scope.row.status !== 'DELISTED'"
          >
            强制下架
          </el-button>
           <span v-else>--</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 【新增】分页组件 -->
    <div class="pagination-container">
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

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getAllProductsAdmin, updateProductStatusAdmin } from '../../api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

const products = ref([]);
const loading = ref(false);
const searchKeyword = ref('');

// 【新增】分页相关的状态数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

// const backendUrl = 'http://localhost:8080';

// const fullImageUrl = (relativePath) => {
//     if (!relativePath) return '';
//     if (relativePath.startsWith('http')) return relativePath;
//     return `${backendUrl}${relativePath}`;
// };

const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = { 
      keyword: searchKeyword.value,
      page: pagination.page,
      size: pagination.size,
    };
    const response = await getAllProductsAdmin(params);
    products.value = response.data.data.list;
    pagination.total = response.data.data.total;
  } catch (error) {
    ElMessage.error('加载商品列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSizeChange = (newSize) => {
  pagination.size = newSize;
  pagination.page = 1;
  fetchProducts();
};

const handleCurrentChange = (newPage) => {
  pagination.page = newPage;
  fetchProducts();
};

const handleDelist = (row) => {
  ElMessageBox.confirm(`确定要强制下架商品 "${row.title}" 吗?`, '确认操作', {
    confirmButtonText: '下架',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await updateProductStatusAdmin(row.id, 'DELISTED');
      ElMessage.success('商品已下架');
      fetchProducts();
    } catch (error) {
      ElMessage.error('操作失败');
    }
  });
};

const statusMap = {
  'AVAILABLE': { text: '在售', type: 'success' },
  'SOLD': { text: '已售出', type: 'info' },
  'DELISTED': { text: '已下架', type: 'danger' }
};
const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';

onMounted(fetchProducts);
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
