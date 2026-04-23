<script setup lang="ts">
const cards = [
  { title: '待处理争议', value: '0' },
  { title: '今日新订单', value: '0' },
  { title: '进行中订单', value: '0' },
]
</script>

<template>
  <section class="page">
    <h1>dongpaopao 管理台</h1>
    <p class="subtitle">系统已初始化，可继续接入后端接口。</p>

    <div class="cards">
      <article v-for="card in cards" :key="card.title" class="card">
        <h2>{{ card.title }}</h2>
        <strong>{{ card.value }}</strong>
      </article>
    </div>
  </section>
</template>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}

h1 {
  margin: 0 0 8px;
  font-size: 28px;
}

.subtitle {
  margin: 0 0 24px;
  color: #64748b;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.card {
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #ffffff;
}

.card h2 {
  margin: 0 0 12px;
  font-size: 14px;
  color: #475569;
}

.card strong {
  font-size: 24px;
}
</style>
