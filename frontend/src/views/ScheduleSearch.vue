<template>
  <div class="schedule-search">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">{{ copy.title }}</div>
      </template>
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item :label="copy.date">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            :placeholder="copy.pickDate"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item :label="copy.trainNo">
          <el-input v-model="searchForm.trainNo" :placeholder="copy.trainPlaceholder" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">{{ copy.search }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="result-card" v-if="schedules.length > 0">
      <el-table :data="schedules" style="width: 100%">
        <el-table-column prop="train.trainNo" :label="copy.trainLabel" width="180" />
        <el-table-column prop="train.trainType" :label="copy.type" width="180" />
        <el-table-column prop="departDate" :label="copy.departDate" />
        <el-table-column prop="status" :label="copy.status" />
        <el-table-column :label="copy.actions">
          <template #default="scope">
            <el-button size="small" @click="viewStops(scope.row.id)">{{ copy.stops }}</el-button>
            <el-button size="small" type="primary" @click="bookTicket(scope.row.id)">{{ copy.book }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="stopsDialogVisible" :title="copy.stops">
      <el-table :data="currentStops">
        <el-table-column prop="sequence" :label="copy.stopSeq" width="60" />
        <el-table-column prop="station.name" :label="copy.station" />
        <el-table-column prop="arrivalTime" :label="copy.arrival" />
        <el-table-column prop="departureTime" :label="copy.departure" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getSchedules, getScheduleStops } from '../api/schedule'
import type { TrainSchedule, ScheduleStop } from '../api/types'
import { ElMessage } from 'element-plus'
import { useI18n } from '../locales'

const { locale } = useI18n()
const copy = computed(() => locale.value.schedule)
const router = useRouter()
const searchForm = reactive({
  date: '',
  trainNo: ''
})

const schedules = ref<TrainSchedule[]>([])
const stopsDialogVisible = ref(false)
const currentStops = ref<ScheduleStop[]>([])

const onSearch = async () => {
  if (!searchForm.date) {
    ElMessage.warning(copy.value.dateWarning)
    return
  }
  try {
    const data = await getSchedules({
      date: searchForm.date,
      trainNo: searchForm.trainNo || undefined
    })
    schedules.value = data
  } catch (error) {
    console.error(error)
  }
}

const viewStops = async (scheduleId: number) => {
  try {
    const data = await getScheduleStops(scheduleId)
    currentStops.value = data
    stopsDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const bookTicket = (scheduleId: number) => {
  router.push(`/booking/${scheduleId}`)
}
</script>

<style scoped>
.schedule-search {
  padding: 20px;
  color: var(--text-primary);
}
.search-card {
  margin-bottom: 20px;
  background: var(--surface);
  border: 1px solid var(--border-color);
}
.result-card {
  background: var(--surface);
  border: 1px solid var(--border-color);
}
.card-header {
  font-weight: 700;
  color: var(--text-primary);
}
</style>
