<template>
  <el-container class="main-layout">
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-header">
        <el-text class="sidebar-logo" size="large" tag="b">东跑跑</el-text>
        <el-tag size="small" effect="dark" type="info">管理后台</el-tag>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" background-color="#1f2937" text-color="#9ca3af"
        active-text-color="#f59e0b" router>
        <template v-for="item in menuTree" :key="item.id">
          <el-sub-menu v-if="item.children?.length" :index="'sub-' + item.id">
            <template #title>
              <el-icon v-if="item.icon && iconMap[item.icon]">
                <component :is="iconMap[item.icon]" />
              </el-icon>
              <span>{{ item.name }}</span>
            </template>
            <template v-for="child in item.children" :key="child.id">
              <el-sub-menu v-if="child.children?.length" :index="'sub-' + child.id">
                <template #title>{{ child.name }}</template>
                <el-menu-item v-for="leaf in child.children" :key="leaf.id" :index="leaf.path">
                  {{ leaf.name }}
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item v-else-if="child.path" :index="child.path">{{ child.name }}</el-menu-item>
            </template>
          </el-sub-menu>
          <el-menu-item v-else-if="item.path" :index="item.path">
            <el-icon v-if="item.icon && iconMap[item.icon]">
              <component :is="iconMap[item.icon]" />
            </el-icon>
            <span>{{ item.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container direction="vertical" class="main-wrap">
      <el-header height="64px" class="header">
        <div class="header-left">
          <el-text size="large" tag="b">{{ pageTitle }}</el-text>
        </div>
        <div class="header-right">
          <el-text type="info" size="small">{{ adminNickname }}</el-text>
          <el-button type="danger" link size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, Box, Setting, Document } from '@element-plus/icons-vue'
import { getInfo } from '@/api/login'

const route = useRoute()
const router = useRouter()

const iconMap = { House, Box, Setting, Document }

const menuTree = ref([])
function loadMenus() {
  try {
    const raw = localStorage.getItem('admin_menus')
    menuTree.value = raw ? JSON.parse(raw) : []
  } catch {
    menuTree.value = []
  }
}

onMounted(() => {
  loadMenus()
  if (menuTree.value.length === 0) {
    getInfo().then(res => {
      if (res?.data?.menus) {
        localStorage.setItem('admin_menus', JSON.stringify(res.data.menus))
        menuTree.value = res.data.menus
      }
    }).catch(() => {})
  }
})

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title ?? '首页')

const adminNickname = computed(() => {
  try {
    const u = localStorage.getItem('admin_user')
    return u ? (JSON.parse(u).nickname || '管理员') : '管理员'
  } catch {
    return '管理员'
  }
})

const handleLogout = () => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_user')
  localStorage.removeItem('admin_menus')
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  width: 100%;
  min-height: 100vh;
  background: #f3f4f6;
}

.sidebar {
  background: #1f2937;
  flex-shrink: 0;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar-logo {
  color: #fff;
  letter-spacing: 2px;
}

.sidebar-menu {
  border: none;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  margin: 4px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: rgba(245, 158, 11, 0.2) !important;
}

.main-wrap {
  flex: 1;
  min-width: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.main-content {
  flex: 1;
  width: 100%;
  padding: 24px;
  background: #f3f4f6;
  overflow: auto;
  box-sizing: border-box;
}
</style>
