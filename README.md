# 黄子弘凡粉丝站

一个前后端分离的黄子弘凡粉丝站项目，包含首页展示、音乐专辑、相册、资讯、论坛、会员中心、商品/订单、私信和后台管理等模块。项目已整理为可直接部署的结构，并补充了 Docker 一键部署配置。

## 技术栈

前端：

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios

后端：

- Spring Boot 2.7
- MyBatis Plus
- MySQL
- Elasticsearch
- Maven

部署：

- Docker
- Docker Compose
- Nginx

## 目录结构

```text
.
├── src/                    # 前端源码
├── public/                 # 前端静态资源
├── backend/                # Spring Boot 后端
│   ├── src/                # 后端源码
│   ├── sql/                # 数据库初始化 SQL
│   └── Dockerfile          # 后端镜像构建
├── docker/
│   └── nginx.conf          # 前端 Nginx 与 API 反代配置
├── Dockerfile              # 前端镜像构建
├── docker-compose.yml      # MySQL + Elasticsearch + 后端 + 前端
├── .env.example            # 环境变量模板
└── DEPLOY.md               # 服务器部署教程
```

## 本地运行

前端：

```bash
npm install
npm run dev
```

后端：

```bash
cd backend
mvn spring-boot:run
```

默认前端开发服务会把 `/api` 代理到 `http://localhost:8121`。

## Docker 部署

复制环境变量模板：

```bash
cp .env.example .env
```

修改 `.env` 中的数据库密码后启动：

```bash
docker compose up -d --build
```

访问：

```text
http://服务器IP/
```

后端接口通过 Nginx 代理到：

```text
http://服务器IP/api
```

更完整的服务器教程见 [DEPLOY.md](DEPLOY.md)。

## 数据库

首次启动 Docker Compose 时，MySQL 会自动执行：

- `backend/sql/basebackend.sql`
- `backend/sql/supplement.sql`

如果已经生成过 Docker 数据卷，再修改 SQL 不会自动重新导入。需要重置数据库时请先备份，再执行：

```bash
docker compose down -v
docker compose up -d --build
```

## 注意事项

- `.env` 包含数据库密码，不要提交到公开仓库。
- `backend/uploads/` 是运行期上传目录，已通过 Docker volume 持久化。
- Elasticsearch 会占用一定内存，小服务器可在 `docker-compose.yml` 中调低 `ES_JAVA_OPTS`。
- 生产环境建议配置 HTTPS，并只开放必要端口。
