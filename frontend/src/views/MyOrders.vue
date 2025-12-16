<template>
  <div class="my-orders">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>My Orders</span>
        </div>
      </template>
      
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="id" label="Order ID" width="100" />
        <el-table-column prop="status" label="Status" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="Amount" width="120" />
        <el-table-column prop="createdAt" label="Created At" />
        <el-table-column label="Actions">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'PENDING' || scope.row.status === 'PAID'"
              size="small" 
              type="danger" 
              @click="handleCancel(scope.row.id)"
            >
              Cancel
            </el-button>
            <el-button 
              v-if="scope.row.status === 'PAID'"
              size="small" 
              type="warning" 
              @click="handleSell(scope.row.id)"
            >
              Sell
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUserBookings, cancelBooking } from '../api/booking'
import { createListing } from '../api/trading'
import { useUserStore } from '../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const orders = ref<any[]>([])

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
    'Are you sure you want to cancel this order?',
    'Warning',
    {
      confirmButtonText: 'Yes',
      cancelButtonText: 'No',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await cancelBooking(orderId)
      ElMessage.success('Order cancelled')
      // Refresh list
      if (userStore.user?.id) {
        orders.value = await getUserBookings(userStore.user.id)
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('Failed to cancel order')
    }
  })
}

const handleSell = (orderId: number) => {
  ElMessageBox.prompt('Please enter the selling price', 'Sell Ticket', {
    confirmButtonText: 'List for Sale',
    cancelButtonText: 'Cancel',
    inputPattern: /^\d+(\.\d{1,2})?$/,
    inputErrorMessage: 'Invalid Price',
  }).then(async ({ value }) => {
    try {
      if (userStore.user?.id) {
        await createListing(userStore.user.id, orderId, parseFloat(value))
        ElMessage.success('Ticket listed for sale successfully')
      }
    } catch (error) {
      console.error(error)
      ElMessage.error('Failed to list ticket')
    }
  })
}
</script>

<style scoped>
.my-orders {
  padding: 20px;
}
</style>
