<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>{{ copy.title }}</span>
        </div>
      </template>
      <el-form :model="form" label-width="120px">
        <el-form-item :label="copy.username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item :label="copy.password">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">{{ copy.submit }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { useI18n } from '../locales'

const router = useRouter()
const userStore = useUserStore()
const { locale } = useI18n()
const copy = computed(() => locale.value.login)

const form = reactive({
  username: '',
  password: ''
})

const onSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning(copy.value.missing)
    return
  }

  const success = await userStore.login({
    username: form.username,
    password: form.password
  })

  if (success) {
    ElMessage.success(copy.value.success)
    router.push('/')
  } else {
    ElMessage.error(copy.value.failed)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background: var(--page-bg);
  color: var(--text-primary);
}
.login-card {
  width: 420px;
  background: var(--surface);
  border: 1px solid var(--border-color);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-primary);
}
</style>
