import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref('')
    const userInfo = ref(null)

    const setToken = (newToken) => {
        token.value = newToken
    }

    const setUserInfo = (newUserInfo) => {
        userInfo.value = newUserInfo
    }

    const logout = () => {
        token.value = ''
        userInfo.value = null
    }

    return {
        token,
        userInfo,
        setToken,
        setUserInfo,
        logout
    }
}, {
    persist: {
        enabled: true,
        strategies: [
            {
                key: 'user-store',
                storage: localStorage
            }
        ]
    }
})
