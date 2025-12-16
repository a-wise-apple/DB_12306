import service from './axios'
import type { TrainSchedule, ScheduleStop, SeatAllocation } from './types'

export const getSchedules = (params: { date: string; trainNo?: string; endDate?: string }) => {
  return service.get<any, TrainSchedule[]>('/schedules', { params })
}

export const getScheduleStops = (scheduleId: number) => {
  return service.get<any, ScheduleStop[]>(`/schedules/${scheduleId}/stops`)
}

export const getSeatAllocations = (scheduleId: number) => {
  return service.get<any, SeatAllocation[]>(`/schedules/${scheduleId}/seats`)
}
