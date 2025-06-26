<template>
  <el-card>
    <template #header>
      <h3>{{ isEdit ? '编辑商品信息' : '发布新的二手宝贝' }}</h3>
    </template>
    <el-form :model="form" :rules="rules" label-width="120px" ref="formRef" style="max-width: 600px; margin: auto;">
      <el-form-item label="商品图片" prop="coverImage">
        <el-upload
          class="product-uploader"
          action="/api/files/upload"
          :show-file-list="false"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
          :headers="{ 'Authorization': `Bearer ${authStore.token}` }"
        >
          <img v-if="form.coverImage" :src="form.coverImage" class="product-image" alt="已上传图片"/>
          <el-icon v-else class="uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="一个响亮的标题能吸引更多人"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" rows="4" placeholder="详细描述一下你的宝贝"></el-input>
      </el-form-item>
       <el-form-item label="价格" prop="price">
        <el-input-number v-model="form.price" :precision="2" :step="1" :min="0.01" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
         <el-select v-model="form.categoryId" placeholder="选择分类" :loading="categoriesLoading">
             <el-option 
               v-for="category in categories" 
               :key="category.id" 
               :label="category.name" 
               :value="category.id"
             />
         </el-select>
      </el-form-item>
      <el-form-item label="新旧程度" prop="conditionLevel">
         <el-rate v-model="form.conditionLevel" :texts="['成色一般', '明显使用', '轻微使用', '几乎全新', '全新']" show-text />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新商品' : '确认发布' }}
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { ElMessage } from 'element-plus';
import { createProduct, getProductById, updateProduct } from '../api/product';
import apiClient from '../api/axios.config';
import { Plus } from '@element-plus/icons-vue';
import ProductDetail from './ProductDetail.vue';

const props = defineProps({ id: String });
const router = useRouter();
const authStore = useAuthStore();
const formRef = ref(null);
const submitting = ref(false);

const form = reactive({
  title: '',
  description: '',
  price: 0.01,
  categoryId: '',
  conditionLevel: 3,
  coverImage: '',
});

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  conditionLevel: [{ required: true, message: '请选择新旧程度', trigger: 'change' }],
  coverImage: [{ required: true, message: '请上传商品图片', trigger: 'change' }],
};

const isEdit = computed(() => !!props.id);

const categories = ref([]);
const categoriesLoading = ref(false);

const backendUrl = 'http://localhost:8080';
const fullImageUrl = computed(() => {
    if (!form.coverImage) return '';
    if (form.coverImage.startsWith('http')) return form.coverImage;
    return `${backendUrl}${form.coverImage}`;
});

const handleUploadSuccess = (response) => {
  form.coverImage = response.data.url;
  ElMessage.success('图片上传成功');
};

const beforeUpload = (rawFile) => {
  const isJpgOrPng = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
  if (!isJpgOrPng) {
    ElMessage.error('图片必须是 JPG 或 PNG 格式!');
    return false;
  }
  const isLt5M = rawFile.size / 1024 / 1024 < 5;
   if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!');
    return false;
  }
  return true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if(valid) {
      submitting.value = true;
      try {
        if (isEdit.value) {
          await updateProduct(props.id, form);
          ElMessage.success('更新成功');
          router.push(`/product/${props.id}`);
        } else {
          await createProduct(form);
          ElMessage.success('发布成功');
          router.push('/');
        }
      } catch (error) {
         console.error(error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const fetchProductData = async (id) => {
    try {
        const res = await getProductById(id);
        Object.assign(form, res.data.data);
    } catch(error) {
        console.error(error);
        ElMessage.error('加载商品数据失败');
        router.push('/');
    }
}

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

onMounted(() => {
  fetchCategories();
  if (isEdit.value) {
    fetchProductData(props.id);
  }
});
</script>

<style>
.product-uploader .el-upload { border: 1px dashed var(--el-border-color); border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; transition: var(--el-transition-duration-fast); }
.product-uploader .el-upload:hover { border-color: var(--el-color-primary); }
.product-image { width: 178px; height: 178px; display: block; }
.uploader-icon { font-size: 28px; color: #8c939d; width: 178px; height: 178px; text-align: center; }
</style>