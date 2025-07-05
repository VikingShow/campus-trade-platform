<template>
  <div class="dashboard-container">
    <!-- 数据总览卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <!-- 【关键修正】为 el-card 添加了点击事件和交互样式 -->
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/user-management')">
          <div class="stat-card">
            <el-icon class="icon-user"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-title">总用户数</div>
              <div class="stat-value">{{ summary.userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/product-management')">
          <div class="stat-card">
             <el-icon class="icon-product"><Goods /></el-icon>
            <div class="stat-info">
              <div class="stat-title">总商品数</div>
              <div class="stat-value">{{ summary.productCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card-clickable" @click="goTo('/order-management')">
          <div class="stat-card">
            <el-icon class="icon-order"><Tickets /></el-icon>
            <div class="stat-info">
              <div class="stat-title">总订单数</div>
              <div class="stat-value">{{ summary.orderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表容器 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue';
import { useRouter } from 'vue-router'; // 【新增】导入 useRouter
import * as echarts from 'echarts';
import { getSummaryStats, getDailyRegistrationStats } from '../../api/admin';
import { User, Goods, Tickets } from '@element-plus/icons-vue';

const router = useRouter(); // 【新增】获取 router 实例

const summary = reactive({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
});
const chartRef = ref(null);
let myChart = null;

const fetchSummary = async () => {
  try {
    const response = await getSummaryStats();
    Object.assign(summary, response.data.data);
  } catch (error) {
    console.error("获取总览数据失败:", error);
  }
};

const initChart = async () => {
  if (chartRef.value) {
    myChart = echarts.init(chartRef.value);
    myChart.showLoading();

    try {
      const response = await getDailyRegistrationStats();
      const chartData = response.data.data;
      
      const dates = chartData.map(item => item.date);
      const counts = chartData.map(item => item.count);

      const option = {
        title: { text: '最近7日新增用户趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: dates },
        yAxis: { type: 'value' },
        series: [{
          name: '新增用户数',
          data: counts,
          type: 'line',
          smooth: true,
          itemStyle: { color: '#409EFF' }
        }]
      };
      myChart.hideLoading();
      myChart.setOption(option);
    } catch (error) {
      console.error("获取图表数据失败:", error);
      myChart.hideLoading();
    }
  }
};

// 【新增】用于页面跳转的函数
const goTo = (path) => {
  router.push(path);
};

onMounted(() => {
  fetchSummary();
  initChart();
  window.addEventListener('resize', () => {
    if (myChart) {
      myChart.resize();
    }
  });
});

onUnmounted(() => {
  if (myChart) {
    myChart.dispose();
  }
});
</script>

<style scoped>
/* 【新增】为可点击的卡片添加样式 */
.stat-card-clickable {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card-clickable:hover {
  transform: translateY(-5px);
  box-shadow: var(--el-box-shadow-light);
}
.stat-card {
  display: flex;
  align-items: center;
}
.stat-card .el-icon {
  font-size: 48px;
  margin-right: 20px;
}
.icon-user { color: #409EFF; }
.icon-product { color: #67C23A; }
.icon-order { color: #E6A23C; }
.stat-info {
  display: flex;
  flex-direction: column;
}
.stat-title {
  font-size: 16px;
  color: #909399;
  margin-bottom: 5px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
}
</style>