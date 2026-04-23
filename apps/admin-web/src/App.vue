<script setup lang="ts">
const menus = [
  { to: '/dashboard', label: '概览' },
  { to: '/orders', label: '订单管理' },
  { to: '/disputes', label: '争议处理' },
  { to: '/users', label: '用户管理' },
]
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <h1 class="brand">dongpaopao 管理端</h1>
      <nav class="menu">
        <RouterLink
          v-for="menu in menus"
          :key="menu.to"
          class="menu-link"
          active-class="menu-link-active"
          :to="menu.to"
        >
          {{ menu.label }}
        </RouterLink>
      </nav>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>
