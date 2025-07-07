<template>
  <el-card class="box-card" v-loading="loading">
    <template #header><h3>确认订单信息</h3></template>
    <div v-if="product">
      <!-- 商品信息展示 -->
      <el-descriptions border :column="1" size="large">
        <el-descriptions-item label="商品">
            <div style="display: flex; align-items: center;">
                <el-image :src="product.coverImage" style="width: 80px; height: 80px; border-radius: 4px;"/>
                <span style="margin-left: 15px;">{{ product.title }}</span>
            </div>
        </el-descriptions-item>
        <el-descriptions-item label="卖家">{{ product.sellerNickname }}</el-descriptions-item>
        <el-descriptions-item label="价格">
          <span style="color: #F56C6C; font-weight: bold; font-size: 18px;">¥{{ product.price }}</span>
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 配送方式选择 -->
      <div class="delivery-section">
        <h3>选择配送方式</h3>
        <el-radio-group v-model="form.deliveryMethod" @change="handleDeliveryMethodChange">
          <el-radio-button 
            v-for="option in deliveryOptions" 
            :key="option.value" 
            :label="option.value"
          >
            {{ option.label }}
          </el-radio-button>
        </el-radio-group>

        <!-- 根据选择的配送方式，显示不同的内容 -->
        <div v-if="form.deliveryMethod === 'MEETUP'" class="delivery-option-content">
            <h4>选择交易地点</h4>
            <el-select v-model="form.meetupLocationId" placeholder="请选择一个校园内交易地点" style="width: 100%;" :loading="locationsLoading">
                <el-option v-for="loc in locations" :key="loc.id" :label="loc.name" :value="loc.id" />
            </el-select>
            <h4 style="margin-top: 20px;">建议交易时间</h4>
            <el-input v-model="form.meetupTimeSlot" placeholder="例如：明天下午3点-5点"></el-input>
        </div>

        <div v-if="form.deliveryMethod === 'SHIPPING'" class="delivery-option-content">
            <h4>选择收货地址</h4>
            <div v-if="addresses.length > 0" class="address-list">
                <div 
                    v-for="addr in addresses" 
                    :key="addr.id"
                    class="address-item"
                    :class="{'selected': form.shippingAddressId === addr.id}"
                    @click="form.shippingAddressId = addr.id"
                >
                    <div class="addr-title">{{ addr.recipientName }} ({{ addr.phone }}) <span v-if="addr.isDefault" class="status-tag status-info" style="margin-left:6px;">默认</span></div>
                    <div class="addr-detail">{{ `${addr.province} ${addr.city} ${addr.district} ${addr.detailedAddress}` }}</div>
                </div>
            </div>
            <el-empty v-else description="您还没有收货地址"></el-empty>
            <el-button @click="$router.push('/dashboard/addresses')" style="margin-top: 10px;background:#409eff!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;">管理收货地址</el-button>
        </div>
      </div>
      
       <div class="action-footer">
          <span>总计: </span>
          <span class="total-price">¥{{ product.price }}</span>
          <el-button size="large" @click="submitOrder" :loading="submitting" :style="'background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'">提交订单</el-button>
       </div>
    </div>
     <el-empty v-else description="商品信息加载失败"></el-empty>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { getProductById } from '../api/product';
import { createOrder } from '../api/order';
import { getAddresses } from '../api/address';
import { getPublicLocations } from '../api/location';
import { ElMessage } from 'element-plus';

const props = defineProps({ productId: String });
const router = useRouter();
const product = ref(null);
const loading = ref(true);
const submitting = ref(false);
const addresses = ref([]);
const locations = ref([]);
const locationsLoading = ref(false);

// 将所有与订单提交相关的数据都放入一个 reactive 对象中
const form = reactive({
    productId: props.productId,
    deliveryMethod: '',
    meetupLocationId: null,
    meetupTimeSlot: '',
    shippingAddressId: null
});

// 计算属性，用于根据商品支持的方式，动态生成配送选项
const deliveryOptions = computed(() => {
    if (!product.value || !product.value.deliveryOptions) return [];
    const options = [];
    const supported = product.value.deliveryOptions.split(',');
    if (supported.includes('MEETUP')) {
        options.push({ label: '线下面交', value: 'MEETUP' });
    }
    if (supported.includes('SHIPPING')) {
        options.push({ label: '快递配送', value: 'SHIPPING' });
    }
    return options;
});

// 获取商品详情
const fetchProduct = async () => {
    try {
        const res = await getProductById(props.productId);
        product.value = res.data.data;
    } catch(err) {
        ElMessage.error("商品信息加载失败");
    }
};

// 获取用户的收货地址列表
const fetchAddresses = async () => {
    try {
        const res = await getAddresses();
        addresses.value = res.data.data;
        // 自动选中默认地址
        const defaultAddr = addresses.value.find(a => a.isDefault);
        if (defaultAddr) {
            form.shippingAddressId = defaultAddr.id;
        } else if (addresses.value.length > 0) {
            form.shippingAddressId = addresses.value[0].id;
        }
    } catch (error) {
        console.error("加载收货地址失败:", error);
    }
};

// 获取公共的交易地点列表
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

// 当用户切换配送方式时，清空不相关的选项，避免提交错误数据
const handleDeliveryMethodChange = () => {
    form.meetupLocationId = null;
    form.meetupTimeSlot = '';
    form.shippingAddressId = null;
    // 切换后，重新为快递配送模式设置默认地址
    if (form.deliveryMethod === 'SHIPPING' && addresses.value.length > 0) {
        const defaultAddr = addresses.value.find(a => a.isDefault);
        form.shippingAddressId = defaultAddr ? defaultAddr.id : addresses.value[0].id;
    }
}

// 提交订单
const submitOrder = async () => {
    // 校验
    if (!form.deliveryMethod) {
        ElMessage.warning('请选择一种配送方式');
        return;
    }
    if (form.deliveryMethod === 'MEETUP' && !form.meetupLocationId) {
        ElMessage.warning('请选择一个交易地点');
        return;
    }
     if (form.deliveryMethod === 'SHIPPING' && !form.shippingAddressId) {
        ElMessage.warning('请选择一个收货地址');
        return;
    }

    submitting.value = true;
    try {
        await createOrder(form);
        ElMessage.success('订单创建成功！');
        router.push(`/dashboard/purchases`);
    } catch (error) {
        // 错误已由 axios 拦截器统一处理
    } finally {
        submitting.value = false;
    }
};

// 组件加载时，并行获取所有需要的数据
onMounted(async () => {
    loading.value = true;
    await Promise.all([fetchProduct(), fetchAddresses(), fetchLocations()]);
    // 默认选中第一个可用的配送方式
    if (deliveryOptions.value.length > 0) {
        form.deliveryMethod = deliveryOptions.value[0].value;
    }
    loading.value = false;
});
</script>

<style scoped>
.box-card { max-width: 800px; margin: auto; }
.delivery-section { margin-top: 30px; }
.delivery-option-content {
    margin-top: 20px;
    padding: 20px;
    border: 1px solid #f0f0f0;
    border-radius: 4px;
}
.address-list { margin-top: 10px; }
.address-item {
    border: 1px solid #dcdfe6;
    padding: 15px;
    border-radius: 4px;
    margin-bottom: 10px;
    cursor: pointer;
    transition: all 0.2s;
}
.address-item:hover {
    border-color: var(--el-color-primary);
}
.address-item.selected {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px var(--el-color-primary-light-7);
}
.addr-title { font-weight: bold; margin-bottom: 5px; }
.addr-detail { font-size: 14px; color: #606266; }
.action-footer { margin-top: 30px; text-align: right; border-top: 1px solid #ebeef5; padding-top: 20px; display: flex; justify-content: flex-end; align-items: center;}
.total-price { font-size: 22px; color: #F56C6C; font-weight: bold; margin-right: 20px; }
</style>
