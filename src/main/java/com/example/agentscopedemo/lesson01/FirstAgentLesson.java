package com.example.agentscopedemo.lesson01;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;

import java.nio.file.Paths;

/**
 * 第一课：创建 HarnessAgent，并用相同会话完成两轮对话。
 */
public class FirstAgentLesson {

    public static void main(String[] args) {
        requireEnvironmentVariable("DASHSCOPE_API_KEY");

        HarnessAgent agent = HarnessAgent.builder()
                .name("learning-assistant")
                .sysPrompt("你是一位耐心的 AgentScope Java 学习助手，请用简洁的中文回答。")
                .model("dashscope:qwen-plus")
                .workspace(Paths.get(".agentscope/workspace"))
                .compaction(CompactionConfig.builder()
                        .triggerMessages(30)
                        .keepMessages(10)
                        .build())
                .build();

        RuntimeContext context = RuntimeContext.builder()
                .userId("student")
                .sessionId("lesson-01")
                .build();

        Msg firstReply = agent.call(
                new UserMessage("我叫小明，正在学习 AgentScope Java 2.0。"),
                context
        ).block();
        printReply("第一轮", firstReply);

        Msg secondReply = agent.call(
                new UserMessage("我叫什么？正在学习什么？"),
                context
        ).block();
        printReply("第二轮", secondReply);
    }

    private static void printReply(String turn, Msg reply) {
        if (reply == null) {
            throw new IllegalStateException(turn + "没有返回消息");
        }
        System.out.println("\n[" + turn + "] " + reply.getTextContent());
    }

    private static void requireEnvironmentVariable(String name) {
        if (System.getenv(name) == null || System.getenv(name).isBlank()) {
            throw new IllegalStateException(
                    "缺少环境变量 " + name + "，请先配置 DashScope API Key 再运行本课。"
            );
        }
    }
}
