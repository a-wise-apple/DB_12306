<template>
  <div class="home">
    <section class="hero">
      <div class="hero__content">
        <div class="hero__text">
          <p class="hero__eyebrow">{{ copy.heroBadge }}</p>
          <h1>{{ copy.heroTitle }}</h1>
          <p class="hero__subtitle">
            {{ copy.heroSubtitle }}
          </p>
          <div class="hero__actions">
            <el-button type="primary" size="large" @click="$router.push('/schedules')">
              <el-icon><Tickets /></el-icon>
              {{ copy.searchNow }}
            </el-button>
            <el-button
              v-if="userStore.isAuthenticated"
              type="success"
              size="large"
              plain
              @click="$router.push('/orders')"
            >
              <el-icon><List /></el-icon>
              {{ copy.viewOrders }}
            </el-button>
            <el-button v-else type="info" size="large" plain @click="$router.push('/login')">
              <el-icon><User /></el-icon>
              {{ copy.quickLogin }}
            </el-button>
          </div>
          <div v-if="userStore.isAuthenticated" class="hero__status">
            <div class="status__label">{{ copy.currentLogin }}</div>
            <div class="status__content">
              <el-icon class="status__icon"><UserFilled /></el-icon>
              <span class="status__text">{{ userStore.user?.name }} · {{ userStore.user?.role }}</span>
              <el-button size="small" type="danger" plain @click="handleLogout">{{ t('nav.logout') }}</el-button>
            </div>
          </div>
          <div v-else class="hero__status guest">
            <div class="status__label">{{ copy.tipTitle }}</div>
            <p class="status__text">{{ copy.tipContent }}</p>
          </div>
        </div>
        <el-card shadow="hover" class="hero__card">
          <div class="card__header">{{ copy.quickEntry }}</div>
          <div class="card__grid">
            <div class="card__item" @click="$router.push('/trading')">
              <div class="card__icon trading">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div>
                <p class="card__title">{{ copy.tradingTitle }}</p>
                <p class="card__desc">{{ copy.tradingDesc }}</p>
              </div>
            </div>
            <div class="card__item" @click="$router.push('/orders')">
              <div class="card__icon info">
                <el-icon><Document /></el-icon>
              </div>
              <div>
                <p class="card__title">{{ copy.orderCenter }}</p>
                <p class="card__desc">{{ copy.orderDesc }}</p>
              </div>
            </div>
            <div v-if="userStore.user?.role === 'ADMIN'" class="card__item" @click="$router.push('/admin')">
              <div class="card__icon admin">
                <el-icon><Setting /></el-icon>
              </div>
              <div>
                <p class="card__title">{{ copy.adminConsole }}</p>
                <p class="card__desc">{{ copy.adminDesc }}</p>
              </div>
            </div>
            <div v-else class="card__item muted">
              <div class="card__icon muted__icon">
                <el-icon><Lock /></el-icon>
              </div>
              <div>
                <p class="card__title">{{ copy.adminConsole }}</p>
                <p class="card__desc">{{ copy.adminDesc }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <section class="features">
      <div class="section__header">
        <p class="section__eyebrow">{{ copy.toolsBadge }}</p>
        <h2>{{ copy.toolsTitle }}</h2>
        <p class="section__desc">{{ copy.toolsDesc }}</p>
      </div>
      <div class="feature__grid">
        <el-card shadow="hover" class="feature__card">
          <div class="feature__icon primary">
            <el-icon><Search /></el-icon>
          </div>
          <h3>{{ copy.searchTitle }}</h3>
          <p>{{ copy.searchDesc }}</p>
          <el-button type="primary" link @click="$router.push('/schedules')">{{ copy.searchNow }}</el-button>
        </el-card>
        <el-card shadow="hover" class="feature__card">
          <div class="feature__icon success">
            <el-icon><Tickets /></el-icon>
          </div>
          <h3>{{ copy.manageTitle }}</h3>
          <p>{{ copy.manageDesc }}</p>
          <el-button type="success" link @click="$router.push('/orders')">{{ copy.viewOrders }}</el-button>
        </el-card>
        <el-card shadow="hover" class="feature__card">
          <div class="feature__icon warning">
            <el-icon><Guide /></el-icon>
          </div>
          <h3>{{ copy.tradeTitle }}</h3>
          <p>{{ copy.tradeDesc }}</p>
          <el-button type="warning" link @click="$router.push('/trading')">{{ copy.tradingTitle }}</el-button>
        </el-card>
      </div>
    </section>

    <section class="workflow">
      <el-card shadow="never" class="workflow__card">
        <div class="workflow__header">
          <div>
            <p class="section__eyebrow">{{ copy.guideBadge }}</p>
            <h2>{{ copy.guideTitle }}</h2>
            <p class="section__desc">{{ copy.guideDesc }}</p>
          </div>
          <el-button type="primary" plain @click="$router.push('/booking/1')">
            {{ copy.demoBooking }}
            <el-icon class="button__icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="workflow__steps">
          <div class="step">
            <span class="step__index">01</span>
            <div>
              <p class="step__title">{{ copy.step1Title }}</p>
              <p class="step__desc">{{ copy.step1Desc }}</p>
            </div>
          </div>
          <div class="step">
            <span class="step__index">02</span>
            <div>
              <p class="step__title">{{ copy.step2Title }}</p>
              <p class="step__desc">{{ copy.step2Desc }}</p>
            </div>
          </div>
          <div class="step">
            <span class="step__index">03</span>
            <div>
              <p class="step__title">{{ copy.step3Title }}</p>
              <p class="step__desc">{{ copy.step3Desc }}</p>
            </div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from '../locales'

const userStore = useUserStore()
const router = useRouter()
const { t, locale } = useI18n()
const copy = computed(() => locale.value.home)

const handleLogout = () => {
  userStore.logout()
  ElMessage.success(t('home.loggedOut'))
  router.push('/login')
}
</script>

<style scoped>
.home {
  padding: 48px 24px 64px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.hero {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.14), rgba(16, 185, 129, 0.12));
  border-radius: 24px;
  padding: 28px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-strong);
  color: var(--text-primary);
}

.hero__content {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 24px;
  align-items: stretch;
}

.hero__text h1 {
  margin: 4px 0 12px;
  font-size: 32px;
  color: var(--text-primary);
}

.hero__eyebrow {
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: var(--accent);
  font-weight: 700;
  margin-bottom: 6px;
}

.hero__subtitle {
  margin: 0 0 20px;
  color: var(--text-secondary);
  line-height: 1.7;
}

.hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hero__status {
  margin-top: 16px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  padding: 12px 14px;
  backdrop-filter: blur(8px);
  color: var(--text-primary);
}

.hero__status.guest {
  color: var(--text-secondary);
}

.status__label {
  font-size: 12px;
  letter-spacing: 0.04em;
  color: var(--text-secondary);
  text-transform: uppercase;
  margin-bottom: 6px;
}

.status__content {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-primary);
}

.status__icon {
  color: var(--accent);
}

.status__text {
  font-weight: 600;
}

.hero__card {
  border-radius: 18px;
  background: var(--surface);
  border: 1px solid var(--border-color);
}

.card__header {
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 10px;
  color: var(--text-primary);
}

.card__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.card__item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: var(--surface-muted);
  border-radius: 12px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.card__item:hover {
  border-color: var(--accent);
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.12);
}

.card__item.muted {
  cursor: not-allowed;
  background: var(--surface);
  color: var(--text-secondary);
  border: 1px dashed var(--border-color);
}

.card__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  color: #fff;
}

.card__icon.trading {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.card__icon.info {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
}

.card__icon.admin {
  background: linear-gradient(135deg, #f97316, #ea580c);
}

.muted__icon {
  background: linear-gradient(135deg, #94a3b8, #cbd5e1);
  color: #0f172a;
}

.card__title {
  margin: 0;
  font-weight: 700;
  color: var(--text-primary);
}

.card__desc {
  margin: 4px 0 0;
  color: var(--text-secondary);
}

.features,
.workflow {
  background: var(--surface);
  border-radius: 20px;
  padding: 24px 24px 28px;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-subtle);
  color: var(--text-primary);
}

.section__header {
  text-align: center;
  margin-bottom: 20px;
}

.section__eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--accent);
  font-weight: 700;
  margin: 0;
}

.section__header h2 {
  margin: 6px 0 8px;
  color: var(--text-primary);
}

.section__desc {
  margin: 0;
  color: var(--text-secondary);
}

.feature__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.feature__card {
  text-align: left;
  border-radius: 14px;
  background: var(--surface);
}

.feature__card h3 {
  margin: 10px 0 6px;
  color: var(--text-primary);
}

.feature__card p {
  margin: 0 0 12px;
  color: var(--text-secondary);
}

.feature__icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  color: #fff;
  margin-bottom: 8px;
}

.feature__icon.primary {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
}

.feature__icon.success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.feature__icon.warning {
  background: linear-gradient(135deg, #f59e0b, #ea580c);
}

.workflow__card {
  padding: 18px;
  border-radius: 18px;
  background: var(--surface);
}

.workflow__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.workflow__steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.step {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 12px;
  background: var(--surface-muted);
  border: 1px solid var(--border-color);
}

.step__index {
  font-weight: 800;
  color: var(--accent);
  letter-spacing: 0.04em;
}

.step__title {
  margin: 0;
  color: var(--text-primary);
  font-weight: 700;
}

.step__desc {
  margin: 4px 0 0;
  color: var(--text-secondary);
}

.button__icon {
  margin-left: 6px;
}

@media (max-width: 960px) {
  .hero__content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .home {
    padding: 32px 16px;
  }

  .hero__text h1 {
    font-size: 26px;
  }

  .workflow__header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
