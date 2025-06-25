<template>
  <div v-if="!loading && user" class="user-profile">
    <el-card>
      <div class="profile-header">
        <el-avatar :size="80" :src="fullImageUrl(user.avatar)" icon="UserFilled" />
        <div class="profile-info">
          <h2>{{ user.nickname }}</h2>
          <el-tag effect="dark" round size="large">
            <el-icon><Finished/></el-icon>
            信誉分: {{ user.creditScore }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <h3>收到的评价 ({{ ratings.length }})</h3>
      </template>
      <div v-if="ratings.length > 0">
        <div v-for="rating in ratings" :key="rating.id" class="rating-item">
          <div class="rating-header">
            <el-avatar :size="40" :src="fullImageUrl(rating.raterAvatar)" icon="UserFilled" />
            <span class="rater-name">{{ rating.raterNickname }}</span>
            <el-rate v-model="rating.score" disabled style="margin-left: auto;" />
          </div>
          <p class="rating-comment">{{ rating.comment || '该用户没有填写评价内容。' }}</p>
          
          <!-- 【新增】展示关联的商品信息，并提供跳转链接 -->
          <div class="product-info">
            <span>评价于商品：</span>
            <router-link :to="`/product/${rating.productId}`" class="product-link">
              {{ rating.productTitle }}
            </router-link>
          </div>

          <div class="rating-time">{{ new Date(rating.createTime).toLocaleString() }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无评价记录"></el-empty>
    </el-card>
  </div>
  <el-skeleton v-else :rows="5" animated />
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getUserInfo, getUserRatings } from '../api/user';
import { UserFilled, Finished } from '@element-plus/icons-vue';

const props = defineProps({ id: String });
const user = ref(null);
const ratings = ref([]);
const loading = ref(true);

const backendUrl = 'http://localhost:8080';

const fullImageUrl = (relativePath) => {
    if (!relativePath) return '';
    if (relativePath.startsWith('http')) return relativePath;
    return `${backendUrl}${relativePath}`;
};

onMounted(async () => {
  loading.value = true;
  try {
    const [userRes, ratingsRes] = await Promise.all([
      getUserInfo(props.id),
      getUserRatings(props.id)
    ]);
    user.value = userRes.data.data;
    ratings.value = ratingsRes.data.data;
  } catch (error) {
    console.error("加载用户主页失败:", error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.profile-header { display: flex; align-items: center; gap: 20px; }
.profile-info h2 { margin: 0 0 10px 0; }
.rating-item { padding: 15px 0; border-bottom: 1px solid #e4e7ed; }
.rating-item:last-child { border-bottom: none; }
.rating-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.rater-name { font-weight: bold; }
.rating-comment { color: #303133; margin: 10px 0; }
.product-info {
  font-size: 13px;
  color: #606266;
  background-color: #f4f4f5;
  padding: 5px 10px;
  border-radius: 4px;
  display: inline-block;
}
.product-link {
  color: var(--el-color-primary);
  text-decoration: none;
}
.product-link:hover {
  text-decoration: underline;
}
.rating-time { font-size: 12px; color: #909399; text-align: right; margin-top: 10px; }
</style>