<template>
  <div class="user-management">
    <h2>用户管理</h2>

    <!-- 表格部分 -->
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
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
            :disabled="scope.row.role === 'ADMIN'"
          />
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
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getAllUsers, updateUserStatus } from '../../api/admin';
import { ElMessage } from 'element-plus';

const users = ref([]);
const loading = ref(false);

// 【新增】分页相关的状态数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const fetchUsers = async () => {
  loading.value = true;
  try {
    // 【修改】调用API时传入分页参数
    const params = {
        page: pagination.page,
        size: pagination.size,
    };
    const response = await getAllUsers(params);
    
    // 【修改】从分页结果中获取数据
    users.value = response.data.data.list;
    pagination.total = response.data.data.total;

  } catch (error) {
    ElMessage.error('加载用户列表失败');
  } finally {
    loading.value = false;
  }
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

onMounted(fetchUsers);
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
