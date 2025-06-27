<template>
  <el-card>
    <template #header>
      <h3>{{ isEdit ? '编辑商品信息' : '发布新的二手宝贝' }}</h3>
    </template>
    <el-form :model="form" :rules="rules" label-width="120px" ref="formRef" style="max-width: 600px; margin: auto;">
      
      <el-form-item label="商品图片" prop="fileList">
        <el-upload
          v-model:file-list="form.fileList"
          action="/api/files/upload"
          list-type="picture-card"
          :multiple="true"
          :limit="3"
          :on-success="handleUploadSuccess"
          :on-remove="handleRemove"
          :on-preview="handlePictureCardPreview"
          :before-upload="beforeUpload"
          :headers="{ 'Authorization': `Bearer ${authStore.token}` }"
          :class="{ 'hide-upload': form.fileList.length >= 3 }"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <p class="upload-tip">第一张将作为封面图，最多上传3张图片。</p>
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

    <el-dialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>
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
  fileList: [], 
});

const dialogImageUrl = ref('');
const dialogVisible = ref(false);

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  fileList: [{ type: 'array', required: true, message: '请至少上传一张图片', trigger: 'change' }]
};

const isEdit = computed(() => !!props.id);
const categories = ref([]);
const categoriesLoading = ref(false);

const handleUploadSuccess = (response, uploadFile, uploadFiles) => {
  uploadFile.url = response.data.url;
  // 更新整个 fileList 以确保响应性
  form.fileList = [...uploadFiles]; 
  ElMessage.success('图片上传成功');
};

const handleRemove = (uploadFile, uploadFiles) => {
  form.fileList = uploadFiles;
};

const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url;
  dialogVisible.value = true;
};

const beforeUpload = (rawFile) => {
  const isAcceptedType = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(rawFile.type);
  if (!isAcceptedType) {
    ElMessage.error('图片必须是 JPG, PNG, GIF 或 WEBP 格式!');
    return false;
  }
  const isLt10M = rawFile.size / 1024 / 1024 < 10;
   if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!');
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
        const imageUrls = form.fileList.map(file => file.url);
        const payload = {
          title: form.title,
          description: form.description,
          price: form.price,
          categoryId: form.categoryId,
          conditionLevel: form.conditionLevel,
          coverImage: imageUrls[0], 
          imageUrls: imageUrls.slice(1)
        };

        if (isEdit.value) {
          await updateProduct(props.id, payload);
          ElMessage.success('更新成功');
          router.push(`/product/${props.id}`);
        } else {
          await createProduct(payload);
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
        const productData = res.data.data;
        
        form.title = productData.title;
        form.description = productData.description;
        form.price = productData.price;
        form.categoryId = productData.categoryId;
        form.conditionLevel = productData.conditionLevel;
        
        const images = [productData.coverImage, ...(productData.imageUrls || [])].filter(Boolean);
        form.fileList = images.map((url) => ({
            // 从 URL 中提取真实的文件名，如果失败则使用一个默认名
            name: url.substring(url.lastIndexOf('/') + 1) || 'image.png',
            url: url,
        }));
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
.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
.hide-upload .el-upload--picture-card {
    display: none;
}
</style>
