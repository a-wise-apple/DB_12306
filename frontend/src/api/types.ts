export interface Train {
  id: number
  trainNo: string
  trainType: string
}

export interface TrainSchedule {
  id: number
  train: Train
  departDate: string
  status: string
}

export interface ScheduleStop {
  id: number
  station: Station
  arrivalTime: string
  departureTime: string
  sequence: number
}

export interface Station {
  id: number
  name: string
  city: string
}

export interface Coach {
  id: number
  coachNo: string
  coachType: string
  seatCount: number
}

export interface Seat {
  id: number
  coach: Coach
  seatNo: string
  seatClass: string
}

export interface SeatAllocation {
  id: number
  schedule: TrainSchedule
  seat: Seat
  status: string
}

export interface SeatPassengerPayload {
  seatId: number
  passengerName: string
}

export interface ReserveSeatsRequest {
  userId: number
  scheduleId: number
  seats: SeatPassengerPayload[]
  holdMinutes?: number
}

export interface LoginRequest {
  username: string // This is actually idNumber based on backend logic
  password: string
}

export interface RegisterRequest {
  name: string
  idNumber: string
  phone: string
  password: string
  email?: string
}

export interface AuthResponse {
  token: string
  userId: number
  username: string
  role: string
}

export interface User {
  id: number
  name: string
  role: string
}
