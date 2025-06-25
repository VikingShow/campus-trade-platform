<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>后台管理系统登录</h2>
      </template>
      <el-form @submit.prevent="handleLogin" :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="管理员账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%;">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/authStore';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref(null);
const form = reactive({ username: '', password: '' });
const loading = ref(false);

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const handleLogin = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const loginSuccess = await authStore.login(form);
        if (loginSuccess) {
          router.push('/');
        } else {
          ElMessage.error('登录失败，请检查账号或权限');
        }
      } catch (error) {
         ElMessage.error(error.message || '登录失败');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.login-container { display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f0f2f5; }
.login-card { width: 400px; }
</style>