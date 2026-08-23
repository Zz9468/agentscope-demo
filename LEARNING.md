# AgentScope Java 2.0 学习课程

本项目按照 AgentScope Java 2.0 官方文档逐课学习。每一课都包含可运行代码和一份独立的详细讲义。

## 如何学习每一课

建议按下面的顺序进行：

1. 先阅读讲义中的“学习目标”和“核心心智模型”。
2. 不改代码，先运行一次官方思路对应的最小示例。
3. 对照“代码逐段解析”阅读入口类。
4. 完成讲义中的动手实验，先预测结果，再运行验证。
5. 回答课后自测题，确认自己不仅会复制代码，也理解运行机制。

## 课程文档标准

从第一课开始，每份讲义固定包含以下内容：

- 学习目标与前置知识
- 本课涉及的源码和依赖
- 核心概念与执行流程
- 代码逐段解析
- IDEA 配置与运行步骤
- 预期输出和运行期文件
- 动手实验
- 常见问题排查
- 课后自测与参考答案
- 官方资料和下一课预告

讲义模板见 [`docs/LESSON_TEMPLATE.md`](docs/LESSON_TEMPLATE.md)。

## 学习路线

| 课程 | 主题 | 代码 | 讲义 | 状态 |
| --- | --- | --- | --- | --- |
| 第 1 课 | 第一个 `HarnessAgent`、消息、会话和工作区 | [`FirstAgentLesson.java`](src/main/java/com/example/agentscopedemo/lesson01/FirstAgentLesson.java) | [第一课详细讲义](docs/lessons/lesson-01-first-agent.md) | 已完成 |
| 第 2 课 | 使用 `streamEvents` 观察流式文本与事件 | 待编写 | 待编写 | 未开始 |
| 第 3 课 | 使用 `ReActAgent` 和 `@Tool` 编写工具 | 待编写 | 待编写 | 未开始 |
| 第 4 课 | 将 Agent 封装成 Spring Boot REST API | 待编写 | 待编写 | 未开始 |
| 第 5 课 | 多用户、多会话与状态隔离 | 待编写 | 待编写 | 未开始 |
| 第 6 课 | Workspace、`AGENTS.md`、Memory 和上下文压缩 | 待编写 | 待编写 | 未开始 |
| 第 7 课 | MCP 与 Skills | 待编写 | 待编写 | 未开始 |
| 第 8 课 | HITL、权限、子 Agent 与生产部署 | 待编写 | 待编写 | 未开始 |

## 官方资料

- [AgentScope Java 2.0 中文快速开始](https://java.agentscope.io/v2/zh/docs/quickstart.html)
- [Agent 核心组件](https://java.agentscope.io/v2/zh/docs/building-blocks/agent.html)
- [AgentScope Java 2.0 Release Notes](https://java.agentscope.io/v2/zh/docs/others/release-notes.html)
