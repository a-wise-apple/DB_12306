<template>
  <div class="admin-dashboard">
    <h2>{{ copy.title }}</h2>
    <el-tabs v-model="activeTab">

      <el-tab-pane :label="copy.scheduleTab" name="schedule">
        <el-form :model="scheduleForm" label-width="120px">
          <el-form-item :label="copy.train">
            <el-select v-model="scheduleForm.trainId" :placeholder="copy.selectTrain">
              <el-option v-for="train in trains" :key="train.id" :label="train.trainNo" :value="train.id" />
            </el-select>
          </el-form-item>
          <el-form-item :label="copy.date">
            <el-date-picker v-model="scheduleForm.departDate" type="date" :placeholder="copy.pickDate" value-format="YYYY-MM-DD" />
          </el-form-item>

          <h3>{{ copy.stops }}</h3>
          <div v-for="(stop, index) in scheduleForm.stops" :key="index" class="stop-item">
            <el-select v-model="stop.stationId" :placeholder="copy.station">
              <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" />
            </el-select>
            <el-time-picker v-model="stop.arrivalTime" :placeholder="copy.arrival" value-format="HH:mm:ss" />
            <el-time-picker v-model="stop.departureTime" :placeholder="copy.departure" value-format="HH:mm:ss" />
            <el-input-number v-model="stop.sequence" :min="1" :label="copy.sequence" />
            <el-button type="danger" icon="Delete" circle @click="removeStop(index)" />
          </div>
          <el-button type="primary" plain @click="addStop">{{ copy.addStop }}</el-button>

          <h3>{{ copy.coaches }}</h3>
          <div v-for="(coach, index) in scheduleForm.coaches" :key="index" class="coach-item">
            <el-input v-model="coach.coachNo" :placeholder="copy.coachNo" style="width: 100px" />
            <el-select v-model="coach.coachType" :placeholder="copy.coachType">
              <el-option :label="copy.firstClass" value="First Class" />
              <el-option :label="copy.secondClass" value="Second Class" />
            </el-select>
            <el-input-number v-model="coach.seatCount" :min="1" :label="copy.seats" />
            <el-button type="danger" icon="Delete" circle @click="removeCoach(index)" />
          </div>
          <el-button type="primary" plain @click="addCoach">{{ copy.addCoach }}</el-button>

          <div class="form-actions">
            <el-button type="success" @click="submitSchedule">{{ copy.createSchedule }}</el-button>
          </div>
        </el-form>
      </el-tab-pane>

      <el-tab-pane :label="copy.checkinTab" name="checkin">
        <el-form :model="checkinForm" label-width="120px">
          <el-form-item :label="copy.ticketId">
            <el-input v-model="checkinForm.ticketId" />
          </el-form-item>
          <el-form-item :label="copy.station">
            <el-select v-model="checkinForm.stationId" :placeholder="copy.station">
              <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" />
            </el-select>
          </el-form-item>
          <el-form-item :label="copy.employeeId">
            <el-input v-model="checkinForm.employeeId" />
          </el-form-item>
          <el-button type="primary" @click="submitCheckin">{{ copy.checkin }}</el-button>
        </el-form>
      </el-tab-pane>

      <el-tab-pane :label="copy.tradingTab" name="trading">
        <el-button @click="fetchListings">{{ copy.refresh }}</el-button>
        <el-table :data="listings" style="width: 100%">
          <el-table-column prop="listingId" :label="copy.listingId" width="80" />
          <el-table-column prop="trainNumber" :label="copy.train" width="100" />
          <el-table-column prop="sellerName" :label="copy.seller" width="120" />
          <el-table-column prop="price" :label="copy.price" width="100" />
          <el-table-column prop="status" :label="copy.status" width="100" />
          <el-table-column :label="copy.actions">
            <template #default="scope">
              <el-button type="danger" size="small" @click="handleDeleteListing(scope.row.listingId)">{{ copy.delete }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getStations, getTrains, type Station, type Train } from '../api/resource';
import { createSchedule, checkin, deleteListing, type CreateScheduleRequest } from '../api/admin';
import { getListings, type TicketListing } from '../api/trading';
import { useI18n } from '../locales'

const { locale } = useI18n()
const copy = computed(() => locale.value.admin)
const activeTab = ref('schedule');
const stations = ref<Station[]>([]);
const trains = ref<Train[]>([]);
const listings = ref<TicketListing[]>([]);

const scheduleForm = reactive<CreateScheduleRequest>({
  trainId: 0,
  departDate: '',
  stops: [],
  coaches: []
});

const addStop = () => {
  scheduleForm.stops.push({ stationId: 0, arrivalTime: '', departureTime: '', sequence: scheduleForm.stops.length + 1 });
};

const removeStop = (index: number) => {
  scheduleForm.stops.splice(index, 1);
};

const addCoach = () => {
  scheduleForm.coaches.push({ coachNo: '', coachType: 'Second Class', seatCount: 60 });
};

const removeCoach = (index: number) => {
  scheduleForm.coaches.splice(index, 1);
};

const submitSchedule = async () => {
  try {
    await createSchedule(scheduleForm);
    ElMessage.success(copy.value.scheduleSuccess);
    scheduleForm.stops = [];
    scheduleForm.coaches = [];
  } catch (error) {
    ElMessage.error(copy.value.scheduleFailed);
  }
};

const checkinForm = reactive({
  ticketId: '',
  stationId: '',
  employeeId: '1'
});

const submitCheckin = async () => {
  try {
    await checkin(Number(checkinForm.ticketId), Number(checkinForm.stationId), Number(checkinForm.employeeId));
    ElMessage.success(copy.value.checkinSuccess);
  } catch (error) {
    ElMessage.error(copy.value.checkinFailed);
  }
};

const fetchListings = async () => {
  try {
    listings.value = await getListings();
  } catch (error) {
    ElMessage.error(copy.value.loadFailed ?? copy.value.deleteFailed);
  }
};

const handleDeleteListing = async (id: number) => {
  try {
    await ElMessageBox.confirm(copy.value.deleteConfirm);
    await deleteListing(id);
    ElMessage.success(copy.value.deleteSuccess);
    fetchListings();
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(copy.value.deleteFailed);
  }
};

onMounted(async () => {
  stations.value = await getStations();
  trains.value = await getTrains();
  fetchListings();
});
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  color: var(--text-primary);
}
.stop-item, .coach-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}
.form-actions {
  margin-top: 20px;
}
</style>
