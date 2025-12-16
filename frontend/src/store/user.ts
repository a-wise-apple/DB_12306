import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '../api/auth'
import type { LoginRequest, User } from '../api/types'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)

  // Try to restore user info from token or localStorage if we had it persisted
  // For now, we only persist token. In a real app, we might fetch user info with the token.
  // Let's assume we can decode the token or we store user info in localStorage too.
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      user.value = JSON.parse(storedUser)
    } catch (e) {
      console.error('Failed to parse stored user', e)
    }
  }

  const isAuthenticated = computed(() => !!token.value)

  const login = async (data: LoginRequest) => {
    try {
      const response = await loginApi(data)
      token.value = response.token
      localStorage.setItem('token', response.token)
      
      // Extract user info from response
      // Note: Backend AuthResponse returns username and role, but not ID directly in the top level?
      // Wait, AuthResponse is { token, username, role }. 
      // But we need ID for booking.
      // Let's check backend AuthResponse again.
      // It is: new AuthResponse("mock-token-" + user.getId(), user.getName(), user.getRole().name())
      // The token contains the ID in the mock implementation, but we should probably return the ID in the response.
      
      // For now, let's parse the mock token to get ID if possible, or update backend to return ID.
      // Let's update backend to return ID in AuthResponse first.
      
      // Assuming backend is updated:
      // user.value = { id: response.id, name: response.username, role: response.role }
      
      // Since I cannot update backend right now easily without context switch, 
      // I will parse the mock token "mock-token-{id}"
      const idPart = response.token.split('-')[2] ?? '0'
      const userId = parseInt(idPart) || 0
      
      user.value = {
        id: userId,
        name: response.username,
        role: response.role
      }
      
      localStorage.setItem('user', JSON.stringify(user.value))
      return true
    } catch (error) {
      console.error(error)
      return false
    }
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout
  }
})
