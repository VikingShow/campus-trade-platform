<template>
  <el-container>
    <el-main>
      <!-- Search and Filter Bar -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="18">
           <el-input v-model="searchQuery" placeholder="搜索你感兴趣的宝贝..." clearable @clear="fetchProducts" @keyup.enter="fetchProducts">
              <template #append>
                <el-button :icon="Search" @click="fetchProducts" />
              </template>
           </el-input>
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedCategory" placeholder="按分类筛选" style="width: 100%;" @change="fetchProducts" clearable>
            <el-option label="全部分类" value=""></el-option>
            <el-option 
              v-for="category in categories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id"
            />
          </el-select>
        </el-col>
      </el-row>
      
      <!-- Products List -->
      <el-row :gutter="20" v-loading="loading">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.id" style="margin-bottom: 20px;">
          <el-card shadow="hover" class="product-card" @click="goToDetail(product.id)">
             <!-- 【最终修正】重新使用 fullImageUrl 函数来正确拼接图片路径 -->
             <img :src="fullImageUrl(product.coverImage)" class="product-image" alt="商品图片" @error="onImageError"/>
             <div class="product-info">
               <p class="product-title">{{ product.title }}</p>
               <div class="bottom">
                 <span class="product-price">¥{{ product.price }}</span>
                 <span class="seller-info">{{ product.sellerNickname }}</span>
               </div>
             </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loading && products.length === 0" description="暂时没有符合条件的商品" />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import { getProducts } from '../api/product';
import apiClient from '../api/axios.config';

const router = useRouter();
const products = ref([]);
const categories = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const selectedCategory = ref('');

// 【最终修正】恢复 backendUrl 和 fullImageUrl 函数的定义
const backendUrl = 'http://localhost:8080';
const fullImageUrl = (relativePath) => {
    if (!relativePath) return ''; // 如果路径为空，返回空字符串
    if (relativePath.startsWith('http')) return relativePath; // 如果已经是完整URL，直接返回
    return `${backendUrl}${relativePath}`; // 否则，拼接成完整URL
};

const fetchProducts = async () => {
    loading.value = true;
    try {
        const params = {
            keyword: searchQuery.value,
            categoryId: selectedCategory.value,
        };
        const response = await getProducts(params);
        products.value = response.data.data;
    } catch (error) {
        console.error("获取商品列表失败:", error);
    } finally {
        loading.value = false;
    }
};

const fetchCategories = async () => {
    try {
        const response = await apiClient.get('/categories');
        categories.value = response.data.data;
    } catch (error) {
        console.error("获取分类列表失败:", error);
    }
};

const goToDetail = (id) => {
    router.push(`/product/${id}`);
};

const onImageError = (e) => {
    e.target.src = 'https://placehold.co/400x300/e8e8e8/969696?text=Image+Not+Found';
};

onMounted(() => {
    fetchProducts();
    fetchCategories();
});
</script>

<style scoped>
.product-card { cursor: pointer; }
.product-image { width: 100%; height: 200px; object-fit: cover; display: block; border-radius: 4px; }
.product-info { padding: 14px; }
.product-title { font-size: 16px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0 0 8px 0; }
.bottom { display: flex; justify-content: space-between; align-items: center; }
.product-price { font-size: 18px; color: #F56C6C; font-weight: bold; }
.seller-info { font-size: 13px; color: #909399; }
</style>
