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
        
        <!-- 【新增】邮箱和验证码输入 -->
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="用于接收验证码和找回密码">
            <template #append>
              <el-button @click="handleSendCode" :disabled="isCountingDown">
                {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="verificationCode">
          <el-input v-model="form.verificationCode" placeholder="请输入6位邮箱验证码"></el-input>
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
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { ElMessage } from 'element-plus';
import { sendVerificationCode } from '../api/email'; // 【新增】导入API

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref(null);
const loading = ref(false);

const form = reactive({ 
  username: '', 
  nickname: '', 
  email: '',
  verificationCode: '',
  password: '', 
  confirmPassword: '' 
});

// --- 【新增】验证码相关逻辑 ---
const countdown = ref(0);
const isCountingDown = computed(() => countdown.value > 0);
let timer = null;

const startCountdown = () => {
  countdown.value = 60;
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};

const handleSendCode = async () => {
  // 简单的前端邮箱格式校验
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!form.email || !emailRegex.test(form.email)) {
    ElMessage.warning('请输入有效的邮箱地址');
    return;
  }
  try {
    await sendVerificationCode(form.email);
    ElMessage.success('验证码已发送，请注意查收！');
    startCountdown();
  } catch (error) {
    // 错误信息已由axios拦截器处理
  }
};
// --- 验证码逻辑结束 ---


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
  email: [{ required: true, message: '请输入邮箱地址', trigger: 'blur' }, { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }],
  verificationCode: [{ required: true, message: '请输入6位验证码', trigger: 'blur', len: 6 }],
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
                // 错误信息已由axios拦截器处理
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
