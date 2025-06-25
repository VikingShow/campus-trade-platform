<template>
  <el-dialog v-model="visible" title="发表评价" width="500px" @close="onClose">
    <el-form :model="form" ref="formRef" label-position="top">
      <el-form-item label="为本次交易打分">
        <el-rate v-model="form.score" :texts="['很差', '较差', '一般', '不错', '很棒']" show-text />
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="form.comment" type="textarea" rows="4" placeholder="分享一下你的交易体验吧..."/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交评价</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, toRefs } from 'vue';
import { ElMessage } from 'element-plus';
import apiClient from '../api/axios.config';

const props = defineProps({
  modelValue: Boolean,
  orderData: Object,
});
const emit = defineEmits(['update:modelValue', 'submitted']);

const { modelValue: visible } = toRefs(props);
const submitting = ref(false);
const form = reactive({
  score: 5,
  comment: '',
});

const onClose = () => {
  emit('update:modelValue', false);
};

const handleSubmit = async () => {
  if (form.score === 0) {
    ElMessage.warning('请为本次交易打分');
    return;
  }
  submitting.value = true;
  try {
    await apiClient.post(`/api/orders/${props.orderData.id}/ratings`, form);
    ElMessage.success('评价成功！');
    emit('submitted'); // 通知父组件刷新列表
    onClose();
  } catch (error) {
    // 错误信息已由拦截器处理
  } finally {
    submitting.value = false;
  }
};
</script>