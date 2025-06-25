<template>
  <div class="form-container">
    <el-card class="form-card">
      <template #header>
        <h2>注册成为校园集市新用户</h2>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleRegister" label-position="top">
        <el-form-item label="学号" prop="username">
          <el-input v-model="form.username" placeholder="将作为你的登录账号"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="大家会看到你的昵称"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码"></el-input>
        </el-form-item>
         <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%;">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="link-area">
         <router-link to="/login">已有账号？直接登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref(null);
const loading = ref(false);

const form = reactive({ username: '', nickname: '', password: '', confirmPassword: '' });

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致!"))
  } else {
    callback()
  }
};

const rules = reactive({
  username: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
});

const handleRegister = async () => {
    if (!formRef.value) return;
    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                const { confirmPassword, ...registerData } = form;
                await authStore.register(registerData);
                ElMessage.success('注册成功！正在跳转到登录页...');
                setTimeout(() => router.push('/login'), 1500);
            } catch (error) {
                // error message is handled by interceptor
            } finally {
                loading.value = false;
            }
        }
    });
};
</script>

<style scoped>
.form-container { display: flex; justify-content: center; align-items: center; padding-top: 50px; }
.form-card { width: 450px; }
.link-area { text-align: center; margin-top: 10px; }
</style>