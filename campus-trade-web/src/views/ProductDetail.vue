<template>
  <div>
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
              <el-image :src=product.coverImage fit="cover" class="product-main-image" @error="onImageError">
                <template #placeholder>
                  <div class="image-slot">加载中<span class="dot">...</span></div>
                </template>
              </el-image>
            </el-col>
            <el-col :md="12">
              <h1>{{ product.title }}</h1>
              <p class="description">{{ product.description }}</p>
              <div class="price-line">
                <span class="price">¥ {{ product.price }}</span>
              </div>
              <el-descriptions :column="1" border style="margin-top:20px;">
                <el-descriptions-item label="新旧程度">
                  <el-rate v-model="product.conditionLevel" disabled />
                </el-descriptions-item>
                <el-descriptions-item label="发布时间">{{ new Date(product.createTime).toLocaleDateString() }}</el-descriptions-item>
                <el-descriptions-item label="卖家">
                  <div class="seller-box">
                    <el-avatar :src=product.sellerAvatar icon="UserFilled" />
                    <router-link :to="`/user/${product.sellerId}`" class="seller-link">
                        <span class="seller-name">{{ product.sellerNickname }}</span>
                    </router-link>
                    <el-tag effect="plain" round class="credit-score-tag" v-if="product.creditScore !== null">
                      <el-icon><Finished/></el-icon>
                      信誉分: {{ product.creditScore }}
                    </el-tag>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
              
              <div class="action-buttons">
                <div v-if="isOwner">
                  <el-button type="primary" :icon="Edit" @click="editProduct">编辑商品</el-button>
                  <el-button type="danger" :icon="Delete" @click="delistProduct">下架商品</el-button>
                </div>
                <div v-else class="buyer-actions">
                  <el-button type="primary" plain :icon="ChatDotSquare" @click="contactSeller">联系卖家</el-button>
                  <el-button 
                    :type="isFavorited ? 'warning' : 'info'" 
                    :icon="Star" 
                    @click.stop="toggleFavorite"
                    plain
                  >
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </el-button>
                  <el-button type="success" size="large" :icon="Goods" @click="buyNow">立即购买</el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-empty v-else description="商品不存在或已下架"></el-empty>
      </template>
    </el-skeleton>

    <!-- “猜你喜欢”模块 -->
    <div v-if="recommendations.length > 0" class="recommendation-section">
        <el-divider><h3>猜你喜欢</h3></el-divider>
        <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="rec in recommendations" :key="rec.id">
                <el-card shadow="hover" class="product-card" @click="goToDetail(rec.id)">
                    <img :src=rec.coverImage class="product-image" alt="推荐商品图片" @error="onImageError"/>
                    <div class="product-info">
                        <p class="product-title">{{ rec.title }}</p>
                        <div class="bottom">
                            <span class="product-price">¥{{ rec.price }}</span>
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

// 定义后端服务的地址，用于拼接完整的图片URL
// const backendUrl = 'http://localhost:8080';

// // 计算完整的图片URL
// const fullImageUrl = (relativePath) => {
//     if (!relativePath) return '';
//     if (relativePath.startsWith('http')) return relativePath;
//     return `${backendUrl}${relativePath}`;
// };

// 计算当前登录用户是否是该商品的卖家
const isOwner = computed(() => {
  if (!authStore.isAuthenticated || !product.value) return false;
  return String(authStore.user.id) === String(product.value.sellerId);
});

// 计算当前商品是否已被收藏
const isFavorited = computed(() => {
  return authStore.favoriteIds.has(String(props.id));
});

// 图片加载失败时的占位图
const onImageError = (e) => {
    e.target.src = 'https://placehold.co/600x400/e8e8e8/969696?text=Image+Not+Found';
};

// 获取商品详情和推荐数据
const fetchAllData = async () => {
  loading.value = true;
  product.value = null; // 重置商品信息
  recommendations.value = []; // 切换商品时清空旧推荐
  try {
    const res = await getProductById(props.id);
    product.value = res.data.data;
    // 获取商品详情成功后，再去获取推荐
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
        // 过滤掉当前商品自身
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

<style scoped>
.product-main-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  background-color: #f5f7fa;
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
.description {
  color: #606266;
  margin: 15px 0;
  line-height: 1.6;
}
.price {
  font-size: 28px;
  color: #F56C6C;
  font-weight: bold;
}
.seller-box {
  display: flex;
  align-items: center;
  gap: 10px;
}
.seller-name {
  font-weight: 500;
}
.seller-link {
  text-decoration: none;
  color: inherit;
}
.seller-link:hover .seller-name {
  color: var(--el-color-primary);
}
.credit-score-tag {
    display: inline-flex;
    align-items: center;
}
.credit-score-tag .el-icon {
    margin-right: 4px;
}
h1 {
  margin-top: 0;
}
.action-buttons {
  margin-top: 30px;
}
.buyer-actions {
  display: flex;
  gap: 15px;
}
.recommendation-section {
    margin-top: 40px;
}
.product-card { cursor: pointer; margin-bottom: 20px;}
.product-image { width: 100%; height: 200px; object-fit: cover; display: block; border-radius: 4px; }
.product-info { padding: 14px; }
.product-title { font-size: 16px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 0 0 8px 0; }
.bottom { display: flex; justify-content: space-between; align-items: center; }
.product-price { font-size: 18px; color: #F56C6C; font-weight: bold; }
</style>
