<template>
  <el-container>
    <el-main>
      <h2 class="page-title">发现好物</h2>
      <!-- 筛选与排序工具栏 -->
      <el-card class="filter-card" shadow="never">
        <el-row :gutter="20" align="middle">
          <!-- 关键词搜索 -->
          <el-col :span="10">
            <el-input v-model="filters.keyword" placeholder="搜索你感兴趣的宝贝..." clearable @clear="handleFilterChange" @keyup.enter="handleFilterChange">
              <template #append>
                <el-button :icon="Search" @click="handleFilterChange" />
              </template>
            </el-input>
          </el-col>

          <!-- 分类筛选 -->
          <el-col :span="4">
            <el-select v-model="filters.categoryId" placeholder="全部分类" style="width: 100%;" @change="handleFilterChange" clearable>
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id"
              />
            </el-select>
          </el-col>

          <!-- 价格区间筛选 -->
          <el-col :span="6">
            <div class="price-range">
              <el-input-number v-model="filters.minPrice" :min="0" :precision="2" controls-position="right" placeholder="最低价" style="flex: 1;" @change="handleFilterChange" />
              <span class="price-separator">-</span>
              <el-input-number v-model="filters.maxPrice" :min="filters.minPrice || 0" :precision="2" controls-position="right" placeholder="最高价" style="flex: 1;" @change="handleFilterChange" />
            </div>
          </el-col>
          
          <!-- 排序方式 -->
          <el-col :span="4">
            <el-select v-model="filters.orderBy" style="width: 100%;" @change="handleFilterChange">
              <el-option label="最新发布" value="latest"></el-option>
              <el-option label="价格从低到高" value="price_asc"></el-option>
              <el-option label="价格从高到低" value="price_desc"></el-option>
            </el-select>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 商品列表 -->
      <el-row :gutter="20" v-loading="loading" style="margin-top: 20px;">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in products" :key="product.id" style="margin-bottom: 20px;">
          <el-card shadow="hover" class="product-card home-recommend-card" @click="goToDetail(product.id)">
            <div class="image-container">
                <img :src="product.coverImage" class="product-image" alt="商品图片" @error="onImageError"/>
                <!-- 【新增】如果商品有多张图片，显示一个角标 -->
                <div v-if="product.imageUrls && product.imageUrls.length > 0" class="image-count-overlay">
                    <el-icon><CameraFilled /></el-icon>
                    <span>1 / {{ 1 + product.imageUrls.length }}</span>
                </div>
             </div>
             <div class="product-info">
               <p class="product-title">{{ product.title }}</p>
               <div class="bottom">
                 <span class="status-tag status-danger price-tag">¥{{ product.price }}</span>
                 <span class="status-tag status-info">{{ product.sellerNickname }}</span>
               </div>
             </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loading && products.length === 0" description="暂时没有符合条件的商品">
        <template #image>
          <el-icon style="font-size:48px;color:var(--color-primary)"><Goods /></el-icon>
        </template>
      </el-empty>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { Search, CameraFilled, Goods } from '@element-plus/icons-vue';
import { getProducts } from '../api/product';
import apiClient from '../api/axios.config';
import { debounce } from '../utils/debounce';

const router = useRouter();
const products = ref([]);
const categories = ref([]);
const loading = ref(false);

const filters = reactive({
  keyword: '',
  categoryId: '',
  minPrice: undefined,
  maxPrice: undefined,
  orderBy: 'latest',
});

const fetchProducts = async () => {
    loading.value = true;
    try {
        const params = {};
        for (const key in filters) {
            if (filters[key] !== '' && filters[key] !== undefined && filters[key] !== null) {
                params[key] = filters[key];
            }
        }
        const response = await getProducts(params);
        // 【最终修正】对返回的数据进行分组处理，以合并同一个商品的多张图片
        const productMap = new Map();
        response.data.data.forEach(item => {
            if (!productMap.has(item.id)) {
                productMap.set(item.id, { ...item, imageUrls: [] });
            }
            if (item.imageUrls && item.imageUrls[0]) { // imageUrls[0] is the joined image_url
                 const existingProduct = productMap.get(item.id);
                 // 避免重复添加封面图
                 if (item.imageUrls[0] !== existingProduct.coverImage) {
                    existingProduct.imageUrls.push(item.imageUrls[0]);
                 }
            }
        });
        products.value = Array.from(productMap.values());

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

const goToDetail = (id) => { router.push(`/product/${id}`); };
const onImageError = (e) => { e.target.src = 'https://placehold.co/400x300/e8e8e8/969696?text=Image+Not+Found'; };

const handleFilterChange = debounce(() => {
    fetchProducts();
}, 500);

onMounted(() => {
    fetchProducts();
    fetchCategories();
});
</script>

<style>
.filter-card {
    margin-bottom: 20px;
    border-radius: 18px;
    box-shadow: 0 4px 24px 0 rgba(60,60,60,0.10);
    background: #fff;
}
.price-range {
    display: flex;
    align-items: center;
}
.price-separator {
    margin: 0 10px;
    color: #909399;
}
.product-card {
    cursor: pointer;
    border-radius: 18px;
    box-shadow: 0 4px 24px 0 rgba(60,60,60,0.10);
    background: #fff;
    transition: box-shadow 0.2s;
}
.product-card:hover {
    box-shadow: 0 8px 32px 0 rgba(0,122,255,0.12);
}
.image-container {
    position: relative;
    width: 100%;
    height: 200px;
}
.product-image { width: 100%; height: 200px; object-fit: cover; display: block; border-radius: 12px; }
.image-count-overlay {
    position: absolute;
    bottom: 8px;
    right: 8px;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
    display: flex;
    align-items: center;
}
.image-count-overlay .el-icon {
    margin-right: 4px;
}
.product-info { padding: 14px; }
.product-title { font-size: 16px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0 0 8px 0; }
.bottom { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
.home-recommend-card {
  border: 2px solid transparent;
  transition: border 0.2s, box-shadow 0.2s;
}
.home-recommend-card:hover {
  border-image: linear-gradient(90deg, #6a93ff 0%, #a685ff 100%) 1;
  box-shadow: 0 8px 32px 0 rgba(106,147,255,0.18);
}
/* 让首页价格更突出，防止被覆盖 */
.price-tag {
  font-size: 20px !important;
  font-weight: bold !important;
  color: var(--color-tag-danger-text) !important;
  color: #ff3b30 !important;
  background: transparent !important;
  box-shadow: none;
  letter-spacing: 1px;
  vertical-align: bottom;
  margin-top: 6px;
  display: inline-block;
}
</style>