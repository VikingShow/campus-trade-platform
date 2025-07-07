<template>
    <div>
        <div class="profile-header">
            <h3>个人信息</h3>
            <el-button :icon="Edit" circle @click="openDialog" :style="'background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'" />
        </div>
        <el-descriptions :column="1" border>
            <!-- 【最终修正】显示学号而不是ID -->
            <el-descriptions-item label="学号">{{ authStore.user?.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ authStore.user?.nickname }}</el-descriptions-item>
            <el-descriptions-item label="头像">
                <el-avatar :size="60" :icon="UserFilled" :src="authStore.user?.avatar" />
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ authStore.user?.email }}</el-descriptions-item>
            <el-descriptions-item label="个人简介">
                {{ authStore.user?.bio || '这位同学很神秘，什么都还没留下...' }}
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
                        <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="新昵称" prop="nickname">
                    <el-input v-model="form.nickname" />
                </el-form-item>
                <el-form-item label="个人简介" prop="bio">
                    <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="100" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false" :style="'background:#409eff!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'">取消</el-button>
                <el-button @click="handleSubmit" :loading="submitting" :style="'background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'">保存</el-button>
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
  bio: ''
});

const openDialog = () => {
  // 打开对话框时，用当前用户信息填充表单
  form.nickname = authStore.user?.nickname || '';
  form.avatar = authStore.user?.avatar || '';
  form.bio = authStore.user?.bio || '';
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
      ElMessage({
        message: '个人信息更新成功！',
        type: 'success',
        duration: 1500,
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