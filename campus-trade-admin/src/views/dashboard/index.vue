<template>
  <div class="dashboard-container">
    <!-- 时间范围选择器 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <div class="time-selector">
        <span class="time-label">统计时间范围：</span>
        <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
          <el-radio-button label="7">最近7天</el-radio-button>
          <el-radio-button label="15">最近15天</el-radio-button>
          <el-radio-button label="30">最近30天</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <!-- 数据总览卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/user-management')">
          <div class="stat-card">
            <el-icon class="icon-user"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-title">总用户数</div>
              <div class="stat-value">{{ summary.userCount }}</div>
              <div class="stat-trend" :class="userGrowth >= 0 ? 'positive' : 'negative'">
                {{ userGrowth >= 0 ? '+' : '' }}{{ userGrowth }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/product-management')">
          <div class="stat-card">
             <el-icon class="icon-product"><Goods /></el-icon>
            <div class="stat-info">
              <div class="stat-title">总商品数</div>
              <div class="stat-value">{{ summary.productCount }}</div>
              <div class="stat-trend" :class="productGrowth >= 0 ? 'positive' : 'negative'">
                {{ productGrowth >= 0 ? '+' : '' }}{{ productGrowth }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/order-management')">
          <div class="stat-card">
            <el-icon class="icon-order"><Tickets /></el-icon>
            <div class="stat-info">
              <div class="stat-title">总订单数</div>
              <div class="stat-value">{{ summary.orderCount }}</div>
              <div class="stat-trend" :class="orderGrowth >= 0 ? 'positive' : 'negative'">
                {{ orderGrowth >= 0 ? '+' : '' }}{{ orderGrowth }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/delivery-management')">
          <div class="stat-card">
            <el-icon class="icon-delivery"><Van /></el-icon>
            <div class="stat-info">
              <div class="stat-title">待发货订单</div>
              <div class="stat-value">{{ pendingShipments }}</div>
              <div class="stat-trend">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表容器 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>用户增长趋势</span>
              <el-tag type="success">{{ timeRange }}天</el-tag>
            </div>
          </template>
          <div ref="userChartRef" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>订单趋势</span>
              <el-tag type="warning">{{ timeRange }}天</el-tag>
            </div>
          </template>
          <div ref="orderChartRef" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>商品发布趋势</span>
              <el-tag type="info">{{ timeRange }}天</el-tag>
            </div>
          </template>
          <div ref="productChartRef" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>综合趋势对比</span>
              <el-tag type="primary">{{ timeRange }}天</el-tag>
            </div>
          </template>
          <div ref="comparisonChartRef" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { 
  getSummaryStats, 
  getDailyRegistrationStats, 
  getUserGrowthStats, 
  getOrderTrendStats, 
  getProductTrendStats,
  getDeliveryStats
} from '../../api/admin';
import { User, Goods, Tickets, Van } from '@element-plus/icons-vue';

const router = useRouter();

const summary = reactive({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
});

const timeRange = ref(15);
const pendingShipments = ref(0);
const userGrowth = ref(0);
const productGrowth = ref(0);
const orderGrowth = ref(0);

// 图表引用
const userChartRef = ref(null);
const orderChartRef = ref(null);
const productChartRef = ref(null);
const comparisonChartRef = ref(null);

let userChart = null;
let orderChart = null;
let productChart = null;
let comparisonChart = null;

// 计算增长率
const calculateGrowth = (data) => {
  if (data.length < 2) return 0;
  const recent = data.slice(-7).reduce((sum, item) => sum + item.count, 0);
  const previous = data.slice(-14, -7).reduce((sum, item) => sum + item.count, 0);
  if (previous === 0) return recent > 0 ? 100 : 0;
  return Math.round(((recent - previous) / previous) * 100);
};

const fetchSummary = async () => {
  try {
    const response = await getSummaryStats();
    Object.assign(summary, response.data.data);
  } catch (error) {
    console.error("获取总览数据失败:", error);
  }
};

const fetchDeliveryStats = async () => {
  try {
    const response = await getDeliveryStats();
    pendingShipments.value = response.data.data.awaitingShipment || 0;
  } catch (error) {
    console.error("获取配送统计失败:", error);
  }
};

const initUserChart = async () => {
  if (userChartRef.value) {
    userChart = echarts.init(userChartRef.value);
    userChart.showLoading();

    try {
      const response = await getUserGrowthStats(timeRange.value);
      const chartData = response.data.data;
      
      const dates = chartData.map(item => item.date);
      const counts = chartData.map(item => item.count);

      userGrowth.value = calculateGrowth(chartData);

      const option = {
        tooltip: { 
          trigger: 'axis',
          formatter: '{b}<br/>新增用户: {c}'
        },
        xAxis: { 
          type: 'category', 
          data: dates,
          axisLabel: { rotate: 45 }
        },
        yAxis: { type: 'value' },
        series: [{
          name: '新增用户',
          data: counts,
          type: 'line',
          smooth: true,
          itemStyle: { color: '#409EFF' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ]
            }
          }
        }]
      };
      userChart.hideLoading();
      userChart.setOption(option);
    } catch (error) {
      console.error("获取用户增长数据失败:", error);
      userChart.hideLoading();
    }
  }
};

const initOrderChart = async () => {
  if (orderChartRef.value) {
    orderChart = echarts.init(orderChartRef.value);
    orderChart.showLoading();

    try {
      const response = await getOrderTrendStats(timeRange.value);
      const chartData = response.data.data;
      
      const dates = chartData.map(item => item.date);
      const counts = chartData.map(item => item.count);

      orderGrowth.value = calculateGrowth(chartData);

      const option = {
        tooltip: { 
          trigger: 'axis',
          formatter: '{b}<br/>新增订单: {c}'
        },
        xAxis: { 
          type: 'category', 
          data: dates,
          axisLabel: { rotate: 45 }
        },
        yAxis: { type: 'value' },
        series: [{
          name: '新增订单',
          data: counts,
          type: 'bar',
          itemStyle: { color: '#E6A23C' }
        }]
      };
      orderChart.hideLoading();
      orderChart.setOption(option);
    } catch (error) {
      console.error("获取订单趋势数据失败:", error);
      orderChart.hideLoading();
    }
  }
};

const initProductChart = async () => {
  if (productChartRef.value) {
    productChart = echarts.init(productChartRef.value);
    productChart.showLoading();

    try {
      const response = await getProductTrendStats(timeRange.value);
      const chartData = response.data.data;
      
      const dates = chartData.map(item => item.date);
      const counts = chartData.map(item => item.count);

      productGrowth.value = calculateGrowth(chartData);

      const option = {
        tooltip: { 
          trigger: 'axis',
          formatter: '{b}<br/>新增商品: {c}'
        },
        xAxis: { 
          type: 'category', 
          data: dates,
          axisLabel: { rotate: 45 }
        },
        yAxis: { type: 'value' },
        series: [{
          name: '新增商品',
          data: counts,
          type: 'line',
          smooth: true,
          itemStyle: { color: '#67C23A' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
              ]
            }
          }
        }]
      };
      productChart.hideLoading();
      productChart.setOption(option);
    } catch (error) {
      console.error("获取商品趋势数据失败:", error);
      productChart.hideLoading();
    }
  }
};

const initComparisonChart = async () => {
  if (comparisonChartRef.value) {
    comparisonChart = echarts.init(comparisonChartRef.value);
    comparisonChart.showLoading();

    try {
      const [userResponse, orderResponse, productResponse] = await Promise.all([
        getUserGrowthStats(timeRange.value),
        getOrderTrendStats(timeRange.value),
        getProductTrendStats(timeRange.value)
      ]);

      const userData = userResponse.data.data;
      const orderData = orderResponse.data.data;
      const productData = productResponse.data.data;

      const dates = userData.map(item => item.date);
      const userCounts = userData.map(item => item.count);
      const orderCounts = orderData.map(item => item.count);
      const productCounts = productData.map(item => item.count);

      const option = {
        tooltip: { 
          trigger: 'axis',
          formatter: function(params) {
            let result = params[0].axisValue + '<br/>';
            params.forEach(param => {
              result += param.marker + param.seriesName + ': ' + param.value + '<br/>';
            });
            return result;
          }
        },
        legend: {
          data: ['用户', '订单', '商品']
        },
        xAxis: { 
          type: 'category', 
          data: dates,
          axisLabel: { rotate: 45 }
        },
        yAxis: { type: 'value' },
        series: [
          {
            name: '用户',
            data: userCounts,
            type: 'line',
            smooth: true,
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '订单',
            data: orderCounts,
            type: 'line',
            smooth: true,
            itemStyle: { color: '#E6A23C' }
          },
          {
            name: '商品',
            data: productCounts,
            type: 'line',
            smooth: true,
            itemStyle: { color: '#67C23A' }
          }
        ]
      };
      comparisonChart.hideLoading();
      comparisonChart.setOption(option);
    } catch (error) {
      console.error("获取对比数据失败:", error);
      comparisonChart.hideLoading();
    }
  }
};

const handleTimeRangeChange = () => {
  initUserChart();
  initOrderChart();
  initProductChart();
  initComparisonChart();
};

const goTo = (path) => {
  router.push(path);
};

const resizeCharts = () => {
  [userChart, orderChart, productChart, comparisonChart].forEach(chart => {
    if (chart) {
      chart.resize();
    }
  });
};

onMounted(() => {
  fetchSummary();
  fetchDeliveryStats();
  initUserChart();
  initOrderChart();
  initProductChart();
  initComparisonChart();
  
  window.addEventListener('resize', resizeCharts);
});

onUnmounted(() => {
  [userChart, orderChart, productChart, comparisonChart].forEach(chart => {
    if (chart) {
      chart.dispose();
    }
  });
  window.removeEventListener('resize', resizeCharts);
});
</script>

<style scoped>
.dashboard-container {
  padding: 0 0.5vw;
}
.time-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}
.time-label {
  color: #888;
  font-size: 15px;
  margin-right: 8px;
}
.el-radio-group {
  background: #f5f6fa;
  border-radius: 16px;
  padding: 4px 10px;
}
.el-radio-button__inner {
  border-radius: 12px !important;
  font-size: 15px;
  padding: 6px 18px;
  background: transparent;
  color: #222;
  transition: background 0.2s, color 0.2s;
}
.el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: #e5e9f2;
  color: #007aff;
  box-shadow: 0 2px 8px 0 rgba(0,122,255,0.08);
}

.stat-card-clickable {
  border-radius: 22px !important;
  box-shadow: 0 4px 24px 0 rgba(60,60,60,0.10);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;
  background: #fff;
  margin-bottom: 18px;
}
.stat-card-clickable:hover {
  box-shadow: 0 8px 32px 0 rgba(0,122,255,0.12);
  transform: translateY(-4px) scale(1.02);
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 18px;
}
.icon-user, .icon-product, .icon-order, .icon-delivery {
  font-size: 38px;
  color: #007aff;
  background: linear-gradient(120deg, #e0e5ec 0%, #f5f6fa 100%);
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 2px 8px 0 rgba(0,122,255,0.08);
}
.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.stat-title {
  font-size: 15px;
  color: #888;
  font-weight: 500;
}
.stat-value {
  font-size: 2.2em;
  font-weight: 700;
  color: #222;
  margin: 2px 0 0 0;
}
.stat-trend {
  font-size: 1em;
  font-weight: 500;
  margin-top: 2px;
}
.stat-trend.positive {
  color: #34c759;
}
.stat-trend.negative {
  color: #ff3b30;
}

.chart-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 600;
  color: #222;
}
.el-card {
  border-radius: 22px !important;
  box-shadow: 0 4px 24px 0 rgba(60,60,60,0.10);
  border: none;
  transition: box-shadow 0.2s;
}
.el-card:hover {
  box-shadow: 0 8px 32px 0 rgba(0,122,255,0.12);
}
.el-tag {
  border-radius: 10px;
  font-size: 14px;
  padding: 2px 12px;
  font-weight: 500;
}

@media (max-width: 900px) {
  .stat-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .icon-user, .icon-product, .icon-order, .icon-delivery {
    font-size: 30px;
    padding: 8px;
  }
  .stat-value {
    font-size: 1.5em;
  }
}
</style>