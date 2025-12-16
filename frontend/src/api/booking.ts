import service from './axios'
import type { ReserveSeatsRequest } from './types'

export const reserveSeats = (data: ReserveSeatsRequest) => {
  return service.post<any, any>('/bookings/reserve', data)
}

export const getUserBookings = (userId: number) => {
  return service.get<any, any[]>(`/bookings/user/${userId}`)
}

export const cancelBooking = (orderId: number) => {
  return service.post<any, any>(`/bookings/${orderId}/cancel`)
}
