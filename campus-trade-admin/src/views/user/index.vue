<template>
  <div>
    <h2>用户管理</h2>
    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名 (学号)" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="role" label="角色" />
      <!-- 【新增】信誉分列 -->
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllUsers, updateUserStatus } from '../../api/admin';
import { ElMessage } from 'element-plus';

const users = ref([]);
const loading = ref(false);

const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await getAllUsers();
    users.value = response.data.data;
  } catch (error) {
    ElMessage.error('加载用户列表失败');
  } finally {
    loading.value = false;
  }
};

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status);
    ElMessage.success('状态更新成功');
  } catch (error) {
    // 如果失败，将开关状态恢复
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.error('状态更新失败');
  }
};

onMounted(fetchUsers);
</script>
