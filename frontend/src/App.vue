<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from './store/user'
import { useUiStore } from './store/ui'
import { useI18n } from './locales'
import { Moon, Sunny, UserFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const uiStore = useUiStore()
const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const navItems = computed(() => [
  { path: '/', label: t('nav.home'), show: true },
  { path: '/schedules', label: t('nav.schedules'), show: true },
  { path: '/orders', label: t('nav.orders'), show: !!userStore.user },
  { path: '/trading', label: t('nav.trading'), show: true },
  { path: '/admin', label: t('nav.admin'), show: userStore.user?.role === 'ADMIN' }
])

const visibleNav = computed(() => navItems.value.filter(item => item.show))

const handleLogout = () => {
  userStore.logout()
  ElMessage.success(t('home.loggedOut'))
  router.push('/login')
}

const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')
</script>

<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="brand" @click="$router.push('/')">
        <div class="brand__mark">🚄</div>
        <div>
          <p class="brand__title">{{ t('appName') }}</p>
          <p class="brand__subtitle">{{ uiStore.language === 'zh' ? '智能出行 · 安全便捷' : 'Smart Travel · Safe & Fast' }}</p>
        </div>
      </div>

      <nav class="nav-links">
        <a
          v-for="item in visibleNav"
          :key="item.path"
          :class="['nav-link', { active: route.path === item.path }]"
          @click.prevent="$router.push(item.path)"
        >
          {{ item.label }}
        </a>
      </nav>

      <div class="toolbar">
        <el-select v-model="uiStore.language" size="small" class="toolbar__select" @change="uiStore.setLanguage">
          <el-option label="中文" value="zh" />
          <el-option label="English" value="en" />
        </el-select>
        <el-switch
          v-model="uiStore.theme"
          inline-prompt
          :active-text="t('toolbar.dark')"
          :inactive-text="t('toolbar.light')"
          :active-icon="Moon"
          :inactive-icon="Sunny"
          @change="uiStore.setTheme(uiStore.theme)"
        />
        <div class="auth-area">
          <span v-if="userStore.user" class="user-chip">
            <el-icon><UserFilled /></el-icon>
            <span>{{ userStore.user.name }}</span>
          </span>
          <el-button v-if="userStore.user" type="danger" size="small" plain @click="handleLogout">
            {{ t('nav.logout') }}
          </el-button>
          <div v-else class="guest-actions">
            <el-button type="primary" size="small" @click="goLogin">
              {{ t('nav.login') }}
            </el-button>
            <el-button type="success" size="small" plain @click="goRegister">
              {{ t('nav.register') }}
            </el-button>
          </div>
        </div>
      </div>
    </header>

    <main class="app-main">
      <router-view></router-view>
    </main>
  </div>
</template>

<style>
.app-shell {
  min-height: 100vh;
  background: var(--page-bg);
  color: var(--text-primary);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: grid;
  grid-template-columns: 1.2fr 1fr 1.2fr;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  background: var(--surface-glass);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-subtle);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.brand__mark {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--accent), var(--accent-strong));
  color: #fff;
  font-size: 20px;
}

.brand__title {
  margin: 0;
  font-weight: 800;
  letter-spacing: 0.01em;
}

.brand__subtitle {
  margin: 0;
  color: var(--text-secondary);
  font-size: 12px;
}

.nav-links {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.nav-link {
  padding: 8px 12px;
  border-radius: 10px;
  color: var(--text-secondary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-link:hover {
  color: var(--accent);
  background: var(--surface-muted);
}

.nav-link.active {
  color: var(--accent-strong);
  background: rgba(37, 99, 235, 0.14);
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
}

.toolbar__select {
  width: 120px;
}

.auth-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.guest-actions {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 10px;
  background: var(--surface-muted);
  color: var(--text-primary);
  font-weight: 600;
}

.app-main {
  padding: 12px 16px 32px;
}

@media (max-width: 900px) {
  .app-header {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }

  .toolbar {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>
