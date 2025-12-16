import service from './axios'
import type { LoginRequest, AuthResponse } from './types'

export const login = (data: LoginRequest) => {
  return service.post<any, AuthResponse>('/auth/login', data)
}
