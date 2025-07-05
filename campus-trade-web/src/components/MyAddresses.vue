<template>
  <div>
    <div class="address-header">
      <h3>我的收货地址</h3>
      <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增收货地址</el-button>
    </div>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="12" v-for="address in addresses" :key="address.id" style="margin-bottom: 20px;">
        <el-card class="address-card" :class="{'is-default': address.isDefault}">
          <div class="card-header">
            <span>{{ address.recipientName }}</span>
            <el-tag v-if="address.isDefault" type="success" size="small">默认地址</el-tag>
          </div>
          <div class="card-body">
            <p>{{ address.phone }}</p>
            <p>{{ `${address.province} ${address.city} ${address.district} ${address.detailedAddress}` }}</p>
          </div>
          <div class="card-footer">
            <el-button text type="primary" @click="handleSetDefault(address.id)" v-if="!address.isDefault">设为默认</el-button>
            <span v-else class="default-text"></span>
            <div>
              <el-button text type="primary" @click="openDialog(address)">编辑</el-button>
              <el-button text type="danger" @click="handleDelete(address.id)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && addresses.length === 0" description="您还没有添加任何收货地址"></el-empty>

    <!-- 新增/编辑地址的对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="收件人姓名" prop="recipientName">
          <el-input v-model="form.recipientName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="所在地区" prop="province">
            <el-input v-model="form.province" placeholder="省份" style="width: 32%; margin-right: 2%;" />
            <el-input v-model="form.city" placeholder="城市" style="width: 32%; margin-right: 2%;" />
            <el-input v-model="form.district" placeholder="区/县" style="width: 32%;" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input v-model="form.detailedAddress" type="textarea" />
        </el-form-item>
        <el-form-item label=" ">
          <el-checkbox v-model="form.isDefault">设为默认收货地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { getAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '../api/address';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const addresses = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);

const form = reactive({
  id: null,
  recipientName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailedAddress: '',
  isDefault: false
});

const dialogTitle = computed(() => form.id ? '编辑收货地址' : '新增收货地址');
const rules = {
  recipientName: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区/县', trigger: 'blur' }],
  detailedAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
};

const fetchAddresses = async () => {
  loading.value = true;
  try {
    const response = await getAddresses();
    addresses.value = response.data.data;
  } catch (error) {
    ElMessage.error("加载地址列表失败");
  } finally {
    loading.value = false;
  }
};

const openDialog = (address) => {
  if (address) {
    // 编辑模式
    Object.assign(form, address);
  } else {
    // 新增模式
    resetForm();
  }
  dialogVisible.value = true;
};

const resetForm = () => {
    Object.assign(form, {
        id: null, recipientName: '', phone: '', province: '', city: '', district: '', detailedAddress: '', isDefault: false
    });
    if (formRef.value) {
        formRef.value.clearValidate();
    }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (form.id) {
          await updateAddress(form.id, form);
          ElMessage.success('地址更新成功');
        } else {
          await addAddress(form);
          ElMessage.success('新地址添加成功');
        }
        dialogVisible.value = false;
        fetchAddresses(); // 刷新列表
      } catch (error) {
        // 错误信息已由axios拦截器处理
      } finally {
        submitting.value = false;
      }
    }
  });
};

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个地址吗?', '警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteAddress(id);
      ElMessage.success('地址已删除');
      fetchAddresses(); // 刷新列表
    } catch (error) {}
  });
};

const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id);
    ElMessage.success('默认地址设置成功');
    fetchAddresses(); // 刷新列表以更新默认状态
  } catch (error) {}
};

onMounted(fetchAddresses);
</script>

<style scoped>
.address-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.address-card { border-left: 5px solid transparent; }
.address-card.is-default { border-left-color: var(--el-color-success); }
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; margin-bottom: 10px; }
.card-body p { margin: 5px 0; color: #606266; }
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 15px; border-top: 1px solid #f0f0f0; padding-top: 10px; }
.default-text { flex-grow: 1; }
</style>
