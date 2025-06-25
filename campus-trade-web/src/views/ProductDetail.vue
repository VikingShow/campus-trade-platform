<template>
  <el-skeleton :loading="loading" animated>
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
    
    <template #default>
      <div v-if="product" class="product-detail-container">
        <el-row :gutter="30">
          <el-col :md="12">
            <el-image :src="fullImageUrl(product.coverImage)" fit="cover" class="product-main-image" @error="onImageError">
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
                  <el-avatar :src="fullImageUrl(product.sellerAvatar)" icon="UserFilled" />
                  <!-- 【关键修正】将卖家昵称变为可点击的链接 -->
                  <router-link :to="`/user/${product.sellerId}`" class="seller-link">
                    <span class="seller-name">{{ product.sellerNickname }}</span>
                  </router-link>
                  <el-tag effect="plain" round class="credit-score-tag" v-if="product.creditScore !== null">
                    <!-- 【最终修正】将 Shield 图标替换为存在的 Finished 图标 -->
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
              <div v-else>
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
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getProductById, updateProductStatus } from '../api/product';
import { useAuthStore } from '../stores/authStore';
import { ElMessage, ElMessageBox } from 'element-plus';
// 【最终修正】将 Shield 图标替换为存在的 Finished 图标
import { UserFilled, Finished, Goods, Edit, Delete } from '@element-plus/icons-vue';

const props = defineProps({ id: String });
const router = useRouter();
const authStore = useAuthStore();
const product = ref(null);
const loading = ref(true);

const backendUrl = 'http://localhost:8080';

const fullImageUrl = (relativePath) => {
    if (!relativePath) return '';
    if (relativePath.startsWith('http')) return relativePath;
    return `${backendUrl}${relativePath}`;
};

const isOwner = computed(() => {
  if (!authStore.isAuthenticated || !product.value) return false;
  return String(authStore.user.id) === String(product.value.sellerId);
});

const onImageError = (e) => {
    e.target.src = 'https://placehold.co/600x400/e8e8e8/969696?text=Image+Not+Found';
};

const fetchProduct = async () => {
  loading.value = true;
  try {
    const res = await getProductById(props.id);
    product.value = res.data.data;
  } catch (err) {
    product.value = null;
    console.error("获取商品详情失败:", err);
  } finally {
    loading.value = false;
  }
};

const editProduct = () => router.push(`/edit-product/${props.id}`);
const buyNow = () => router.push(`/confirm-order/${props.id}`);

const delistProduct = () => {
  ElMessageBox.confirm('确定要下架此商品吗？下架后其他用户将无法看到。', '确认操作', {
    confirmButtonText: '确定下架', cancelButtonText: '取消', type: 'warning',
  }).then(async () => {
    try {
      await updateProductStatus(props.id, 'DELISTED');
      ElMessage.success('商品已下架');
      router.push('/');
    } catch (error) {}
  });
};

const contactSeller = () => {
    if (!authStore.isAuthenticated) {
        ElMessage.warning('请先登录再联系卖家');
        router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } });
        return;
    }
    // 跳转到与卖家的聊天窗口
    router.push(`/messages/${product.value.sellerId}`);
};

// 【新增】判断当前商品是否已被收藏
const isFavorited = computed(() => {
  return authStore.favoriteIds.has(String(props.id));
});

// 【新增】切换收藏状态的方法
const toggleFavorite = () => {
    if (!authStore.isAuthenticated) {
        ElMessage.warning('请先登录再收藏商品');
        return;
    }
    if (isFavorited.value) {
        authStore.removeFromFavorites(props.id);
    } else {
        authStore.addToFavorites(props.id);
    }
};

onMounted(fetchProduct);
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
</style>
