<template>
  <div class="home">
    <h1>Welcome to Railway Booking System</h1>
    
    <div v-if="userStore.isAuthenticated" class="welcome-msg">
      <p>Hello, {{ userStore.user?.name }}!</p>
    </div>

    <div class="actions">
      <template v-if="!userStore.isAuthenticated">
        <el-button type="primary" @click="$router.push('/login')">Login</el-button>
      </template>
      <template v-else>
        <el-button type="info" @click="$router.push('/orders')">My Orders</el-button>
        <el-button type="warning" @click="$router.push('/trading')">Trading Platform</el-button>
        <el-button v-if="userStore.user?.role === 'ADMIN'" type="primary" @click="$router.push('/admin')">Admin Dashboard</el-button>
        <el-button type="danger" @click="handleLogout">Logout</el-button>
      </template>
      <el-button type="success" @click="$router.push('/schedules')">Search Schedules</el-button>
    </div>
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
  text-align: center;
  margin-top: 50px;
}
.welcome-msg {
  margin: 20px 0;
  font-size: 1.2em;
  color: #409eff;
}
.actions {
  margin-top: 20px;
}
.actions .el-button {
  margin: 0 10px;
}
</style>
