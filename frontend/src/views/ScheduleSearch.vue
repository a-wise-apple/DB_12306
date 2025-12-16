<template>
  <div class="schedule-search">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="Date">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="Pick a date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="Train No">
          <el-input v-model="searchForm.trainNo" placeholder="Train No (Optional)" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">Search</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="result-card" v-if="schedules.length > 0">
      <el-table :data="schedules" style="width: 100%">
        <el-table-column prop="train.trainNo" label="Train No" width="180" />
        <el-table-column prop="train.trainType" label="Type" width="180" />
        <el-table-column prop="departDate" label="Depart Date" />
        <el-table-column prop="status" label="Status" />
        <el-table-column label="Actions">
          <template #default="scope">
            <el-button size="small" @click="viewStops(scope.row.id)">Stops</el-button>
            <el-button size="small" type="primary" @click="bookTicket(scope.row.id)">Book</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="stopsDialogVisible" title="Schedule Stops">
      <el-table :data="currentStops">
        <el-table-column prop="sequence" label="Seq" width="60" />
        <el-table-column prop="station.name" label="Station" />
        <el-table-column prop="arrivalTime" label="Arrival" />
        <el-table-column prop="departureTime" label="Departure" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getSchedules, getScheduleStops } from '../api/schedule'
import type { TrainSchedule, ScheduleStop } from '../api/types'
import { ElMessage } from 'element-plus'

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
    ElMessage.warning('Please select a date')
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
}
.search-card {
  margin-bottom: 20px;
}
</style>
