# 面向中医知识学习的 AI 知识库问答平台

> 本项目是一个基于 Spring Boot 的中医知识库问答系统，面向中医知识学习与资料查询场景。系统支持中医知识分类管理、知识条目管理、知识片段切分、关键词检索，并结合大模型 API 实现基于知识库上下文的问答。

## 1. 项目说明

本项目的核心目标是实现一个面向中医知识学习场景的后端问答系统。

用户提问后，系统会先从本地中医知识库中检索相关知识片段，再将检索结果作为上下文传递给大模型，由大模型基于给定资料生成回答。系统同时返回引用片段，便于用户查看回答依据。

本项目当前版本主要用于 Java 后端学习、项目实践和简历展示，不用于疾病诊断、治疗决策或具体用药指导。

## 2. 核心功能

### 2.1 用户模块

- 用户注册
- 用户登录
- 用户列表查询

当前版本为基础版用户模块，密码暂未加密，暂未接入 JWT。后续可扩展 BCrypt 密码加密和 JWT 登录认证。

### 2.2 中医知识分类模块

支持中医知识分类管理，例如：

- 中药
- 方剂
- 症状
- 证型
- 穴位
- 中医基础理论
- 经典医籍

功能包括：

- 查询分类列表
- 新增分类
- 修改分类
- 删除分类

### 2.3 中医知识条目模块

支持录入和维护具体中医知识条目，例如：

- 黄芪
- 桂枝汤
- 四君子汤
- 气虚
- 足三里
- 阴阳五行

功能包括：

- 新增知识条目
- 分页查询知识条目
- 关键词搜索知识条目
- 查看知识详情
- 修改知识条目
- 删除知识条目

### 2.4 知识片段模块

系统支持将知识条目的长文本内容切分为多个知识片段，并保存到 `knowledge_chunk` 表中。

知识片段用于后续问答检索，避免每次问答都直接使用整篇知识内容。

功能包括：

- 按知识条目 ID 切分知识片段
- 查询某个知识条目的所有片段
- 根据关键词检索知识片段

### 2.5 AI 问答模块

用户提问后，系统会执行以下流程：

```text
用户问题
↓
关键词提取
↓
检索 knowledge_chunk 表
↓
召回相关中医知识片段
↓
构造 Prompt
↓
调用大模型 API
↓
返回回答和引用来源
```

返回内容包括：

- 用户问题
- 大模型回答
- 引用知识片段

## 3. 技术栈

| 技术 | 说明 |
|---|---|
| Spring Boot | 后端主框架 |
| MyBatis-Plus | 数据库 ORM 与 CRUD 简化 |
| MySQL | 关系型数据库 |
| DeepSeek API | 大模型问答接口 |
| OkHttp | 调用大模型 HTTP API |
| Lombok | 简化 Java 实体类代码 |
| Validation | 接口参数校验 |
| Maven | 项目构建工具 |
| Apifox / Postman | 接口测试工具 |

## 4. 项目结构

```text
src/main/java/com/lilin/tcmqa
├── common
│   └── Result.java
├── config
│   └── MybatisPlusConfig.java
├── controller
│   ├── HelloController.java
│   └── TestController.java
├── exception
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── module
│   ├── ai
│   │   └── service
│   │       └── DeepSeekService.java
│   ├── chat
│   │   ├── controller
│   │   │   └── ChatController.java
│   │   ├── dto
│   │   │   └── ChatAskRequest.java
│   │   └── vo
│   │       ├── ChatAskResponse.java
│   │       └── ReferenceChunkVO.java
│   ├── knowledge
│   │   ├── controller
│   │   │   ├── KnowledgeCategoryController.java
│   │   │   ├── KnowledgeItemController.java
│   │   │   └── KnowledgeChunkController.java
│   │   ├── entity
│   │   │   ├── KnowledgeCategory.java
│   │   │   ├── KnowledgeItem.java
│   │   │   └── KnowledgeChunk.java
│   │   └── mapper
│   │       ├── KnowledgeCategoryMapper.java
│   │       ├── KnowledgeItemMapper.java
│   │       └── KnowledgeChunkMapper.java
│   └── user
│       ├── controller
│       │   └── UserController.java
│       ├── dto
│       │   ├── UserLoginRequest.java
│       │   └── UserRegisterRequest.java
│       ├── entity
│       │   └── User.java
│       ├── mapper
│       │   └── UserMapper.java
│       ├── service
│       │   ├── UserService.java
│       │   └── impl
│       │       └── UserServiceImpl.java
│       └── vo
│           └── UserLoginResponse.java
└── util
    └── TextSplitUtil.java
```

## 5. 数据库设计

### 5.1 用户表：`user`

```sql
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='用户表';
```

### 5.2 知识分类表：`knowledge_category`

```sql
CREATE TABLE IF NOT EXISTS knowledge_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='中医知识分类表';
```

### 5.3 知识条目表：`knowledge_item`

```sql
CREATE TABLE IF NOT EXISTS knowledge_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识条目ID',
    title VARCHAR(100) NOT NULL COMMENT '标题，如黄芪、桂枝汤、气虚',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    tags VARCHAR(255) DEFAULT NULL COMMENT '标签，多个标签用逗号分隔',
    content TEXT NOT NULL COMMENT '正文内容',
    source_name VARCHAR(100) DEFAULT NULL COMMENT '来源名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1发布 0草稿',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_title (title)
) COMMENT='中医知识条目表';
```

### 5.4 知识片段表：`knowledge_chunk`

```sql
CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识片段ID',
    item_id BIGINT DEFAULT NULL COMMENT '知识条目ID',
    document_id BIGINT DEFAULT NULL COMMENT '文档ID，后续扩展文档上传模块使用',
    chunk_index INT NOT NULL COMMENT '片段序号',
    chunk_text TEXT NOT NULL COMMENT '片段内容',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1有效 0无效',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_item_id (item_id),
    INDEX idx_document_id (document_id)
) COMMENT='知识片段表';
```

## 6. 初始化测试数据

```sql
INSERT INTO user (username, password, nickname, role, status)
VALUES ('admin', '123456', '管理员', 'ADMIN', 1);

INSERT INTO knowledge_category (name, description, sort_order, status)
VALUES
('中药', '中药功效、性味归经、主治等知识', 1, 1),
('方剂', '经典方剂组成、功效、主治等知识', 2, 1),
('症状', '常见症状及相关中医解释', 3, 1),
('证型', '中医证型相关知识', 4, 1),
('穴位', '穴位定位、功效、主治等知识', 5, 1),
('中医基础理论', '阴阳五行、脏腑经络等基础理论', 6, 1),
('经典医籍', '伤寒论、黄帝内经等经典内容', 7, 1);

INSERT INTO knowledge_item
(title, category_id, tags, content, source_name, status, create_by)
VALUES
('黄芪', 1, '补气,固表,利水', '黄芪为常用补气药，具有补气升阳、益卫固表、利水消肿等功效，常用于气虚乏力、自汗、水肿等相关学习场景。', '中药学资料', 1, 1),
('桂枝汤', 2, '解肌发表,调和营卫', '桂枝汤由桂枝、芍药、生姜、大枣、甘草组成，具有解肌发表、调和营卫的作用，是伤寒论中的经典方剂。', '伤寒论相关资料', 1, 1),
('气虚', 4, '乏力,少气懒言,自汗', '气虚是中医常见证型之一，常见表现包括神疲乏力、少气懒言、自汗、舌淡等。', '中医基础理论资料', 1, 1);
```

## 7. 配置说明

`application.yml` 示例：

```yaml
server:
  port: 8080

spring:
  application:
    name: tcm-ai-qa-backend

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/tcm_ai_qa?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto

deepseek:
  api-key: deepseek-api-key
  base-url: https://api.deepseek.com
  model: deepseek-v4-flash
```



## 8. 核心接口

### 8.1 用户接口

#### 用户注册

```http
POST /api/user/register
```

请求体：

```json
{
  "username": "lilin",
  "password": "123456",
  "nickname": "lilin"
}
```

#### 用户登录

```http
POST /api/user/login
```

请求体：

```json
{
  "username": "lilin",
  "password": "123456"
}
```

#### 用户列表

```http
GET /api/user/list
```

### 8.2 知识分类接口

#### 查询分类列表

```http
GET /api/category/list
```

#### 新增分类

```http
POST /api/category/add
```

请求体：

```json
{
  "name": "体质",
  "description": "中医体质分类相关知识",
  "sortOrder": 8
}
```

#### 修改分类

```http
PUT /api/category/update/{id}
```

#### 删除分类

```http
DELETE /api/category/delete/{id}
```

### 8.3 知识条目接口

#### 新增知识条目

```http
POST /api/knowledge/add
```

请求体：

```json
{
  "title": "四君子汤",
  "categoryId": 2,
  "tags": "益气健脾,脾胃气虚",
  "content": "四君子汤由人参、白术、茯苓、甘草组成，具有益气健脾的作用，常用于脾胃气虚相关学习场景。",
  "sourceName": "方剂学资料",
  "status": 1,
  "createBy": 1
}
```

#### 分页查询知识条目

```http
GET /api/knowledge/page?pageNum=1&pageSize=10
```

#### 关键词搜索知识条目

```http
GET /api/knowledge/page?pageNum=1&pageSize=10&keyword=黄芪
```

#### 查看知识详情

```http
GET /api/knowledge/{id}
```

#### 修改知识条目

```http
PUT /api/knowledge/update/{id}
```

#### 删除知识条目

```http
DELETE /api/knowledge/delete/{id}
```

### 8.4 知识片段接口

#### 切分知识条目

```http
POST /api/chunk/split/{itemId}
```

示例：

```http
POST /api/chunk/split/1
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": 1
}
```

其中 `data` 表示切分出的知识片段数量。

#### 查询某个知识条目的所有片段

```http
GET /api/chunk/item/{itemId}
```

示例：

```http
GET /api/chunk/item/1
```

#### 关键词检索知识片段

```http
GET /api/chunk/search?keyword=补气
```

### 8.5 AI 问答接口

#### 提问

```http
POST /api/chat/ask
```

请求体：

```json
{
  "question": "黄芪有什么功效？",
  "topK": 5
}
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "question": "黄芪有什么功效？",
    "answer": "根据知识库资料，黄芪主要具有补气升阳、益卫固表、利水消肿等功效……",
    "references": [
      {
        "chunkId": 1,
        "itemId": 1,
        "chunkIndex": 1,
        "chunkText": "黄芪为常用补气药，具有补气升阳、益卫固表、利水消肿等功效……"
      }
    ]
  }
}
```

## 9. AI 问答实现流程

```text
用户提问
↓
从问题中提取关键词
↓
使用关键词检索 knowledge_chunk 表
↓
获取 topK 个相关知识片段
↓
将知识片段拼接为上下文
↓
构造 Prompt
↓
调用 DeepSeek 大模型接口
↓
返回模型回答
↓
同时返回 references 引用片段
```

Prompt 中会约束模型：

```text
1. 只能基于给定资料回答，不要脱离资料自由发挥。
2. 如果资料不足，请说明“知识库资料不足，无法给出充分回答”。
3. 回答用于中医知识学习与资料查询。
4. 不要进行疾病诊断。
5. 不要给出具体用药剂量或处方建议。
6. 回答要清晰、简洁、有条理。
```

## 10. 快速启动

### 10.1 克隆项目

```bash
git clone <your-repository-url>
cd tcm-ai-qa-backend
```

### 10.2 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS tcm_ai_qa
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;
```

### 10.3 执行建表 SQL

按本文档中的数据库设计创建表，并插入测试数据。

### 10.4 修改配置

修改 `application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: your-mysql-password

deepseek:
  api-key: your-deepseek-api-key
```

### 10.5 启动项目

在 IDEA 中运行：

```text
TcmAiQaBackendApplication
```

启动成功后访问：

```http
GET http://localhost:8080/hello
```

## 11. 推荐测试顺序

### 11.1 测试用户模块

```http
GET /api/user/list
POST /api/user/register
POST /api/user/login
```

### 11.2 测试知识分类

```http
GET /api/category/list
```

### 11.3 测试知识条目

```http
GET /api/knowledge/page?pageNum=1&pageSize=10
```

### 11.4 切分知识片段

```http
POST /api/chunk/split/1
```

### 11.5 查看切分结果

```http
GET /api/chunk/item/1
```

### 11.6 测试关键词检索

```http
GET /api/chunk/search?keyword=补气
```

### 11.7 测试 AI 问答

```http
POST /api/chat/ask
```

请求体：

```json
{
  "question": "黄芪有什么功效？",
  "topK": 5
}
```

