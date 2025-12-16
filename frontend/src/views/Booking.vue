<template>
  <div class="booking-page">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ copy.title }}</span>
          <el-button class="button" text @click="$router.back()">{{ copy.back }}</el-button>
        </div>
      </template>

      <div v-if="loading" class="loading">{{ copy.loading }}</div>

      <div v-else class="coach-container">
        <div class="legend">
          <span>{{ copy.selectHint }}</span>
          <div class="legend__chips">
            <span class="chip available">{{ copy.statusAvailable }}</span>
            <span class="chip locked">{{ copy.statusLocked }}</span>
            <span class="chip reserved">{{ copy.statusReserved }}</span>
            <span class="chip sold">{{ copy.statusSold }}</span>
          </div>
        </div>
        <div v-for="(seats, coachNo) in seatsByCoach" :key="coachNo" class="coach-section">
          <h3>{{ coachTitle(coachNo, seats[0]?.seat.coach.coachType) }}</h3>
          <div class="seats-grid">
            <div
              v-for="allocation in seats"
              :key="allocation.id"
              class="seat-item"
              :class="{
                available: allocation.status === 'AVAILABLE',
                selected: isSelected(allocation.seat.id),
                locked: allocation.status === 'LOCKED',
                reserved: allocation.status === 'RESERVED',
                sold: allocation.status === 'SOLD'
              }"
              :title="seatStatusLabel(allocation)"
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
          <span>{{ copy.passengerInfo }}</span>
        </div>
      </template>

      <el-form label-width="120px">
        <div v-for="seat in selectedSeats" :key="seat.seatId" class="passenger-form-item">
          <h4>Seat: {{ getSeatLabel(seat.seatId) }}</h4>
          <el-form-item label="Passenger Name">
            <el-input v-model="seat.passengerName" placeholder="Enter passenger name" />
          </el-form-item>
        </div>

        <el-form-item>
          <el-button type="primary" @click="submitBooking">{{ copy.confirm }}</el-button>
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
import { useI18n } from '../locales'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { t, locale } = useI18n()
const copy = computed(() => locale.value.booking)

const scheduleId = Number(route.params.id)

const loading = ref(true)
const allAllocations = ref<SeatAllocation[]>([])
const selectedSeats = ref<SeatPassengerPayload[]>([])

const normalizeSeatNo = (seatNo: string) => {
  const match = seatNo.match(/(\d+)([A-Za-z]?)/)
  return { num: Number(match?.[1] ?? 0), suffix: match?.[2] ?? '' }
}

const seatsByCoach = computed(() => {
  const groups: Record<string, SeatAllocation[]> = {}
  allAllocations.value.forEach(a => {
    const coachNo = a.seat.coach.coachNo
    if (!groups[coachNo]) {
      groups[coachNo] = []
    }
    groups[coachNo].push(a)
  })
  Object.entries(groups).forEach(([key, list]) => {
    groups[key] = list.slice().sort((a, b) => {
      const seatA = normalizeSeatNo(a.seat.seatNo)
      const seatB = normalizeSeatNo(b.seat.seatNo)
      if (seatA.num === seatB.num) return seatA.suffix.localeCompare(seatB.suffix)
      return seatA.num - seatB.num
    })
  })
  return groups
})

onMounted(async () => {
  if (!scheduleId) {
    ElMessage.error(copy.value.invalidSchedule)
    router.push('/schedules')
    return
  }
  try {
    allAllocations.value = await getSeatAllocations(scheduleId)
  } catch (error) {
    console.error(error)
    ElMessage.error(copy.value.loadError)
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

const seatStatusLabel = (allocation: SeatAllocation) => {
  switch (allocation.status) {
    case 'LOCKED':
      return copy.value.statusLocked
    case 'RESERVED':
      return copy.value.statusReserved
    case 'SOLD':
      return copy.value.statusSold
    default:
      return copy.value.statusAvailable
  }
}

const coachTitle = (coachNo: string | number, type?: string) => {
  return t('booking.coachTitle', { coach: coachNo, type: type || copy.value.unknownCoachType })
}

const getSeatLabel = (seatId: number) => {
  const allocation = allAllocations.value.find(a => a.seat.id === seatId)
  if (allocation) {
    return t('booking.seatDisplay', {
      coach: allocation.seat.coach.coachNo,
      seat: allocation.seat.seatNo
    })
  }
  return `${t('booking.seatLabel')} ${seatId}`
}

const submitBooking = async () => {
  if (selectedSeats.value.some(s => !s.passengerName.trim())) {
    ElMessage.warning(copy.value.nameWarning)
    return
  }

  if (!userStore.isAuthenticated || !userStore.user) {
    ElMessage.warning(copy.value.loginWarning)
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

    ElMessage.success(copy.value.success)
    router.push('/schedules')
  } catch (error) {
    console.error(error)
    ElMessage.error(copy.value.failed)
  }
}
</script>

<style scoped>
.booking-page {
  padding: 20px;
  color: var(--text-primary);
}

.box-card {
  margin-bottom: 20px;
  background: var(--surface);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-primary);
}

.coach-section {
  margin-bottom: 20px;
}

.legend {
  margin-bottom: 10px;
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 10px;
  font-size: 12px;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  background: var(--surface);
}

.chip.available {
  border-color: var(--accent);
  color: var(--accent);
}

.chip.locked,
.chip.reserved,
.chip.sold {
  background-color: rgba(148, 163, 184, 0.25);
  color: var(--text-secondary);
}

.seats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.seat-item {
  width: 42px;
  height: 42px;
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: not-allowed;
  background-color: var(--surface-muted);
  border-radius: 6px;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.seat-item.available {
  cursor: pointer;
  background-color: var(--surface);
  border-color: var(--accent);
  color: var(--accent);
}

.seat-item.available:hover {
  box-shadow: var(--shadow-subtle);
  transform: translateY(-2px);
}

.seat-item.selected {
  background-color: var(--accent);
  color: #fff;
}

.seat-item.locked,
.seat-item.reserved,
.seat-item.sold {
  background-color: rgba(148, 163, 184, 0.25);
  color: var(--text-secondary);
}

.passenger-form-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
}
</style>
