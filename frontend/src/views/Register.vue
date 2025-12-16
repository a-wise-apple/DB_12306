<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <div>
            <div class="card-title">{{ copy.title }}</div>
            <div class="card-subtitle">{{ copy.subtitle }}</div>
          </div>
        </div>
      </template>

      <el-form :model="form" label-width="120px" class="register-form">
        <el-form-item :label="copy.name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="copy.idNumber">
          <el-input v-model="form.idNumber" />
        </el-form-item>
        <el-form-item :label="copy.phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item :label="copy.email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item :label="copy.password">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <div class="form-actions">
          <el-button type="primary" @click="onSubmit">{{ copy.submit }}</el-button>
          <el-button type="default" text @click="goLogin">{{ copy.backToLogin }}</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { useI18n } from '../locales'

const router = useRouter()
const userStore = useUserStore()
const { locale } = useI18n()
const copy = computed(() => locale.value.register)

const form = reactive({
  name: '',
  idNumber: '',
  phone: '',
  email: '',
  password: ''
})

const onSubmit = async () => {
  if (!form.name || !form.idNumber || !form.phone || !form.password) {
    ElMessage.warning(copy.value.missing)
    return
  }

  const result = await userStore.register({ ...form })
  if (result.success) {
    ElMessage.success(copy.value.success)
    router.push('/')
  } else if (result.conflict) {
    ElMessage.error(copy.value.conflict)
  } else {
    ElMessage.error(copy.value.failed)
  }
}

const goLogin = () => router.push('/login')
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background: var(--page-bg);
  color: var(--text-primary);
}

.register-card {
  width: 520px;
  background: var(--surface);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-soft);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  color: var(--text-primary);
}

.card-title {
  font-size: 18px;
  font-weight: 700;
}

.card-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.register-form {
  margin-top: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 8px 0 4px;
}
</style>
