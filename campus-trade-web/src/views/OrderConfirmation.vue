<template>
  <el-card class="box-card" v-loading="loading">
    <template #header><h3>确认订单信息</h3></template>
    <div v-if="product">
      <el-descriptions border :column="1" size="large">
        <el-descriptions-item label="商品">
            <div style="display: flex; align-items: center;">
                <el-image :src=product.coverImage style="width: 80px; height: 80px; border-radius: 4px;"/>
                <span style="margin-left: 15px;">{{ product.title }}</span>
            </div>
        </el-descriptions-item>
        <el-descriptions-item label="卖家">{{ product.sellerNickname }}</el-descriptions-item>
        <el-descriptions-item label="价格">
          <span style="color: #F56C6C; font-weight: bold; font-size: 18px;">¥{{ product.price }}</span>
        </el-descriptions-item>
      </el-descriptions>
      
      <div class="delivery-section">
        <h3>选择交易地点</h3>
        <el-select v-model="selectedLocation" placeholder="请选择一个校园内交易地点" style="width: 100%;" :loading="locationsLoading">
            <el-option 
              v-for="location in locations"
              :key="location.id"
              :label="location.name" 
              :value="location.id"
            />
        </el-select>
        <h3 style="margin-top: 20px;">建议交易时间</h3>
        <el-input v-model="meetupTimeSlot" placeholder="例如：明天下午3点-5点"></el-input>
      </div>
      
       <div class="action-footer">
          <span>总计: </span>
          <span class="total-price">¥{{ product.price }}</span>
          <el-button type="primary" size="large" @click="submitOrder" :loading="submitting">提交订单</el-button>
       </div>
    </div>
     <el-empty v-else description="商品信息加载失败"></el-empty>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getProductById } from '../api/product';
import { createOrder } from '../api/order';
import { ElMessage } from 'element-plus';
import { getPublicLocations } from '../api/location'; // 【新增】导入API函数


const props = defineProps({ productId: String });
const router = useRouter();
const product = ref(null);
const loading = ref(true);
// 【修改】交易地点相关状态
const locations = ref([]);
const locationsLoading = ref(false);
const selectedLocation = ref(null);
const meetupTimeSlot = ref('');
const submitting = ref(false);

// const backendUrl = 'http://localhost:8080';

// const fullImageUrl = (relativePath) => {
//     if (!relativePath) return '';
//     if (relativePath.startsWith('http')) return relativePath;
//     return `${backendUrl}${relativePath}`;
// };

const fetchProduct = async () => {
    loading.value = true;
    try {
        const res = await getProductById(props.productId);
        product.value = res.data.data;
    } catch(err) {
        ElMessage.error("商品信息加载失败");
    } finally {
        loading.value = false;
    }
};

// 【新增】获取交易地点的函数
const fetchLocations = async () => {
    locationsLoading.value = true;
    try {
        const response = await getPublicLocations();
        locations.value = response.data.data;
    } catch (error) {
        ElMessage.error("获取交易地点失败");
    } finally {
        locationsLoading.value = false;
    }
};

const submitOrder = async () => {
    if(!selectedLocation.value) {
        ElMessage.warning('请选择一个交易地点');
        return;
    }
    submitting.value = true;
    try {
        const orderData = {
            productId: product.value.id,
            meetupLocationId: selectedLocation.value,
            meetupTimeSlot: meetupTimeSlot.value,
        };
        await createOrder(orderData);
        ElMessage.success('订单创建成功！');
        router.push(`/dashboard/purchases`);
    } catch (error) {
    } finally {
        submitting.value = false;
    }
};

onMounted(() => {
  fetchProduct();
  fetchLocations(); // 【新增】页面加载时同时获取地点列表
});
</script>

<style scoped>
.box-card { max-width: 800px; margin: auto; }
.delivery-section { margin-top: 30px; }
.action-footer { margin-top: 30px; text-align: right; border-top: 1px solid #ebeef5; padding-top: 20px; display: flex; justify-content: flex-end; align-items: center;}
.total-price { font-size: 22px; color: #F56C6C; font-weight: bold; margin-right: 20px; }
</style>