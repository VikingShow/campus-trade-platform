<template>
  <div class="user-management">
    <div class="toolbar">
      <h2>用户管理</h2>
      <div class="actions">
        <!-- 【新增】筛选控件 -->
        <el-input
          v-model="filters.keyword"
          placeholder="按学号或昵称搜索"
          clearable
          @clear="handleFilterChange"
          @keyup.enter="handleFilterChange"
          style="width: 200px; margin-right: 10px;"
        />
        <el-select v-model="filters.role" placeholder="按角色筛选" clearable @change="handleFilterChange" style="width: 140px; margin-right: 10px;">
            <el-option label="所有角色" value=""></el-option>
            <el-option label="普通用户" value="USER"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
        </el-select>
        <el-select v-model="filters.status" placeholder="按状态筛选" clearable @change="handleFilterChange" style="width: 140px; margin-right: 10px;">
            <el-option label="所有状态" value=""></el-option>
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
        </el-select>
        <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增用户</el-button>
      </div>
    </div>

    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名 (学号)" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="creditScore" label="信誉分" width="100" align="center" sortable />
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
            :disabled="scope.row.id === authStore.user?.id"
            style="margin: 0 10px;"
          />
          <el-button 
            size="small" 
            type="danger" 
            @click="handleDelete(scope.row)"
            :disabled="scope.row.id === authStore.user?.id"
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
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>


    <!-- 新增/编辑用户的对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名 (学号)" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password :placeholder="isEdit ? '留空则不修改密码' : '请输入初始密码'" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="USER"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="信誉分" prop="creditScore">
          <el-input-number v-model="form.creditScore" :min="0" />
        </el-form-item>
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
import { useAuthStore } from '../../stores/authStore';
import { getAllUsers, updateUserStatus, createUserAdmin, updateUserAdmin, deleteUserAdmin } from '../../api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search } from '@element-plus/icons-vue'; // 【新增】导入Search图标

const filters = reactive({
  keyword: '',
  role: '',
  status: null,
});
const authStore = useAuthStore();
const users = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);
const searchKeyword = ref(''); // 【新增】搜索关键词

const pagination = reactive({ page: 1, size: 10, total: 0 });

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  password: '',
  role: 'USER',
  creditScore: 100,
});

const isEdit = computed(() => !!form.id);
const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '新增用户'));

const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: computed(() => !isEdit.value), message: '请输入初始密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
});

const fetchUsers = async () => {
  loading.value = true;
  try {
    const params = {
        ...filters, // 将所有筛选条件加入请求参数
        page: pagination.page,
        size: pagination.size,
    };
    const response = await getAllUsers(params);
    users.value = response.data.data.list;
    pagination.total = response.data.data.total;
  } catch (error) {
    ElMessage.error('加载用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 【新增】当任何筛选条件变化时，都调用此函数
const handleFilterChange = () => {
    pagination.page = 1; // 每次筛选都回到第一页
    fetchUsers();
};

// 【新增】处理每页显示条数变化
const handleSizeChange = (newSize) => {
  pagination.size = newSize;
  pagination.page = 1; // 切换每页条数时，回到第一页
  fetchUsers();
};

// 【新增】处理页码变化
const handleCurrentChange = (newPage) => {
  pagination.page = newPage;
  fetchUsers();
};


const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status);
    ElMessage.success('状态更新成功');
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error('状态更新失败');
  }
};

const openDialog = (rowData) => {
  resetForm();
  if (rowData) { // 编辑模式
    Object.assign(form, rowData);
    form.password = ''; // 编辑时不显示密码
  }
  dialogVisible.value = true;
};

const resetForm = () => {
  form.id = null;
  form.username = '';
  form.nickname = '';
  form.password = '';
  form.role = 'USER';
  form.creditScore = 100;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEdit.value) {
          await updateUserAdmin(form.id, form);
          ElMessage.success('更新成功');
        } else {
          await createUserAdmin(form);
          ElMessage.success('新增成功');
        }
        dialogVisible.value = false;
        fetchUsers();
      } catch (error) {
        // 错误信息已由拦截器处理
      } finally {
        submitting.value = false;
      }
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要永久删除用户 "${row.nickname}" 吗? 此操作不可逆。`, '警告', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteUserAdmin(row.id);
      ElMessage.success('删除成功');
      fetchUsers();
    } catch (error) {}
  });
};

onMounted(fetchUsers);
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.actions {
  display: flex;
  align-items: center;
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>