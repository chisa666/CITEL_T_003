---
marp: true
theme: default
paginate: true
size: 16:9
---

# 数据查询与统计系统
## 项目汇报

**CITEL-T-003**

---

# 目录

1. 项目背景与目标
2. 系统架构总览
3. 技术栈选型
4. 后端模块详解
5. 前端模块详解
6. 数据库设计
7. 核心功能流程
8. 开发中的困难与解决方案
9. 总结与展望

---

# 项目背景与目标

## 背景
- 需要对大量人员出行数据进行高效管理与统计分析
- 数据量较大，传统 Excel 方式无法满足多维度查询需求

## 目标
- 构建一个 **B/S 架构** 的数据查询与统计系统
- 支持 **大文件批量导入**、**多维度区间查询**、**可视化图表展示**
- 采用 **高性能架构**（多线程并发 + Redis 缓存）

---

# 系统架构总览

```
┌──────────────────────────────────────────────┐
│              前端 (Vue 3)                      │
│  Element Plus  │  ECharts  │  Axios  │  Router │
└─────────────────────┬────────────────────────┘
                      │ HTTP RESTful API
┌─────────────────────▼────────────────────────┐
│           后端 (Spring Boot 3.2)               │
│  Controller  →  Service  →  Mapper            │
│  ┌─────────┬──────────┬──────────┬─────────┐  │
│  │ 数据导入 │ 区间查询  │ 配置管理  │ 缓存模块 │  │
│  │(JUC并发) │(3种模式) │ (CRUD)   │ (Redis) │  │
│  └─────────┴──────────┴──────────┴─────────┘  │
└─────────────────────┬────────────────────────┘
                      │
┌─────────────────────▼────────────────────────┐
│             数据存储层                         │
│    MySQL 8.0 (持久化)  +  Redis (缓存)         │
└──────────────────────────────────────────────┘
```

---

# 技术栈选型

## 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.5 | 基础框架，IoC / DI |
| MyBatis-Plus | 3.5.6 | ORM，简化 CRUD |
| MySQL | 8.0 | 数据持久化存储 |
| Redis | 7.x | 查询结果缓存 |
| JUC ThreadPool | — | 数据导入并发处理 |
| Maven | 3.x | 项目构建管理 |

## 前端技术栈

| 技术 | 用途 |
|------|------|
| Vue 3 | 前端框架（Composition API） |
| Element Plus | UI 组件库 |
| ECharts | 图表可视化 |
| Axios | HTTP 请求封装 |

---

# 后端模块详解（一）

## 整体分层结构

```
controller/          ← 接收 HTTP 请求，参数校验
    ↓
service/             ← 业务逻辑处理
    ↓
mapper/              ← 数据访问层（MyBatis-Plus + 自定义 SQL）
    ↓
entity/              ← 数据库实体映射
```

## 公共模块 `common/`
- **Result\<T\>**：统一响应格式 `{code, message, data}`
- **GlobalExceptionHandler**：`@RestControllerAdvice` 全局异常拦截
- **BusinessException**：自定义业务异常

---

# 后端模块详解（二）

## 模块 1：数据导入 `DataImportService`

- 读取文本文件，逐行解析为 `PersonTravelData` 实体
- 按 **500 条 / 批** 分批
- 使用 **JUC ThreadPoolExecutor** 多线程并发批量入库
- **CountDownLatch** 等待所有批次完成
- 线程池配置：核心 4 线程 / 最大 8 线程 / 有界队列 10000

```
文件读取 → 解析 → 分批 → 线程池并发 → 批量入库 → 返回结果
                     ↓
              CountDownLatch 汇总
```

---

# 后端模块详解（三）

## 模块 2：区间查询统计 `QueryService`

支持 **三种查询模式**：

| 查询模式 | 维度 | 接口 |
|----------|------|------|
| 年龄查询 | 出生年份 → 年龄区间 | `POST /api/query/age` |
| 里程查询 | 总旅行里程区间 | `POST /api/query/mileage` |
| 时间查询 | 总旅行时间区间 | `POST /api/query/time` |

## 每次查询返回：
- **表格数据**：分页列表（含 personId、性别、年龄、里程、时间）
- **图表数据**：各区间人数统计（用于柱状图 / 饼图展示）

---

# 后端模块详解（四）

## 动态 SQL 区间查询

MyBatis XML 中使用 `<foreach>` 动态生成多区间 OR 条件：

```xml
<select id="queryByAgeRanges" resultType="PersonTravelData">
    SELECT * FROM person_travel_data
    WHERE
    <foreach collection="ranges" item="range"
             separator=" OR " open="(" close=")">
        (YEAR(CURDATE()) - birth_year
         BETWEEN #{range.min} AND #{range.max})
    </foreach>
    ORDER BY person_id
</select>
```

## 区间统计使用 UNION ALL

```sql
SELECT '0-20岁' AS rangeLabel, COUNT(*) AS personCount
FROM person_travel_data
WHERE (YEAR(CURDATE())-birth_year) BETWEEN 0 AND 20
UNION ALL
SELECT '20-40岁' AS rangeLabel, COUNT(*) AS personCount
FROM person_travel_data
WHERE (YEAR(CURDATE())-birth_year) BETWEEN 20 AND 40
```

---

# 后端模块详解（五）

## 模块 3：Redis 缓存策略

- 查询结果以 **JSON 格式** 缓存到 Redis
- 缓存 Key 生成规则：`query:{type}:{rangesHash}:{page}`
- 查询时 **先查缓存**，命中直接返回，未命中查库并写入缓存
- 使用 `GenericJackson2JsonRedisSerializer` 序列化

```
请求 → 生成缓存Key → 查Redis
                      ├─ 命中 → 直接返回
                      └─ 未命中 → 查MySQL → 写入Redis → 返回
```

## 模块 4：区间配置管理 `RangeConfigService`

- 支持对查询区间配置的 **增删改查**
- 配置存储在 `query_range_config` 表，区间数据以 JSON 存储
- 前端可动态管理查询区间的范围和标签

---

# 前端模块详解

## 页面结构

```
App.vue (导航布局)
├── DataImport.vue      数据导入页面
├── AgeQuery.vue        年龄区间查询
├── MileageQuery.vue    里程区间查询
├── TimeQuery.vue       时间区间查询
├── ConfigManage.vue    区间配置管理
├── PieChart.vue        饼图组件
├── BarChart.vue        柱状图组件
├── LineChart.vue       折线图组件
└── ResultTable.vue     结果表格组件
```

## 技术要点
- 使用 **Vue 3 Composition API**（setup 语法糖）
- **ECharts** 实现饼图、柱状图、折线图三种可视化
- **Element Plus** 提供表单、表格、分页、消息提示等 UI
- **Axios** 封装统一请求拦截器

---

# 数据库设计

## 表 1：人员出行数据表 `person_travel_data`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增主键 |
| person_id | BIGINT | 人员 ID |
| gender | TINYINT | 性别 (0-女, 1-男) |
| birth_year | INT | 出生年份 |
| total_mileage | BIGINT | 总旅行里程 |
| total_travel_time | BIGINT | 总旅行时间(分钟) |

索引：`birth_year`、`total_mileage`、`total_travel_time`

## 表 2：查询区间配置表 `query_range_config`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增主键 |
| config_name | VARCHAR(100) | 配置名称 |
| query_type | VARCHAR(20) | 类型: AGE/MILEAGE/TIME |
| range_data | JSON | 区间数据 JSON |

---

# 核心功能流程

## 数据导入流程

```
用户选择文件 → 前端上传 → 后端接收
    → 逐行解析文件（BufferedReader）
    → 每 500 条为一批，提交到线程池
    → 多线程并发批量 INSERT
    → CountDownLatch 等待全部完成
    → 返回导入结果（总数 / 成功 / 失败）
```

## 查询统计流程

```
用户选择区间 → 前端发送请求 → 后端接收
    → 生成缓存 Key → 查 Redis 缓存
    → 缓存未命中 → 执行动态 SQL 查询
    → 分页 + 区间统计 → 组装结果
    → 写入 Redis 缓存 → 返回前端
    → ECharts 渲染图表 + 表格展示
```

---

# 开发中的困难与解决方案

## 困难 1：大文件批量导入性能瓶颈

**问题**：单线程逐条入库，百万级数据导入耗时长

**方案**：
- 引入 **JUC ThreadPoolExecutor**，4 核心 8 最大线程
- 按 **500 条 / 批** 分批处理
- 使用 **CountDownLatch** 协调多线程，确保全部完成后返回结果
- 使用 **有界队列 + CallerRunsPolicy** 拒绝策略，防止内存溢出

---

# 开发中的困难与解决方案

## 困难 2：多区间动态查询的 SQL 编写

**问题**：需要支持用户自定义任意多个区间，且区间可能重叠

**方案**：
- 使用 MyBatis **动态 SQL `<foreach>`** 标签
- 区间之间用 **OR** 条件连接，允许重叠
- 统计查询使用 **UNION ALL** 独立计算每个区间人数
- 区间参数通过 `QueryRangeItem` 内部类传递

---

# 开发中的困难与解决方案

## 困难 3：前后端跨域问题

**问题**：Vite 开发服务器（端口 5173）请求后端（端口 8080）跨域

**方案**：
- 配置 `CorsFilter`，允许所有来源和方法
- 设置 `allowCredentials(true)` 支持 Cookie 传递

## 困难 4：图表数据与表格数据的一致性

**问题**：表格需要分页数据，图表需要全量统计，如何兼顾？

**方案**：
- 设计 `QueryResult` 统一返回 `TableData` + `ChartData`
- 一次查询同时返回两种数据，前端各取所需

---

# 总结

## 项目成果

✅ 完成了 **B/S 架构** 的数据查询与统计系统

✅ 实现了 **三大核心功能**：
- 大文件多线程并发导入
- 年龄 / 里程 / 时间三维度区间查询
- ECharts 可视化图表 + 分页表格

✅ 技术亮点：
- Spring Boot 3.2 + MyBatis-Plus + Redis 缓存
- JUC 线程池并发处理
- 动态 SQL + 统一响应 + 全局异常处理

## 展望

- 可扩展更多查询维度（如性别、地域等）
- 引入消息队列处理超大规模数据导入
- 增加数据导出功能（Excel / PDF）

---

# 感谢聆听

## 欢迎提问 🙋