<template>
  <div class="booking-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>Select Seats</span>
          <el-button class="button" text @click="$router.back()">Back</el-button>
        </div>
      </template>
      
      <div v-if="loading" class="loading">Loading seats...</div>
      
      <div v-else class="coach-container">
        <div v-for="(seats, coachNo) in seatsByCoach" :key="coachNo" class="coach-section">
          <h3>Coach {{ coachNo }} ({{ seats[0]?.seat.coach.coachType }})</h3>
          <div class="seats-grid">
            <div 
              v-for="allocation in seats" 
              :key="allocation.id" 
              class="seat-item"
              :class="{ 
                'available': allocation.status === 'AVAILABLE',
                'selected': isSelected(allocation.seat.id)
              }"
              @click="toggleSeat(allocation)"
            >
              {{ allocation.seat.seatNo }}
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="box-card passenger-info" v-if="selectedSeats.length > 0">
      <template #header>
        <div class="card-header">
          <span>Passenger Information</span>
        </div>
      </template>
      
      <el-form label-width="120px">
        <div v-for="(seat, index) in selectedSeats" :key="seat.seatId" class="passenger-form-item">
          <h4>Seat: {{ getSeatLabel(seat.seatId) }}</h4>
          <el-form-item label="Passenger Name">
            <el-input v-model="seat.passengerName" placeholder="Enter passenger name" />
          </el-form-item>
        </div>
        
        <el-form-item>
          <el-button type="primary" @click="submitBooking">Confirm Booking</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSeatAllocations } from '../api/schedule'
import { reserveSeats } from '../api/booking'
import type { SeatAllocation, SeatPassengerPayload } from '../api/types'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const scheduleId = Number(route.params.id)

const loading = ref(true)
const allAllocations = ref<SeatAllocation[]>([])
const selectedSeats = ref<SeatPassengerPayload[]>([])

const seatsByCoach = computed(() => {
  const groups: Record<string, SeatAllocation[]> = {}
  allAllocations.value.forEach(a => {
    const coachNo = a.seat.coach.coachNo
    if (!groups[coachNo]) {
      groups[coachNo] = []
    }
    groups[coachNo].push(a)
  })
  return groups
})

onMounted(async () => {
  if (!scheduleId) {
    ElMessage.error('Invalid schedule ID')
    router.push('/schedules')
    return
  }
  try {
    allAllocations.value = await getSeatAllocations(scheduleId)
  } catch (error) {
    console.error(error)
    ElMessage.error('Failed to load seats')
  } finally {
    loading.value = false
  }
})

const isSelected = (seatId: number) => {
  return selectedSeats.value.some(s => s.seatId === seatId)
}

const toggleSeat = (allocation: SeatAllocation) => {
  if (allocation.status !== 'AVAILABLE') return
  
  const seatId = allocation.seat.id
  const index = selectedSeats.value.findIndex(s => s.seatId === seatId)
  
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
  } else {
    selectedSeats.value.push({
      seatId: seatId,
      passengerName: ''
    })
  }
}

const getSeatLabel = (seatId: number) => {
  const allocation = allAllocations.value.find(a => a.seat.id === seatId)
  if (allocation) {
    return `Coach ${allocation.seat.coach.coachNo} - ${allocation.seat.seatNo}`
  }
  return `Seat ${seatId}`
}

const submitBooking = async () => {
  // Validate names
  if (selectedSeats.value.some(s => !s.passengerName.trim())) {
    ElMessage.warning('Please enter all passenger names')
    return
  }

  if (!userStore.isAuthenticated || !userStore.user) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }

  try {
    const userId = userStore.user.id
    
    await reserveSeats({
      userId,
      scheduleId,
      seats: selectedSeats.value
    })
    
    ElMessage.success('Booking confirmed!')
    router.push('/schedules')
  } catch (error) {
    console.error(error)
    ElMessage.error('Booking failed')
  }
}
</script>

<style scoped>
.booking-page {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
}
.coach-section {
  margin-bottom: 20px;
}
.seats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.seat-item {
  width: 40px;
  height: 40px;
  border: 1px solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: not-allowed;
  background-color: #eee;
  border-radius: 4px;
}
.seat-item.available {
  cursor: pointer;
  background-color: #fff;
  border-color: #409eff;
  color: #409eff;
}
.seat-item.selected {
  background-color: #409eff;
  color: #fff;
}
.passenger-form-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}
</style>
