<template>
  <div class="location-management">
    <h2>交易地点管理</h2>
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增地点</el-button>
    </div>

    <el-table :data="locations" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="地点名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Dialog for Add/Edit -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="地点名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
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
import { getAllLocationsAdmin, addLocationAdmin, updateLocationAdmin, deleteLocationAdmin } from '../../api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const locations = ref([]);
const loading = ref(false);
const submitting = ref(false);

const dialogVisible = ref(false);
const formRef = ref(null);
const form = reactive({
  id: null,
  name: '',
  description: '',
});

const rules = {
  name: [{ required: true, message: '请输入地点名称', trigger: 'blur' }],
};

const dialogTitle = computed(() => (form.id ? '编辑地点' : '新增地点'));

const fetchLocations = async () => {
  loading.value = true;
  try {
    const response = await getAllLocationsAdmin();
    locations.value = response.data.data;
  } catch (error) {
    ElMessage.error('加载地点列表失败');
  } finally {
    loading.value = false;
  }
};

const openDialog = (rowData) => {
  // Reset form
  form.id = null;
  form.name = '';
  form.description = '';
  if (rowData) {
    // Edit mode
    form.id = rowData.id;
    form.name = rowData.name;
    form.description = rowData.description;
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (form.id) {
          // Update
          await updateLocationAdmin(form.id, { name: form.name, description: form.description });
          ElMessage.success('更新成功');
        } else {
          // Add
          await addLocationAdmin({ name: form.name, description: form.description });
          ElMessage.success('新增成功');
        }
        dialogVisible.value = false;
        fetchLocations();
      } catch (error) {
        ElMessage.error('操作失败');
      } finally {
        submitting.value = false;
      }
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除地点 "${row.name}" 吗?`, '确认删除', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteLocationAdmin(row.id);
      ElMessage.success('删除成功');
      fetchLocations();
    } catch (error) {
      ElMessage.error('删除失败');
    }
  });
};

onMounted(fetchLocations);
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
</style>
