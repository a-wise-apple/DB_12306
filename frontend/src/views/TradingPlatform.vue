<template>
  <div class="trading-platform">
    <h2>Ticket Trading Platform</h2>
    
    <el-table :data="listings" style="width: 100%" v-loading="loading">
      <el-table-column prop="trainNumber" label="Train" width="120" />
      <el-table-column prop="departureTime" label="Departure Time" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.departureTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="seatInfo" label="Seat" />
      <el-table-column prop="sellerName" label="Seller" width="120" />
      <el-table-column prop="price" label="Price" width="120">
        <template #default="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="180">
        <template #default="scope">
          <el-button 
            v-if="!isMyListing(scope.row)"
            type="primary" 
            size="small" 
            @click="handleBuy(scope.row)"
          >
            Buy
          </el-button>
          <el-button 
            v-else
            type="danger" 
            size="small" 
            @click="handleCancel(scope.row)"
          >
            Cancel
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getListings, buyListing, cancelListing, type TicketListing } from '../api/trading';
import { useUserStore } from '../store/user';

const listings = ref<TicketListing[]>([]);
const loading = ref(false);
const userStore = useUserStore();

const fetchListings = async () => {
  loading.value = true;
  try {
    listings.value = await getListings();
  } catch (error) {
    ElMessage.error('Failed to load listings');
  } finally {
    loading.value = false;
  }
};

const formatTime = (time: string) => {
  return new Date(time).toLocaleString();
};

const isMyListing = (listing: TicketListing) => {
  return listing.sellerName === userStore.user?.name;
};

const handleBuy = async (listing: TicketListing) => {
  if (!userStore.user) {
    ElMessage.warning('Please login first');
    return;
  }

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to buy this ticket for ¥${listing.price}?`,
      'Confirm Purchase',
      {
        confirmButtonText: 'Buy',
        cancelButtonText: 'Cancel',
        type: 'info',
      }
    );

    await buyListing(listing.listingId, userStore.user.id);
    ElMessage.success('Ticket purchased successfully!');
    fetchListings(); // Refresh list
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Failed to buy ticket');
    }
  }
};

const handleCancel = async (listing: TicketListing) => {
  if (!userStore.user) return;
  
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to remove this listing?',
      'Confirm Cancellation',
      {
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        type: 'warning',
      }
    );

    await cancelListing(listing.listingId, userStore.user.id);
    ElMessage.success('Listing removed');
    fetchListings();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Failed to remove listing');
    }
  }
};

onMounted(() => {
  fetchListings();
});
</script>

<style scoped>
.trading-platform {
  padding: 20px;
}
</style>
