<template>
  <div class="home">
    <section class="hero">
      <div class="hero__content">
        <div class="hero__text">
          <p class="hero__eyebrow">智能铁路出行助手</p>
          <h1>一站式完成火车票查询、预订与订单管理</h1>
          <p class="hero__subtitle">
            通过清晰的操作入口快速完成行程安排，随时掌握订单状态，并在需要时进入余票交易平台或管理员后台。
          </p>
          <div class="hero__actions">
            <el-button type="primary" size="large" @click="$router.push('/schedules')">
              <el-icon><Tickets /></el-icon>
              立即查询车次
            </el-button>
            <el-button
              v-if="userStore.isAuthenticated"
              type="success"
              size="large"
              plain
              @click="$router.push('/orders')"
            >
              <el-icon><List /></el-icon>
              查看我的订单
            </el-button>
            <el-button v-else type="info" size="large" plain @click="$router.push('/login')">
              <el-icon><User /></el-icon>
              快速登录
            </el-button>
          </div>
          <div v-if="userStore.isAuthenticated" class="hero__status">
            <div class="status__label">当前登录</div>
            <div class="status__content">
              <el-icon class="status__icon"><UserFilled /></el-icon>
              <span class="status__text">{{ userStore.user?.name }} · {{ userStore.user?.role }}</span>
              <el-button size="small" type="danger" plain @click="handleLogout">退出</el-button>
            </div>
          </div>
          <div v-else class="hero__status guest">
            <div class="status__label">小提示</div>
            <p class="status__text">登录后可快速查看订单、参与余票交易或进入管理员后台。</p>
          </div>
        </div>
        <el-card shadow="hover" class="hero__card">
          <div class="card__header">快捷入口</div>
          <div class="card__grid">
            <div class="card__item" @click="$router.push('/trading')">
              <div class="card__icon trading">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div>
                <p class="card__title">余票交易平台</p>
                <p class="card__desc">查看并挂出车票，灵活调整行程。</p>
              </div>
            </div>
            <div class="card__item" @click="$router.push('/orders')">
              <div class="card__icon info">
                <el-icon><Document /></el-icon>
              </div>
              <div>
                <p class="card__title">订单中心</p>
                <p class="card__desc">管理已购票、退款与改签请求。</p>
              </div>
            </div>
            <div v-if="userStore.user?.role === 'ADMIN'" class="card__item" @click="$router.push('/admin')">
              <div class="card__icon admin">
                <el-icon><Setting /></el-icon>
              </div>
              <div>
                <p class="card__title">管理员后台</p>
                <p class="card__desc">调度资源、审核信息，一站掌握。</p>
              </div>
            </div>
            <div v-else class="card__item muted">
              <div class="card__icon muted__icon">
                <el-icon><Lock /></el-icon>
              </div>
              <div>
                <p class="card__title">管理员后台</p>
                <p class="card__desc">管理员登录后可访问更多功能。</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <section class="features">
      <div class="section__header">
        <p class="section__eyebrow">便捷工具</p>
        <h2>将复杂流程化繁为简</h2>
        <p class="section__desc">精选常用操作与贴心提醒，让订票体验更加顺畅。</p>
      </div>
      <div class="feature__grid">
        <el-card shadow="hover" class="feature__card">
          <div class="feature__icon primary">
            <el-icon><Search /></el-icon>
          </div>
          <h3>精准车次搜索</h3>
          <p>按日期、车次或车站快速筛选，支持一键跳转预订。</p>
          <el-button type="primary" link @click="$router.push('/schedules')">立即搜索</el-button>
        </el-card>
        <el-card shadow="hover" class="feature__card">
          <div class="feature__icon success">
            <el-icon><Tickets /></el-icon>
          </div>
          <h3>安心订单管理</h3>
          <p>集中查看所有订单，及时处理退改签并接收状态反馈。</p>
          <el-button type="success" link @click="$router.push('/orders')">查看订单</el-button>
        </el-card>
        <el-card shadow="hover" class="feature__card">
          <div class="feature__icon warning">
            <el-icon><Guide /></el-icon>
          </div>
          <h3>余票交易协作</h3>
          <p>在官方渠道安全转让余票，让每一张车票发挥最大价值。</p>
          <el-button type="warning" link @click="$router.push('/trading')">进入交易</el-button>
        </el-card>
      </div>
    </section>

    <section class="workflow">
      <el-card shadow="never" class="workflow__card">
        <div class="workflow__header">
          <div>
            <p class="section__eyebrow">贴心指引</p>
            <h2>三步完成行程安排</h2>
            <p class="section__desc">从搜索到预订再到管理，每一步都保持透明与简洁。</p>
          </div>
          <el-button type="primary" plain @click="$router.push('/booking/1')">
            体验示例预订
            <el-icon class="button__icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="workflow__steps">
          <div class="step">
            <span class="step__index">01</span>
            <div>
              <p class="step__title">搜索最优车次</p>
              <p class="step__desc">输入出发到达信息，智能排序与筛选满足不同需求。</p>
            </div>
          </div>
          <div class="step">
            <span class="step__index">02</span>
            <div>
              <p class="step__title">完成在线预订</p>
              <p class="step__desc">选择座席与乘车人信息，实时确认余票后安全支付。</p>
            </div>
          </div>
          <div class="step">
            <span class="step__index">03</span>
            <div>
              <p class="step__title">随时管理订单</p>
              <p class="step__desc">在订单中心查看详情，必要时发起退改签或票务交易。</p>
            </div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('Logged out')
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
  border: 1px solid rgba(37, 99, 235, 0.08);
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.08);
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
  color: #0f172a;
}

.hero__eyebrow {
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: #2563eb;
  font-weight: 700;
  margin-bottom: 6px;
}

.hero__subtitle {
  margin: 0 0 20px;
  color: #475569;
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
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 14px;
  padding: 12px 14px;
  backdrop-filter: blur(8px);
}

.hero__status.guest {
  color: #334155;
}

.status__label {
  font-size: 12px;
  letter-spacing: 0.04em;
  color: #6b7280;
  text-transform: uppercase;
  margin-bottom: 6px;
}

.status__content {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #111827;
}

.status__icon {
  color: #2563eb;
}

.status__text {
  font-weight: 600;
}

.hero__card {
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.card__header {
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 10px;
  color: #0f172a;
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
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.card__item:hover {
  border-color: #2563eb;
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.12);
}

.card__item.muted {
  cursor: not-allowed;
  background: #f1f5f9;
  color: #94a3b8;
  border: 1px dashed #cbd5e1;
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
  background: linear-gradient(135deg, #cbd5e1, #e2e8f0);
  color: #475569;
}

.card__title {
  margin: 0;
  font-weight: 700;
  color: #0f172a;
}

.card__desc {
  margin: 4px 0 0;
  color: #475569;
}

.features,
.workflow {
  background: rgba(255, 255, 255, 0.82);
  border-radius: 20px;
  padding: 24px 24px 28px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 8px 30px rgba(15, 23, 42, 0.06);
}

.section__header {
  text-align: center;
  margin-bottom: 20px;
}

.section__eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #2563eb;
  font-weight: 700;
  margin: 0;
}

.section__header h2 {
  margin: 6px 0 8px;
  color: #0f172a;
}

.section__desc {
  margin: 0;
  color: #475569;
}

.feature__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.feature__card {
  text-align: left;
  border-radius: 14px;
}

.feature__card h3 {
  margin: 10px 0 6px;
  color: #0f172a;
}

.feature__card p {
  margin: 0 0 12px;
  color: #475569;
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
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.step__index {
  font-weight: 800;
  color: #2563eb;
  letter-spacing: 0.04em;
}

.step__title {
  margin: 0;
  color: #0f172a;
  font-weight: 700;
}

.step__desc {
  margin: 4px 0 0;
  color: #475569;
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
