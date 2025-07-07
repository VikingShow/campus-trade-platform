<template>
  <div class="location-management">
    <div class="toolbar">
      <h2 class="page-title">交易地点管理</h2>
      <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增地点</el-button>
    </div>

    <el-table :data="locations" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="地点名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <div class="action-btn-group">
            <el-button size="small" class="btn-primary" @click="openDialog(scope.row)">编辑</el-button>
            <el-button size="small" class="btn-danger" @click="handleDelete(scope.row)">删除</el-button>
          </div>
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

<style>
.location-management {
  padding: 18px 0.5vw 0 0.5vw;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 18px;
  gap: 12px;
}
.el-button {
  border-radius: 16px;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 22px;
  transition: background 0.2s, color 0.2s, box-shadow 0.2s;
}
.el-table {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 4px 24px 0 rgba(60,60,60,0.10);
  background: #fff;
  margin-bottom: 18px;
}
.el-table th {
  background: #f5f6fa !important;
  color: #222;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 1.5px solid #e0e5ec;
}
.el-table td {
  font-size: 15px;
  color: #222;
  border-bottom: 1px solid #e0e5ec;
}
.el-table__row:hover td {
  background: #e5e9f2 !important;
  color: #007aff;
}
.el-table__body tr.current-row > td {
  border-left: 4px solid #007aff;
  background: #f0f8ff !important;
}
.action-btn-group {
  display: flex;
  gap: 14px;
  justify-content: center;
  align-items: stretch;
  flex-wrap: wrap;
}
.action-btn-group .el-button {
  min-width: 60px;
  height: 40px;
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 18px;
  box-sizing: border-box;
}
.el-dialog {
  border-radius: 22px !important;
  box-shadow: 0 8px 32px 0 rgba(60,60,60,0.12);
  background: #fff;
}
.el-dialog__header {
  font-size: 1.3em;
  font-weight: 700;
  color: #222;
  padding-bottom: 8px;
}
.el-form {
  padding: 8px 0 0 0;
}
.el-form-item {
  margin-bottom: 18px;
}
.el-form-item__label {
  font-weight: 600;
  color: #222;
  font-size: 15px;
}
@media (max-width: 900px) {
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .el-table {
    font-size: 13px;
  }
  .action-btn-group {
    gap: 8px;
  }
  .action-btn-group .el-button {
    min-width: 48px;
    height: 34px;
    font-size: 14px;
    padding: 0 10px;
  }
}
</style>
