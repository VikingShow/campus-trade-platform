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
               <!-- 【最终修正】使用 v-if 进行条件渲染 -->
              
              <!-- 情况一：如果有多张图片，则渲染轮播图 -->
              <el-carousel v-if="allImages.length > 1" trigger="click" height="400px" indicator-position="outside">
                <el-carousel-item v-for="(imgUrl, index) in allImages" :key="index" class="carousel-item-wrapper">
                  <!-- 
                    关键修正:
                    1. fit="cover": 强制图片填满容器，保持统一大小，多余部分会被裁剪。
                    2. preview-src-list: 提供了点击查看大图的功能，它会自动生成一个图片画廊。
                    3. initial-index: 确保点击哪张小图，就从哪张开始预览。
                    4. preview-teleported: 确保预览层在最上层，不会被其他元素遮挡。
                  -->
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
              
              <!-- 情况二：如果只有一张图片，则只渲染一张静态图片，但同样提供点击查看大图的功能 -->
              <div v-else-if="allImages.length === 1" class="carousel-item-wrapper">
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
                    <el-avatar :src="product.sellerAvatar" icon="UserFilled" />
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
                    <img :src="rec.coverImage" class="product-image" alt="推荐商品图片" @error="onImageError"/>
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

// 【新增】计算属性，用于合并封面图和附图列表
const allImages = computed(() => {
    if (!product.value || !product.value.coverImage) return [];
    const otherImages = product.value.imageUrls || [];
    // 使用 Set 去重，防止封面图和附图列表重复
    return [...new Set([product.value.coverImage, ...otherImages])].filter(url => url);
});

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
/* 为轮播图的每一项增加一个容器，用于设置背景和居中 */
.carousel-item-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa; /* 浅灰色背景 */
  border-radius: 8px;
  overflow: hidden; /* 确保图片不会超出圆角 */
}

/* 【修改】el-image 的 fit="cover" 属性会自动处理 object-fit，
  我们只需确保它填满容器即可。
*/
.carousel-image {
  width: 100%;
  height: 100%;
}
</style>
