<template>
    <el-container class="home-container">
        <el-header class="home-header">
            <div class="header-left">
                
            </div>
            <div class="header-right">
                <el-dropdown>
                    <div class="user-info">
                        <el-avatar :size="40"
                            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                        <span class="username">管理员</span>
                        <el-icon class="el-icon--right">
                            <ArrowDown />
                        </el-icon>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item>个人中心</el-dropdown-item>
                            <el-dropdown-item>修改密码</el-dropdown-item>
                            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <el-container>
            <el-aside width="200px" class="home-aside">
                <el-menu :default-active="activeMenu" class="el-menu-vertical" @select="handleSelect">
                    <el-menu-item index="dashboard">
                        <el-icon>
                            <DataAnalysis />
                        </el-icon>
                        <span>仪表板</span>
                    </el-menu-item>
                    <el-menu-item index="users">
                        <el-icon>
                            <User />
                        </el-icon>
                        <span>用户管理</span>
                    </el-menu-item>
                    <el-menu-item index="orders">
                        <el-icon>
                            <Document />
                        </el-icon>
                        <span>订单管理</span>
                    </el-menu-item>
                    <el-menu-item index="settings">
                        <el-icon>
                            <Setting />
                        </el-icon>
                        <span>系统设置</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>

            <el-main class="home-main">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowDown, DataAnalysis, User, Document, Goods, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
    const path = route.path
    if (path.startsWith('/home/')) {
        return path.replace('/home/', '')
    }
    return 'dashboard'
})

const handleSelect = (index) => {
    router.push(`/home/${index}`)
}

const handleLogout = () => {
    console.log('退出登录')
    router.push('/login')
}
</script>

<style scoped>
.home-container {
    height: 100vh;
}

.home-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    border-bottom: 1px solid #e4e7ed;
    padding: 0 20px;
}

.header-left .logo {
    font-size: 24px;
    font-weight: bold;
    color: #409eff;
    margin: 0;
}

.header-right .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 10px;
    transition: all 0.3s;
}

.header-right .user-info:hover {
    background: #f5f7fa;
    border-radius: 4px;
}

.header-right .username {
    margin-left: 10px;
    font-size: 14px;
    color: #303133;
}

.home-aside {
    background: #fff;
    border-right: 1px solid #e4e7ed;
}

.el-menu-vertical {
    border-right: none;
}

.home-main {
    background: #f5f7fa;
    padding: 20px;
}
</style>
