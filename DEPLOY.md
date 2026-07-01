# Docker 部署教程

下面假设服务器是 Linux，并且已经安装 Docker Engine 和 Docker Compose 插件。

## 1. 上传项目

把整个 `project` 目录上传到服务器，例如：

```bash
scp -r project root@你的服务器IP:/opt/hzhf
```

登录服务器：

```bash
ssh root@你的服务器IP
cd /opt/hzhf
```

## 2. 配置环境变量

```bash
cp .env.example .env
vim .env
```

建议至少修改：

```bash
MYSQL_ROOT_PASSWORD=换成强密码
WEB_PORT=80
```

`PUBLIC_API_BASE_URL` 通常留空。只有当前端和后端不在同一个域名下时，才设置成类似 `https://api.example.com/api`。

## 3. 启动

```bash
docker compose up -d --build
```

第一次启动时，MySQL 会自动执行：

- `backend/sql/basebackend.sql`
- `backend/sql/supplement.sql`

查看状态：

```bash
docker compose ps
docker compose logs -f backend
```

访问：

```text
http://你的服务器IP/
```

后端接口会通过前端 Nginx 自动代理到 `/api`，例如：

```text
http://你的服务器IP/api/doc.html
```

## 4. 常用运维命令

停止：

```bash
docker compose down
```

重新构建并启动：

```bash
docker compose up -d --build
```

查看日志：

```bash
docker compose logs -f frontend
docker compose logs -f backend
docker compose logs -f mysql
```

备份 MySQL：

```bash
docker exec hzhf-mysql mysqldump -uroot -p zhf > zhf-backup.sql
```

恢复 MySQL：

```bash
cat zhf-backup.sql | docker exec -i hzhf-mysql mysql -uroot -p zhf
```

## 5. 重新初始化数据库

只有确认可以清空数据库时再执行：

```bash
docker compose down -v
docker compose up -d --build
```

`down -v` 会删除 MySQL、Elasticsearch 和上传文件卷，生产环境慎用。

## 6. 服务器安全建议

- 只开放 `80`、`443`、`22` 等必要端口。
- `.env` 里的数据库密码不要提交到公开仓库。
- 需要 HTTPS 时，可以在服务器前面加 Caddy、宝塔、Nginx Proxy Manager，或把本项目的 `WEB_PORT` 改成内网端口后由外层 Nginx 反代。
- 如果服务器内存较小，Elasticsearch 可能占用较多资源；可以先把 `ES_JAVA_OPTS` 调小到 `-Xms256m -Xmx256m`，但搜索功能稳定性会下降。
