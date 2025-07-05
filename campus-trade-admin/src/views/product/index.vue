<template>
  <div class="product-management">
    <div class="toolbar">
      <h2>商品管理</h2>
      <div class="actions">
        <el-input
          v-model="filters.keyword"
          placeholder="按商品标题搜索"
          clearable
          @clear="handleFilterChange"
          @keyup.enter="handleFilterChange"
          style="width: 240px; margin-right: 10px;"
        />
        <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增商品</el-button>
      </div>
    </div>

    <el-table :data="products" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品信息" min-width="250">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <el-image 
              :src="scope.row.coverImage" 
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
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button 
            v-if="scope.row.status !== 'DELISTED'"
            size="small" 
            type="warning" 
            @click="handleStatusUpdate(scope.row, 'DELISTED')" 
            style="margin: 0 10px;"
          >
            下架
          </el-button>
           <el-button 
            v-else
            size="small" 
            type="success" 
            @click="handleStatusUpdate(scope.row, 'AVAILABLE')"
            style="margin: 0 10px;"
          >
            上架
          </el-button>
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

    <!-- 新增/编辑商品的对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="卖家" prop="sellerId">
            <el-select v-model="form.sellerId" placeholder="请选择卖家" filterable style="width: 100%;" :disabled="isEdit">
                <el-option v-for="user in users" :key="user.id" :label="`${user.nickname} (${user.username})`" :value="user.id" />
            </el-select>
        </el-form-item>
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" :loading="categoriesLoading">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="新旧程度" prop="conditionLevel">
          <el-rate v-model="form.conditionLevel" />
        </el-form-item>
        <el-form-item label="商品图片" prop="fileList" v-if="!isEdit">
            <!-- 【关键修正】使用 on-change 钩子来处理文件状态变化 -->
            <el-upload
                v-model:file-list="form.fileList"
                action="/api/files/upload"
                list-type="picture-card"
                :multiple="true"
                :limit="3"
                :on-change="handleChange"
                :on-remove="handleRemove"
                :on-preview="handlePictureCardPreview"
                :headers="uploadHeaders"
                :class="{ 'hide-upload': form.fileList.length >= 3 }"
            >
                <el-icon><Plus /></el-icon>
            </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确认</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="previewVisible">
      <img w-full :src="previewImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useAuthStore } from '../../stores/authStore';
import { getAllProductsAdmin, updateProductStatusAdmin, updateProductByAdmin, deleteProductAdmin, createProductByAdmin, getAllUsers } from '../../api/admin';
import apiClient from '../../api/axios.config';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const products = ref([]);
const users = ref([]);
const categories = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);
const categoriesLoading = ref(false);

const filters = reactive({ keyword: '' });
const pagination = reactive({ page: 1, size: 10, total: 0 });
const form = reactive({
  id: null,
  sellerId: '',
  title: '',
  description: '',
  price: 0.01,
  categoryId: '',
  conditionLevel: 3,
  fileList: [],
});

const isEdit = computed(() => !!form.id);
const dialogTitle = computed(() => (isEdit.value ? '编辑商品' : '新增商品'));

// 【关键修正】创建一个计算属性来动态生成包含 "Bearer " 前缀的请求头
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}));

const rules = {
  sellerId: [{ required: true, message: '请选择卖家', trigger: 'change' }],
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  fileList: [{ type: 'array', required: computed(() => !isEdit.value), message: '请至少上传一张图片', trigger: 'change' }]
};
const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = { ...filters, ...pagination };
    const response = await getAllProductsAdmin(params);
    products.value = response.data.data.list;
    pagination.total = response.data.data.total;
  } catch (error) {
    ElMessage.error('加载商品列表失败');
  } finally {
    loading.value = false;
  }
};

const fetchCategories = async () => {
    categoriesLoading.value = true;
    try {
        const response = await apiClient.get('/categories');
        categories.value = response.data.data;
    } catch (error) {
        ElMessage.error('获取分类列表失败');
    } finally {
        categoriesLoading.value = false;
    }
};

const fetchAllUsersForSelect = async () => {
    try {
        // 获取所有用户用于下拉选择，不分页
        const response = await getAllUsers({ page: 1, size: 1000 }); 
        users.value = response.data.data.list;
    } catch (error) {
        ElMessage.error('加载用户列表失败');
    }
};

const handleFilterChange = () => { pagination.page = 1; fetchProducts(); };
const handleSizeChange = (newSize) => { pagination.size = newSize; fetchProducts(); };
const handleCurrentChange = (newPage) => { pagination.page = newPage; fetchProducts(); };

const openDialog = (rowData) => {
  resetForm();
  if (rowData) { // 编辑模式
    Object.assign(form, rowData);
  }
  dialogVisible.value = true;
};

const resetForm = () => { Object.assign(form, { id: null, sellerId: '', title: '', description: '', price: 0.01, categoryId: '', conditionLevel: 3, fileList: [] }); };

const handleUploadSuccess = (response, uploadFile, uploadFiles) => { 
  // 确保 uploadFile 有 response 属性
  if (uploadFile.response) {
    uploadFile.url = uploadFile.response.data.url;
  }
  form.fileList = uploadFiles; 
};
// 【最终修正】使用 on-change 钩子，它在文件状态改变时（包括上传成功）触发
const handleChange = (uploadFile, uploadFiles) => {
  // 当文件上传成功时，它的 status 会变为 'success'，并且会包含 response
  if (uploadFile.status === 'success') {
    // 我们从服务器返回的 response 中获取真实的 URL，并设置到文件对象的 url 属性上
    // el-upload 组件会使用这个 url 属性来显示预览图
    if (uploadFile.response && uploadFile.response.data && uploadFile.response.data.url) {
      uploadFile.url = uploadFile.response.data.url;
    }
  }
  // 保持我们的数据列表与组件的内部列表同步
  form.fileList = uploadFiles;
};
const handleRemove = (uploadFile, uploadFiles) => { form.fileList = uploadFiles; };
// 【最终修正】定义图片预览函数
const handlePictureCardPreview = (uploadFile) => {
    previewImageUrl.value = uploadFile.url;
    previewVisible.value = true;
};
const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        // 【最终修正】从 file.url 获取路径，并过滤掉未上传成功的文件
        const imageUrls = form.fileList
            .filter(file => file.status === 'success' && file.url)
            .map(file => file.url);

        if (imageUrls.length === 0 && !isEdit.value) {
            ElMessage.error('请至少上传一张图片并等待上传成功。');
            submitting.value = false;
            return;
        }

        const payload = {
          sellerId: form.sellerId,
          title: form.title,
          description: form.description,
          price: form.price,
          categoryId: form.categoryId,
          conditionLevel: form.conditionLevel,
          coverImage: imageUrls[0],
          imageUrls: imageUrls.slice(1)
        };

        if (isEdit.value) {
          await updateProductByAdmin(form.id, payload);
          ElMessage.success('商品信息更新成功');
        } else {
          await createProductByAdmin(payload);
          ElMessage.success('新增商品成功');
        }
        dialogVisible.value = false;
        fetchProducts();
      } catch (error) {
        console.error("提交失败:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};
const handleStatusUpdate = (row, status) => {
  ElMessageBox.confirm(`确定要将商品 "${row.title}" 的状态更新为 [${status === 'AVAILABLE' ? '在售' : '已下架'}] 吗?`, '确认操作', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await updateProductStatusAdmin(row.id, status);
      ElMessage.success('状态更新成功');
      fetchProducts();
    } catch (error) {}
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要永久删除商品 "${row.title}" 吗? 此操作不可逆，将一并删除相关订单和收藏记录。`, '高危操作警告', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'error',
  }).then(async () => {
    try {
      await deleteProductAdmin(row.id);
      ElMessage.success('删除成功');
      fetchProducts();
    } catch (error) {}
  });
};

const statusMap = { 'AVAILABLE': { text: '在售', type: 'success' }, 'SOLD': { text: '已售出', type: 'info' }, 'DELISTED': { text: '已下架', type: 'danger' } };
const formatStatus = (status) => statusMap[status]?.text || '未知';
const getStatusType = (status) => statusMap[status]?.type || 'info';

onMounted(() => {
  fetchProducts();
  fetchCategories();
  fetchAllUsersForSelect(); // 加载用户列表
});
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.actions { display: flex; align-items: center; }
.pagination-container { display: flex; justify-content: center; margin-top: 20px; }
/* 用于隐藏上传按钮的CSS */
.hide-upload .el-upload--picture-card {
    display: none;
}
</style>