<template>
    <div>
        <h3>我的收藏</h3>
        <el-row :gutter="20" v-loading="loading">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in favoriteProducts" :key="product.id" style="margin-bottom: 20px;">
                <el-card shadow="hover" class="product-card">
                    <div class="product-image-area" @click="goToDetail(product.id)">
                        <img :src="product.coverImage" class="product-image" alt="商品图片" @error="onImageError"/>
                    </div>
                    <div class="product-info-area">
                        <div class="product-title">{{ product.title }}</div>
                        <div class="product-meta">
                            <span class="product-price">¥{{ product.price }}</span>
                            <span class="product-seller">{{ product.sellerNickname }}</span>
                        </div>
                    </div>
                    <div class="unfavorite-btn">
                        <el-button plain size="small" @click.stop="handleRemoveFavorite(product.id)" :style="'background:#ff3b30!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'">取消收藏</el-button>
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

<style>
.product-card {
    display: flex;
    flex-direction: column;
    border-radius: 18px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(60,60,60,0.10);
    background: var(--color-bg-card);
    transition: box-shadow 0.2s, transform 0.2s;
    cursor: pointer;
    position: relative;
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
.product-info-area {
    padding: 4px 2px 4px 2px;
    background: var(--color-bg-card);
    border-top: 1px solid var(--color-border);
    display: flex;
    flex-direction: column;
    gap: 4px;
    align-items: flex-start;
}
:deep(.el-card__body) {
    padding: 0;
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
    text-align: left;
}
.product-meta {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    gap: 6px;
}
.product-price {
    font-size: 1.15rem;
    font-weight: bold;
    color: #ff3b30;
    text-align: left;
}
.product-seller {
    font-size: 0.98rem;
    color: #888;
    background: #f0f4fa;
    border-radius: 8px;
    padding: 2px 8px;
    max-width: 90px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    text-align: left;
}
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
@media (max-width: 600px) {
    .product-info-area { padding: 10px 8px 8px 8px; }
    .product-title { font-size: 1rem; }
    .product-price, .product-seller { font-size: 0.98rem; }
}
</style>
