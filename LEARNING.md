# AgentScope Java 2.0 学习记录

本项目按官方 2.0 文档逐课学习。每一课都先跑通最小示例，再观察运行结果，最后做一个小练习。

## 学习路线

1. 创建第一个 `HarnessAgent`，理解模型、消息、会话和工作区
2. 使用 `streamEvents` 观察流式文本与事件
3. 用 `ReActAgent` 和 `@Tool` 编写第一个工具
4. 把 Agent 封装成 Spring Boot REST API
5. 理解多用户、多会话状态隔离
6. 学习 Workspace、`AGENTS.md`、Memory 和上下文压缩
7. 接入 MCP 与 Skills
8. 学习权限确认（HITL）、子 Agent 与生产部署

## 第一课：两轮对话与会话记忆

入口类：`com.example.agentscopedemo.lesson01.FirstAgentLesson`

运行前，请在 PowerShell 当前窗口中设置 DashScope API Key：

```powershell
$env:DASHSCOPE_API_KEY = "你的 API Key"
```

然后在 IntelliJ IDEA 中直接运行 `FirstAgentLesson.main()`。

预期现象：

- 第一轮，模型回应你的自我介绍。
- 第二轮，模型能说出“小明”和“AgentScope Java 2.0”。
- 项目下出现 `.agentscope/workspace/`。
- 用户目录下出现 `.agentscope/state/learning-assistant/student/lesson-01/agent_state.json`。

### 阅读代码时重点观察

- `HarnessAgent.builder()`：组装一个带工作区、会话持久化等工程能力的 Agent。
- `.model("dashscope:qwen-plus")`：由 `ModelRegistry` 解析模型，并读取 `DASHSCOPE_API_KEY`。
- `UserMessage`：用户发给 Agent 的消息。
- `RuntimeContext`：一次调用的运行上下文；相同的 `userId + sessionId` 对应同一份会话状态。
- `.block()`：AgentScope 基于 Reactor；这里为了控制台入门示例，把异步结果阻塞为普通返回值。

### 小练习

把第一轮消息中的姓名和学习目标换成你自己的内容，再次运行。然后只修改 `sessionId`，观察第二轮是否还能记起第一轮。

## 官方资料

- 中文快速开始：https://java.agentscope.io/v2/zh/docs/quickstart.html
- Agent 核心组件：https://java.agentscope.io/v2/zh/docs/building-blocks/agent.html
- Release Notes：https://java.agentscope.io/v2/zh/docs/others/release-notes.html
