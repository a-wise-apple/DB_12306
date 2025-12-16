<template>
  <div class="admin-dashboard">
    <h2>Admin Dashboard</h2>
    <el-tabs v-model="activeTab">
      
      <!-- Schedule Management -->
      <el-tab-pane label="Schedule Management" name="schedule">
        <el-form :model="scheduleForm" label-width="120px">
          <el-form-item label="Train">
            <el-select v-model="scheduleForm.trainId" placeholder="Select Train">
              <el-option v-for="train in trains" :key="train.id" :label="train.trainNo" :value="train.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Date">
            <el-date-picker v-model="scheduleForm.departDate" type="date" placeholder="Pick a date" value-format="YYYY-MM-DD" />
          </el-form-item>
          
          <h3>Stops</h3>
          <div v-for="(stop, index) in scheduleForm.stops" :key="index" class="stop-item">
            <el-select v-model="stop.stationId" placeholder="Station">
              <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" />
            </el-select>
            <el-time-picker v-model="stop.arrivalTime" placeholder="Arrival" value-format="HH:mm:ss" />
            <el-time-picker v-model="stop.departureTime" placeholder="Departure" value-format="HH:mm:ss" />
            <el-input-number v-model="stop.sequence" :min="1" label="Seq" />
            <el-button type="danger" icon="Delete" circle @click="removeStop(index)" />
          </div>
          <el-button type="primary" plain @click="addStop">Add Stop</el-button>

          <h3>Coaches</h3>
          <div v-for="(coach, index) in scheduleForm.coaches" :key="index" class="coach-item">
            <el-input v-model="coach.coachNo" placeholder="Coach No" style="width: 100px" />
            <el-select v-model="coach.coachType" placeholder="Type">
              <el-option label="First Class" value="First Class" />
              <el-option label="Second Class" value="Second Class" />
            </el-select>
            <el-input-number v-model="coach.seatCount" :min="1" label="Seats" />
            <el-button type="danger" icon="Delete" circle @click="removeCoach(index)" />
          </div>
          <el-button type="primary" plain @click="addCoach">Add Coach</el-button>

          <div class="form-actions">
            <el-button type="success" @click="submitSchedule">Create Schedule</el-button>
          </div>
        </el-form>
      </el-tab-pane>

      <!-- Check-in -->
      <el-tab-pane label="Check-in" name="checkin">
        <el-form :model="checkinForm" label-width="120px">
          <el-form-item label="Ticket ID">
            <el-input v-model="checkinForm.ticketId" />
          </el-form-item>
          <el-form-item label="Station">
            <el-select v-model="checkinForm.stationId" placeholder="Select Station">
              <el-option v-for="station in stations" :key="station.id" :label="station.name" :value="station.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Employee ID">
            <el-input v-model="checkinForm.employeeId" />
          </el-form-item>
          <el-button type="primary" @click="submitCheckin">Check-in</el-button>
        </el-form>
      </el-tab-pane>

      <!-- Trading Moderation -->
      <el-tab-pane label="Trading Moderation" name="trading">
        <el-button @click="fetchListings">Refresh</el-button>
        <el-table :data="listings" style="width: 100%">
          <el-table-column prop="listingId" label="ID" width="80" />
          <el-table-column prop="trainNumber" label="Train" width="100" />
          <el-table-column prop="sellerName" label="Seller" width="120" />
          <el-table-column prop="price" label="Price" width="100" />
          <el-table-column prop="status" label="Status" width="100" />
          <el-table-column label="Actions">
            <template #default="scope">
              <el-button type="danger" size="small" @click="handleDeleteListing(scope.row.listingId)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getStations, getTrains, type Station, type Train } from '../api/resource';
import { createSchedule, checkin, deleteListing, type CreateScheduleRequest } from '../api/admin';
import { getListings, type TicketListing } from '../api/trading';

const activeTab = ref('schedule');
const stations = ref<Station[]>([]);
const trains = ref<Train[]>([]);
const listings = ref<TicketListing[]>([]);

// Schedule Form
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
    ElMessage.success('Schedule created successfully');
    // Reset form
    scheduleForm.stops = [];
    scheduleForm.coaches = [];
  } catch (error) {
    ElMessage.error('Failed to create schedule');
  }
};

// Checkin Form
const checkinForm = reactive({
  ticketId: '',
  stationId: '',
  employeeId: '1' // Default employee ID for now
});

const submitCheckin = async () => {
  try {
    await checkin(Number(checkinForm.ticketId), Number(checkinForm.stationId), Number(checkinForm.employeeId));
    ElMessage.success('Check-in successful');
  } catch (error) {
    ElMessage.error('Check-in failed');
  }
};

// Trading
const fetchListings = async () => {
  try {
    listings.value = await getListings();
  } catch (error) {
    ElMessage.error('Failed to fetch listings');
  }
};

const handleDeleteListing = async (id: number) => {
  try {
    await ElMessageBox.confirm('Are you sure you want to delete this listing?');
    await deleteListing(id);
    ElMessage.success('Listing deleted');
    fetchListings();
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('Failed to delete listing');
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
