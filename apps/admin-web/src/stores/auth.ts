import { defineStore } from 'pinia'

type AuthState = {
  accessToken: string
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    accessToken: '',
  }),
  actions: {
    setToken(token: string) {
      this.accessToken = token
    },
    clearToken() {
      this.accessToken = ''
    },
  },
})
