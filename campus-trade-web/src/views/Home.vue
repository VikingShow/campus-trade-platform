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
          <el-card shadow="hover" class="product-card" @click="goToDetail(product.id)">
            <div class="product-image-area">
              <img :src="product.coverImage" class="product-image" alt="商品图片" @error="onImageError"/>
              <div v-if="product.imageUrls && product.imageUrls.length > 0" class="image-count-overlay">
                <el-icon><CameraFilled /></el-icon>
                <span>1 / {{ 1 + product.imageUrls.length }}</span>
              </div>
            </div>
            <div class="product-info-area">
              <div class="product-title">{{ product.title }}</div>
              <div class="product-meta">
                <span class="product-price">¥{{ product.price }}</span>
                <span class="product-seller">{{ product.sellerNickname }}</span>
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
    background: var(--color-bg-card);
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
  display: flex;
  flex-direction: column;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(60,60,60,0.10);
  background: var(--color-bg-card);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
}
.product-card:hover {
  box-shadow: 0 8px 32px 0 rgba(0,122,255,0.15);
  transform: translateY(-4px) scale(1.03);
}
.product-image-area {
  width: 100%;
  aspect-ratio: 1/1;
  background: #f5f6fa;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-radius: 0;
  overflow: hidden;
}
.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 0;
  display: block;
}
.image-count-overlay {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0,0,0,0.55);
  color: #fff;
  border-radius: 10px;
  padding: 2px 8px;
  font-size: 12px;
  display: flex;
  align-items: center;
}
.product-info-area {
  padding: 10px 8px 8px 8px;
  background: var(--color-bg-card);
  border-top: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.product-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.3;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.product-price {
  font-size: 1.15rem;
  font-weight: bold;
  color: #ff3b30;
}
.product-seller {
  font-size: 0.98rem;
  color: #888;
  background: #f0f4fa;
  border-radius: 8px;
  padding: 2px 10px;
}
@media (max-width: 600px) {
  .product-info-area { padding: 10px 8px 8px 8px; }
  .product-title { font-size: 1rem; }
  .product-price, .product-seller { font-size: 0.98rem; }
}
</style>