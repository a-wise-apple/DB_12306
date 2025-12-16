<template>
  <div class="my-orders">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ copy.title }}</span>
        </div>
      </template>

      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="id" :label="copy.orderId" width="100" />
        <el-table-column prop="status" :label="copy.status" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" :label="copy.amount" width="120" />
        <el-table-column prop="createdAt" :label="copy.created" />
        <el-table-column :label="copy.actions">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING' || scope.row.status === 'PAID'"
              size="small"
              type="danger"
              @click="handleCancel(scope.row.id)"
            >
              {{ copy.cancel }}
            </el-button>
            <el-button
              v-if="scope.row.status === 'PAID'"
              size="small"
              type="warning"
              @click="handleSell(scope.row.id)"
            >
              {{ copy.sell }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getUserBookings, cancelBooking } from '../api/booking'
import { createListing } from '../api/trading'
import { useUserStore } from '../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from '../locales'

const userStore = useUserStore()
const orders = ref<any[]>([])
const { locale } = useI18n()
const copy = computed(() => locale.value.orders)

onMounted(async () => {
  if (userStore.user?.id) {
    try {
      orders.value = await getUserBookings(userStore.user.id)
    } catch (error) {
      console.error(error)
    }
  }
})

const getStatusType = (status: string) => {
  switch (status) {
    case 'PAID': return 'success'
    case 'PENDING': return 'warning'
    case 'CANCELLED': return 'info'
    default: return ''
  }
}

const handleCancel = (orderId: number) => {
  ElMessageBox.confirm(
    copy.value.cancelConfirm,
    copy.value.warn,
    {
      confirmButtonText: copy.value.yes,
      cancelButtonText: copy.value.no,
      type: 'warning',
    }
  ).then(async () => {
    try {
      await cancelBooking(orderId)
      ElMessage.success(copy.value.cancelled)
      if (userStore.user?.id) {
        orders.value = await getUserBookings(userStore.user.id)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error(copy.value.cancelFailed)
    }
  })
}

const handleSell = (orderId: number) => {
  ElMessageBox.prompt(copy.value.sellPrompt, copy.value.sellTitle, {
    confirmButtonText: copy.value.sellAction,
    cancelButtonText: copy.value.sellCancel,
    inputPattern: /^\d+(\.\d{1,2})?$/,
    inputErrorMessage: copy.value.priceError,
  }).then(async ({ value }) => {
    try {
      if (userStore.user?.id) {
        await createListing(userStore.user.id, orderId, parseFloat(value))
        ElMessage.success(copy.value.sellSuccess)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error(copy.value.sellFailed)
    }
  })
}
</script>

<style scoped>
.my-orders {
  padding: 20px;
  color: var(--text-primary);
}
.box-card {
  background: var(--surface);
  border: 1px solid var(--border-color);
}
.card-header {
  color: var(--text-primary);
  font-weight: 700;
}
</style>
