<template>
  <div>
    <h2 class="page-title">商品详情</h2>
    <el-skeleton :loading="loading" animated>
      <!-- 加载时的骨架屏效果 -->
      <template #template>
        <el-row :gutter="30">
          <el-col :md="12"><el-skeleton-item variant="image" style="width: 100%; height: 400px;" /></el-col>
          <el-col :md="12">
              <el-skeleton-item variant="p" style="width: 50%; margin-bottom: 20px;" />
              <el-skeleton-item variant="p" style="width: 30%; margin-bottom: 30px;" />
              <el-skeleton :rows="5" animated />
          </el-col>
        </el-row>
      </template>
      
      <!-- 加载完成后的真实内容 -->
     <template #default>
        <div v-if="product" class="product-detail-container">
          <el-row :gutter="30">
            <el-col :md="12">
               <!-- 情况一：如果有多张图片，则渲染轮播图 -->
              <el-carousel v-if="allImages.length > 1" trigger="click" height="400px" indicator-position="outside">
                <el-carousel-item v-for="(imgUrl, index) in allImages" :key="index" class="carousel-item-wrapper">
                  <el-image 
                    :src="imgUrl" 
                    :preview-src-list="allImages"
                    :initial-index="index"
                    preview-teleported
                    fit="cover" 
                    class="carousel-image" 
                    @error="onImageError"
                  />
                </el-carousel-item>
              </el-carousel>
              
              <!-- 情况二：如果只有一张图片，则只渲染一张静态图片 -->
              <!-- 【最终修正】为这个容器也明确设置 height: 400px，与轮播图保持一致 -->
              <div v-else-if="allImages.length === 1" class="carousel-item-wrapper" style="height: 400px;">
                <el-image 
                  :src="allImages[0]" 
                  :preview-src-list="allImages"
                  :initial-index="0"
                  preview-teleported
                  fit="cover" 
                  class="carousel-image" 
                  @error="onImageError"
                />
              </div>

            </el-col>
            <el-col :md="12">
              <h1 style="font-size:1.5em;font-weight:600;margin-bottom:10px;">{{ product.title }}</h1>
              <p class="description">{{ product.description }}</p>
              <div class="price-line">
                <span class="status-tag status-danger" style="font-size:1.2em;">¥ {{ product.price }}</span>
              </div>
              <el-descriptions :column="1" border style="margin-top:20px;">
                <el-descriptions-item label="新旧程度">
                  <el-rate v-model="product.conditionLevel" disabled />
                </el-descriptions-item>
                <el-descriptions-item label="发布时间">{{ new Date(product.createTime).toLocaleDateString() }}</el-descriptions-item>
                <el-descriptions-item label="配送方式">
                  <el-tag v-for="opt in deliveryOptionsList" :key="opt" type="info" style="margin-right: 8px;">
                    {{ opt === 'MEETUP' ? '线下面交' : opt === 'SHIPPING' ? '支持快递' : opt }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="卖家">
                  <div class="seller-box">
                    <el-avatar :src="product.sellerAvatar" icon="UserFilled" />
                    <router-link :to="`/user/${product.sellerId}`" class="seller-link">
                        <span class="status-tag status-info seller-name">{{ product.sellerNickname }}</span>
                    </router-link>
                    <transition name="fade-bounce">
                      <span v-if="product.creditScore !== null" class="credit-badge">
                        <el-icon><Finished/></el-icon>
                        信誉分: {{ product.creditScore }}
                      </span>
                    </transition>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
              
              <div class="action-buttons">
                <div v-if="isOwner" class="action-btn-group">
                  <el-button class="btn-primary" :icon="Edit" @click="editProduct" style="min-width:120px;background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;">编辑商品</el-button>
                  <el-button class="btn-danger" :icon="Delete" @click="delistProduct" style="min-width:120px;background:#ff3b30!important;color:#fff!important;">下架商品</el-button>
                </div>
                <div v-else class="buyer-actions action-btn-group">
                  <el-button class="btn-primary" :icon="ChatDotSquare" @click="contactSeller" style="min-width:120px;background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;">联系卖家</el-button>
                  <el-button
                    :icon="Star"
                    @click.stop="toggleFavorite"
                    :style="isFavorited
                      ? 'background:#ff9500 !important;color:#222 !important;border:none !important;min-width:120px;font-weight:bold;border-radius:16px;'
                      : 'background:#409eff !important;color:#fff !important;border:none !important;min-width:120px;font-weight:bold;border-radius:16px;'"
                  >
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </el-button>
                  <el-button class="btn-success" size="large" :icon="Goods" @click="buyNow" style="min-width:120px;background:#34c759!important;color:#fff!important;">立即购买</el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-empty v-else description="商品不存在或已下架"></el-empty>
      </template>
    </el-skeleton>

    <!-- "猜你喜欢"模块 -->
    <div v-if="recommendations.length > 0" class="recommendation-section">
        <el-divider><h3>猜你喜欢</h3></el-divider>
        <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="rec in recommendations" :key="rec.id">
                <el-card shadow="hover" class="product-card" @click="goToDetail(rec.id)">
                    <img :src="rec.coverImage" class="product-image" alt="推荐商品图片" @error="onImageError"/>
                    <div class="product-info">
                        <p class="product-title">{{ rec.title }}</p>
                        <div class="bottom">
                            <span class="status-tag status-danger">¥{{ rec.price }}</span>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { getProductById, updateProductStatus, getRecommendations } from '../api/product';
import { useAuthStore } from '../stores/authStore';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UserFilled, Finished, Goods, Edit, Delete, ChatDotSquare, Star } from '@element-plus/icons-vue';

// 从路由参数中获取商品ID
const props = defineProps({ id: String });

const router = useRouter();
const authStore = useAuthStore();
const product = ref(null);
const loading = ref(true);
const recommendations = ref([]);

// 【新增】计算属性，用于合并封面图和附图列表
const allImages = computed(() => {
    if (!product.value || !product.value.coverImage) return [];
    const otherImages = product.value.imageUrls || [];
    // 使用 Set 去重，防止封面图和附图列表重复
    return [...new Set([product.value.coverImage, ...otherImages])].filter(url => url);
});

// 计算当前登录用户是否是该商品的卖家
const isOwner = computed(() => {
  if (!authStore.isAuthenticated || !product.value) return false;
  return String(authStore.user.id) === String(product.value.sellerId);
});

// 计算当前商品是否已被收藏
const isFavorited = computed(() => {
  return authStore.favoriteIds.has(String(props.id));
});

// 【新增】配送方式数组
const deliveryOptionsList = computed(() => {
  if (!product.value || !product.value.deliveryOptions) return [];
  return product.value.deliveryOptions.split(',').filter(Boolean);
});

// 图片加载失败时的占位图
const onImageError = (e) => {
    e.target.src = 'https://placehold.co/600x400/e8e8e8/969696?text=Image+Not+Found';
};

// 获取商品详情和推荐数据
const fetchAllData = async () => {
  loading.value = true;
  product.value = null;
  recommendations.value = [];
  try {
    const res = await getProductById(props.id);
    product.value = res.data.data;
    if(product.value) {
        fetchRecommendations();
    }
  } catch (err) {
    console.error("获取商品详情失败:", err);
  } finally {
    loading.value = false;
  }
};

// 获取推荐商品的函数
const fetchRecommendations = async () => {
    try {
        const res = await getRecommendations(props.id);
        recommendations.value = res.data.data.filter(p => p.id !== props.id);
    } catch (error) {
        console.error("获取推荐商品失败:", error);
    }
};

// 定义页面操作
const editProduct = () => router.push(`/edit-product/${props.id}`);
const buyNow = () => router.push(`/confirm-order/${props.id}`);
const contactSeller = () => router.push(`/messages/${product.value.sellerId}`);
const goToDetail = (id) => router.push(`/product/${id}`);

const delistProduct = () => {
  ElMessageBox.confirm(`确定要下架商品 "${product.value.title}" 吗?`, '确认操作', {
    confirmButtonText: '确定下架', cancelButtonText: '取消', type: 'warning',
  }).then(async () => {
    try {
      await updateProductStatus(props.id, 'DELISTED');
      ElMessage.success('商品已下架');
      router.push('/'); // 下架后返回首页
    } catch (error) {}
  });
};

const toggleFavorite = () => {
    if (!authStore.isAuthenticated) {
        ElMessage.warning('请先登录再收藏商品');
        router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } });
        return;
    }
    if (isFavorited.value) {
        authStore.removeFromFavorites(props.id);
    } else {
        authStore.addToFavorites(props.id);
    }
};

// 监听路由参数变化，当从一个详情页跳转到另一个详情页时，重新加载数据
watch(() => props.id, fetchAllData, { immediate: true });

</script>

<style>
.product-detail-container {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(60,60,60,0.10);
  padding: 32px 24px;
  margin-bottom: 32px;
}
.carousel-image {
  border-radius: 16px;
  width: 100%;
  height: 400px;
  object-fit: cover;
}
.carousel-item-wrapper {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6fa;
  border-radius: 16px;
}
.price-line {
  margin: 18px 0 10px 0;
}
.seller-box {
  display: flex;
  align-items: center;
  gap: 10px;
}
.seller-link { text-decoration: none; }
.seller-name { font-weight: 500; }
.action-buttons, .buyer-actions, .action-btn-group {
  display: flex;
  gap: 14px;
  margin-top: 24px;
  flex-wrap: wrap;
}
@media (max-width: 900px) {
  .product-detail-container {
    padding: 10px 2px;
  }
  .carousel-image, .carousel-item-wrapper {
    height: 220px;
  }
  .action-buttons, .buyer-actions, .action-btn-group {
    gap: 8px;
    margin-top: 12px;
  }
}
.recommendation-section {
  margin-top: 40px;
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
.product-image { width: 100%; height: 200px; object-fit: cover; display: block; border-radius: 12px; }
.product-info { padding: 14px; }
.product-title { font-size: 16px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0 0 8px 0; }
.bottom { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
/* 强化按钮色彩优先级，彻底覆盖 el-button 默认色 */
.el-button.btn-primary,
.el-button.btn-danger,
.el-button.btn-success,
.el-button.btn-info,
.el-button.btn-warning {
  background: inherit !important;
  color: inherit !important;
  border: none !important;
  border-radius: 16px !important;
  font-weight: 500 !important;
  box-shadow: var(--color-shadow) !important;
}
/* 信誉分徽章动效与美化 */
.credit-badge {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(90deg, #e6f9ed 0%, #d0f5e2 100%);
  color: #30d158;
  font-weight: bold;
  border-radius: 16px;
  padding: 4px 16px;
  font-size: 15px;
  box-shadow: 0 2px 8px 0 rgba(48,209,88,0.08);
  margin-left: 8px;
  transition: all 0.3s cubic-bezier(.68,-0.55,.27,1.55);
}
.fade-bounce-enter-active {
  animation: badge-bounce-in 0.6s;
}
@keyframes badge-bounce-in {
  0% { opacity: 0; transform: scale(0.7) translateY(-20px); }
  60% { opacity: 1; transform: scale(1.1) translateY(6px); }
  100% { opacity: 1; transform: scale(1) translateY(0); }
}
/* 收藏按钮样式彻底穿透el-button */
::v-deep(.el-button.btn-info) {
  background: #409eff !important;
  color: #fff !important;
  border: none !important;
  border-radius: 16px !important;
  font-weight: bold !important;
  min-width: 120px !important;
}
::v-deep(.el-button.btn-warning) {
  background: #ff9500 !important;
  color: #222 !important;
  border: none !important;
  border-radius: 16px !important;
  font-weight: bold !important;
  min-width: 120px !important;
}
</style>
