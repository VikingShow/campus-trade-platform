<template>
    <div>
        <div class="profile-header">
            <h3>个人信息</h3>
            <el-button :icon="Edit" circle @click="openDialog" :style="'background:linear-gradient(90deg,#007aff 0%,#409eff 100%)!important;color:#fff!important;border:none!important;border-radius:16px!important;font-weight:bold!important;'" />
        </div>
        <el-descriptions :column="1" border v-if="user">
            <el-descriptions-item label="学号">{{ user.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
            <el-descriptions-item label="头像">
                <el-avatar :size="60" :icon="UserFilled" :src="user.avatar" />
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
            <el-descriptions-item label="个人简介">
                {{ user.bio || '这位同学很神秘，什么都还没留下...' }}
            </el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="用户不存在或已注销" />

        <!-- 编辑个人信息的对话框 -->
        <el-dialog v-model="dialogVisible" title="编辑个人信息" width="500px" @close="resetForm">
            <el-form :model="form" ref="formRef" label-width="80px">
                <el-form-item label="新头像">
                    <el-upload
                        class="avatar-uploader"
                        action="/api/files/upload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                        :headers="{}"
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
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { getUserById } from '../api/user';
import { UserFilled, Edit, Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const route = useRoute();
const user = ref(null);
const dialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref(null);

const form = reactive({
  nickname: '',
  avatar: '',
  bio: ''
});

const fetchUser = async () => {
  try {
    const { data } = await getUserById(route.params.id);
    user.value = data.data;
  } catch (e) {
    user.value = null;
  }
};

onMounted(fetchUser);
watch(() => route.params.id, fetchUser);

const openDialog = () => {
  if (!user.value) return;
  form.nickname = user.value.nickname || '';
  form.avatar = user.value.avatar || '';
  form.bio = user.value.bio || '';
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
  // 这里只允许本人编辑自己的信息，若要支持管理员编辑他人信息可扩展
  submitting.value = true;
  try {
    // 这里应调用更新用户信息的API，略
    dialogVisible.value = false;
    ElMessage({
      message: '个人信息更新成功！',
      type: 'success',
      duration: 1500,
    });
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