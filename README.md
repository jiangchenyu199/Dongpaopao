# dongpaopao

项目采用前后端分离结构：

- `apps/backend`：Spring Boot 4 后端（MyBatis-Plus + PostgreSQL + Redis + JWT + springdoc）
- `apps/admin-web`：Vue3 管理端（Vite + Vue Router + Pinia + Axios）
- `db/dongpaopao.sql`：数据库结构脚本（当前唯一数据库脚本来源）
- `docs/essay.md`：项目论文文档

## 环境要求

- Java 21+
- Node.js 22+（或 20+）
- PostgreSQL 14+
- Redis 6+

## 后端启动

1. 创建数据库并执行脚本：
   - 执行 `db/dongpaopao.sql`
2. 配置环境变量（可选，未配置使用默认值）：
   - `DB_URL`（默认 `jdbc:postgresql://localhost:5432/dongpaopao`）
   - `DB_USERNAME`（默认 `postgres`）
   - `DB_PASSWORD`（默认 `postgres`）
   - `REDIS_HOST`（默认 `localhost`）
   - `REDIS_PORT`（默认 `6379`）
3. 运行后端：
   - `cd apps/backend`
   - `./gradlew bootRun`

常用接口：

- 健康检查：`GET /api/health`
- OpenAPI：`GET /v3/api-docs`
- Swagger UI：`GET /swagger-ui.html`

## 管理端启动

1. 安装依赖：
   - `cd apps/admin-web`
   - `npm install`
2. 启动开发服务：
   - `npm run dev`
3. 生产构建：
   - `npm run build`
