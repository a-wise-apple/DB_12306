import service from './axios'
import type { LoginRequest, AuthResponse, RegisterRequest } from './types'

export const login = (data: LoginRequest) => {
  return service.post<any, AuthResponse>('/auth/login', data)
}

export const register = (data: RegisterRequest) => {
  return service.post<any, AuthResponse>('/auth/register', data)
}
