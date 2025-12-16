<template>
  <div class="trading-platform">
    <h2>{{ copy.title }}</h2>

    <el-table :data="listings" style="width: 100%" v-loading="loading">
      <el-table-column prop="trainNumber" :label="copy.train" width="120" />
      <el-table-column prop="departureTime" :label="copy.departure" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.departureTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="seatInfo" :label="copy.seat" />
      <el-table-column prop="sellerName" :label="copy.seller" width="120" />
      <el-table-column prop="price" :label="copy.price" width="120">
        <template #default="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column :label="copy.actions" width="180">
        <template #default="scope">
          <el-button
            v-if="!isMyListing(scope.row)"
            type="primary"
            size="small"
            @click="handleBuy(scope.row)"
          >
            {{ copy.buy }}
          </el-button>
          <el-button
            v-else
            type="danger"
            size="small"
            @click="handleCancel(scope.row)"
          >
            {{ copy.cancel }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getListings, buyListing, cancelListing, type TicketListing } from '../api/trading';
import { useUserStore } from '../store/user';
import { useI18n } from '../locales'

const listings = ref<TicketListing[]>([]);
const loading = ref(false);
const userStore = useUserStore();
const { locale } = useI18n()
const copy = computed(() => locale.value.trading)

const fetchListings = async () => {
  loading.value = true;
  try {
    listings.value = await getListings();
  } catch (error) {
    ElMessage.error(copy.value.loadFailed);
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
    ElMessage.warning(copy.value.needLogin);
    return;
  }

  try {
    await ElMessageBox.confirm(
      copy.value.confirmBuy.replace('{price}', String(listing.price)),
      copy.value.confirmTitle,
      {
        confirmButtonText: copy.value.buyAction,
        cancelButtonText: copy.value.buyCancel,
        type: 'info',
      }
    );

    await buyListing(listing.listingId, userStore.user.id);
    ElMessage.success(copy.value.buySuccess);
    fetchListings();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(copy.value.buyFailed);
    }
  }
};

const handleCancel = async (listing: TicketListing) => {
  if (!userStore.user) return;

  try {
    await ElMessageBox.confirm(
      copy.value.removeConfirm,
      copy.value.removeTitle,
      {
        confirmButtonText: copy.value.removeYes,
        cancelButtonText: copy.value.removeNo,
        type: 'warning',
      }
    );

    await cancelListing(listing.listingId, userStore.user.id);
    ElMessage.success(copy.value.removeSuccess);
    fetchListings();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(copy.value.removeFailed);
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
  color: var(--text-primary);
}
</style>
