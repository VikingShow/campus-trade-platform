<template>
    <div>
        <h3>我的收藏</h3>
        <el-row :gutter="20" v-loading="loading">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in favoriteProducts" :key="product.id" style="margin-bottom: 20px;">
                <el-card shadow="hover" class="product-card">
                    <div @click="goToDetail(product.id)">
                        <img :src=product.coverImage class="product-image" alt="商品图片" @error="onImageError"/>
                        <div class="product-info">
                            <p class="product-title">{{ product.title }}</p>
                            <div class="bottom">
                                <span class="product-price">¥{{ product.price }}</span>
                                <span class="seller-info">{{ product.sellerNickname }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="unfavorite-btn">
                        <el-button type="danger" plain size="small" @click="handleRemoveFavorite(product.id)">取消收藏</el-button>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-empty v-if="!loading && favoriteProducts.length === 0" description="您还没有收藏任何商品"></el-empty>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { getMyFavorites } from '../api/favorite';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();
const favoriteProducts = ref([]);
const loading = ref(false);

// const backendUrl = 'http://localhost:8080';
// const fullImageUrl = (relativePath) => {
//     if (!relativePath) return '';
//     if (relativePath.startsWith('http')) return relativePath;
//     return `${backendUrl}${relativePath}`;
// };

const fetchFavorites = async () => {
    loading.value = true;
    try {
        const response = await getMyFavorites();
        favoriteProducts.value = response.data.data;
    } catch (error) {
        console.error("加载收藏列表失败:", error);
    } finally {
        loading.value = false;
    }
};

const goToDetail = (id) => {
    router.push(`/product/${id}`);
};

const handleRemoveFavorite = async (productId) => {
    await authStore.removeFromFavorites(productId);
    ElMessage.success("已取消收藏");
    // 重新加载列表以反映变化
    fetchFavorites();
};

const onImageError = (e) => {
    e.target.src = 'https://placehold.co/400x300/e8e8e8/969696?text=Image+Not+Found';
};

onMounted(fetchFavorites);
</script>

<style scoped>
.product-card { cursor: pointer; position: relative; }
.product-image { width: 100%; height: 200px; object-fit: cover; display: block; border-radius: 4px; }
.product-info { padding: 14px; }
.product-title { font-size: 16px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0 0 8px 0; }
.bottom { display: flex; justify-content: space-between; align-items: center; }
.product-price { font-size: 18px; color: #F56C6C; font-weight: bold; }
.seller-info { font-size: 13px; color: #909399; }
.unfavorite-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    opacity: 0;
    transition: opacity 0.3s;
}
.product-card:hover .unfavorite-btn {
    opacity: 1;
}
</style>
