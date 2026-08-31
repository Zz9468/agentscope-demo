# AgentScope Java 2.0 学习项目

这是一个用于学习 [AgentScope Java 2.0](https://java.agentscope.io/v2/zh/docs/quickstart.html) 的示例项目。

项目按照课程逐步演示 Agent 的核心概念和实际用法。每一课都配有可运行的 Java 代码、详细 Markdown 讲义、动手实验和课后自测，适合边运行边理解 AgentScope 的设计。

## 技术栈

- Java 21
- Spring Boot 4.1.1
- AgentScope Java 2.0.1
- Project Reactor
- DashScope `qwen-max` 模型
- Maven Wrapper

主要依赖：

- `agentscope-harness`
- `agentscope-extensions-model-dashscope`

## 快速开始

### 1. 准备环境

请先安装：

- JDK 21
- IntelliJ IDEA
- 可访问 DashScope 的网络环境
- 有效的 DashScope API Key

### 2. 配置 API Key

在 IntelliJ IDEA 的 Run Configuration 中添加环境变量：

```text
DASHSCOPE_API_KEY=你的 API Key
```

API Key 只应保存在本地环境变量或 IDEA 的本地运行配置中，不要写入源码、README、配置文件或提交到 GitHub。

### 3. 编译和测试

在 Windows PowerShell 中执行：

```powershell
.\mvnw.cmd test
```

工具单元测试不需要 API Key；需要调用模型的课程运行时才需要配置 API Key。

### 4. 在 IDEA 中运行课程

建议直接打开对应 Java 文件，点击 `main` 方法左侧的绿色运行按钮。

如果复制已有运行配置，请修改：

- 主类（Main class）
- 运行配置名称

如果 API Key 配置在旧的运行配置中，复制配置可以避免重新填写；如果 API Key 配置在 Windows 系统环境变量中，则新配置通常可以直接继承。

## 学习路线

| 课程 | 主题 | 代码 | 讲义 | 状态 |
| --- | --- | --- | --- | --- |
| 第 1 课 | 第一个 `HarnessAgent`、消息、会话和工作区 | [`FirstAgentLesson.java`](src/main/java/com/example/agentscopedemo/lesson01/FirstAgentLesson.java) | [第一课讲义](docs/lessons/lesson-01-first-agent.md) | 已完成 |
| 第 2 课 | 使用 `streamEvents` 观察流式文本与事件 | [`StreamingEventsLesson.java`](src/main/java/com/example/agentscopedemo/lesson02/StreamingEventsLesson.java) | [第二课讲义](docs/lessons/lesson-02-stream-events.md) | 已完成 |
| 第 3 课 | 使用 `ReActAgent` 和 `@Tool` 编写工具 | [`ToolCallingLesson.java`](src/main/java/com/example/agentscopedemo/lesson03/ToolCallingLesson.java) | [第三课讲义](docs/lessons/lesson-03-react-tool.md) | 已完成 |
| 第 4 课 | 将 Agent 封装成 Spring Boot REST API | 待编写 | 待编写 | 计划中 |
| 第 5 课 | 多用户、多会话与状态隔离 | 待编写 | 待编写 | 计划中 |
| 第 6 课 | Workspace、Memory 和上下文压缩 | 待编写 | 待编写 | 计划中 |
| 第 7 课 | MCP 与 Skills | 待编写 | 待编写 | 计划中 |
| 第 8 课 | HITL、权限、子 Agent 与生产部署 | 待编写 | 待编写 | 计划中 |

完整学习目录见 [`LEARNING.md`](LEARNING.md)。

## 当前课程简介

### 第 1 课：第一个 Agent

学习 `HarnessAgent` 的基本构建方式，理解 `UserMessage`、`RuntimeContext`、用户 ID、会话 ID 和 Workspace 的作用。

### 第 2 课：流式事件

使用 `streamEvents()` 获取 `Flux<AgentEvent>`，观察 Agent 开始、文本增量、Agent 结束等事件，并从多个 `TextBlockDeltaEvent` 重建完整文本。

### 第 3 课：工具调用

使用 `@Tool` 和 `@ToolParam` 把普通 Java 方法注册到 `Toolkit`，再交给 `ReActAgent` 使用。课程会展示：

```text
模型生成 ToolCall
        ↓
Toolkit 查找工具
        ↓
Java 方法执行
        ↓
ToolResult 返回模型
        ↓
模型生成最终回答
```

第三课还包含不依赖模型的 `ShoppingToolsTest`，用于验证工具业务逻辑。

## 项目结构

```text
agentscope-demo/
├── docs/
│   ├── lessons/                 每课详细讲义
│   └── LESSON_TEMPLATE.md       讲义模板
├── src/
│   ├── main/java/
│   │   └── com/example/agentscopedemo/
│   │       ├── lesson01/        第 1 课代码
│   │       ├── lesson02/        第 2 课代码
│   │       └── lesson03/        第 3 课代码
│   └── test/java/               单元测试
├── LEARNING.md                  课程总目录和学习方法
├── pom.xml                      Maven 项目配置
└── mvnw.cmd                     Windows Maven Wrapper
```

`.agentscope/` 是运行时 Workspace 和会话数据目录，已加入 `.gitignore`，不会提交到 GitHub。

## 推荐学习方式

1. 先阅读对应讲义的学习目标和核心心智模型。
2. 不修改代码，先运行一次最小示例。
3. 对照讲义逐段阅读入口类。
4. 完成实验：先预测，再修改代码验证。
5. 完成课后自测。
6. 每完成一课，再提交一次 Git commit。

## Git 提交建议

第三课可以使用：

```text
feat(lesson-03): 完成 ReActAgent 与 @Tool 工具调用课程
```

提交前确认：

- 没有提交 API Key。
- `.agentscope/`、`target/` 等运行目录没有被加入暂存区。
- Maven 测试通过。

## 官方资料

- [AgentScope Java 2.0 中文快速开始](https://java.agentscope.io/v2/zh/docs/quickstart.html)
- [Agent 核心组件](https://java.agentscope.io/v2/zh/docs/building-blocks/agent.html)
- [消息与事件](https://java.agentscope.io/v2/zh/docs/building-blocks/message-and-event.html)
- [Tool](https://java.agentscope.io/v2/zh/docs/building-blocks/tool.html)
- [AgentScope Java 2.0 Release Notes](https://java.agentscope.io/v2/zh/docs/others/release-notes.html)

## 许可证

本项目用于个人学习和实验。AgentScope 的许可证及版权信息请以其官方仓库和发布版本说明为准。
