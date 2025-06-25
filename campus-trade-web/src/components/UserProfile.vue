<template>
    <div>
        <div class="profile-header">
            <h3>个人信息</h3>
            <el-button type="primary" :icon="Edit" circle @click="openDialog" />
        </div>
        <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">{{ authStore.user?.id }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ authStore.user?.nickname }}</el-descriptions-item>
            <el-descriptions-item label="头像">
                <el-avatar :icon="UserFilled" :src="fullImageUrl(authStore.user?.avatar)" />
            </el-descriptions-item>
        </el-descriptions>

        <!-- 编辑个人信息的对话框 -->
        <el-dialog v-model="dialogVisible" title="编辑个人信息" width="500px" @close="resetForm">
            <el-form :model="form" ref="formRef" label-width="80px">
                <el-form-item label="新头像">
                    <el-upload
                        class="avatar-uploader"
                        action="/api/files/upload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                        :headers="{ 'Authorization': `Bearer ${authStore.token}` }"
                    >
                        <img v-if="form.avatar" :src="fullImageUrl(form.avatar)" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="新昵称" prop="nickname">
                    <el-input v-model="form.nickname" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { UserFilled, Edit, Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const authStore = useAuthStore();
const dialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref(null);

const form = reactive({
  nickname: '',
  avatar: '',
});

const backendUrl = 'http://localhost:8080';
const fullImageUrl = (relativePath) => {
    if (!relativePath) return '';
    if (relativePath.startsWith('http')) return relativePath;
    return `${backendUrl}${relativePath}`;
};

const openDialog = () => {
  form.nickname = authStore.user?.nickname || '';
  form.avatar = authStore.user?.avatar || '';
  dialogVisible.value = true;
};

const handleAvatarSuccess = (response) => {
  form.avatar = response.data.url;
  ElMessage.success('头像上传成功');
};

const resetForm = () => {
    if (formRef.value) {
        formRef.value.resetFields();
    }
};

const handleSubmit = async () => {
  submitting.value = true;
  try {
    const success = await authStore.updateUserProfile(form);
    if (success) {
      dialogVisible.value = false;
      // 【关键修正】使用 Element Plus 的消息框回调，在提示消失后刷新页面
      ElMessage({
        message: '个人信息更新成功！页面将刷新以同步所有数据。',
        type: 'success',
        duration: 2000,
        onClose: () => {
          window.location.reload();
        }
      });
    } else {
      ElMessage.error('更新失败，请稍后重试');
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<style>
.profile-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}
</style>