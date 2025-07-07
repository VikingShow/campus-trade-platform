<template>
  <div class="form-container">
    <el-card class="form-card">
      <template #header>
        <h2>登录校园二手市场</h2>
      </template>
      <el-form @submit.prevent="handleLogin" :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="学号" prop="username">
          <el-input v-model="form.username" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="btn-primary" native-type="submit" :loading="loading" style="width: 100%;">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="link-area">
         <router-link to="/register" class="btn-info" style="display:inline-block;padding:6px 18px;border-radius:14px;">还没有账号？立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const formRef = ref(null);
const form = reactive({ username: '', password: '' });
const loading = ref(false);

const rules = {
  username: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const handleLogin = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 【最终修正】对于 reactive 对象，直接传递 form 本身，而不是 form.value
        const loginSuccess = await authStore.login(form); 
        
        if (loginSuccess) {
          ElMessage.success('登录成功');
          const redirectPath = route.query.redirect || '/';
          router.replace(redirectPath);
        }
      } catch (error) {
        console.error("Login failed in component:", error);
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style>
.form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding-top: 50px;
}
.form-card {
  width: 450px;
}
.link-area {
  text-align: center;
  margin-top: 10px;
}
</style>
