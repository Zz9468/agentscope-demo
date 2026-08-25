package com.example.agentscopedemo.lesson02;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEndEvent;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.AgentStartEvent;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.event.ToolCallStartEvent;
import io.agentscope.core.event.ToolResultEndEvent;
import io.agentscope.core.message.UserMessage;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;

import java.nio.file.Paths;

/**
 * 第二课：使用 streamEvents 实时观察 Agent 的类型化事件流。
 *
 * <p>配套讲义：docs/lessons/lesson-02-stream-events.md
 */
public class StreamingEventsLesson {

    public static void main(String[] args) {
        requireEnvironmentVariable("DASHSCOPE_API_KEY");

        HarnessAgent agent = HarnessAgent.builder()
                .name("streaming-assistant")
                .sysPrompt("你是一位耐心的 AgentScope Java 学习助手，请用简洁的中文回答。")
                .model("dashscope:qwen-max")
                .workspace(Paths.get(".agentscope/workspace"))
                .compaction(CompactionConfig.builder()
                        .triggerMessages(30)
                        .keepMessages(10)
                        .build())
                .build();

        RuntimeContext context = RuntimeContext.builder()
                .userId("student")
                .sessionId("lesson-02")
                .build();

        UserMessage message = new UserMessage(
                "请用三点解释 Agent、Message 和 Event 的区别，每点尽量简短。"
        );
        EventPrinter eventPrinter = new EventPrinter();

        System.out.println("[客户端] 已发起请求，等待 Agent 事件……\n");

        agent.streamEvents(message, context)
                .doOnNext(eventPrinter::print)
                .doOnComplete(eventPrinter::printSummary)
                .doOnError(error -> System.err.println("\n[错误] " + error.getMessage()))
                .blockLast();
    }

    private static final class EventPrinter {

        private final StringBuilder accumulatedText = new StringBuilder();
        private int eventCount;

        private void print(AgentEvent event) {
            eventCount++;

            if (event instanceof AgentStartEvent start) {
                printEvent(start.getType(),
                        "sessionId=" + start.getSessionId() + ", replyId=" + start.getReplyId());
            } else if (event instanceof TextBlockDeltaEvent delta) {
                accumulatedText.append(delta.getDelta());
                System.out.print(delta.getDelta());
                System.out.flush();
            } else if (event instanceof ToolCallStartEvent toolCall) {
                printEvent(toolCall.getType(), "tool=" + toolCall.getToolCallName());
            } else if (event instanceof ToolResultEndEvent toolResult) {
                printEvent(toolResult.getType(), "state=" + toolResult.getState());
            } else if (event instanceof AgentEndEvent end) {
                printEvent(end.getType(), "replyId=" + end.getReplyId());
            } else if (!event.getType().name().endsWith("_DELTA")) {
                printEvent(event.getType(), null);
            }
        }

        private void printSummary() {
            System.out.println("\n[重建后的完整文本]\n" + accumulatedText);
            System.out.println("\n[统计] 共收到 " + eventCount + " 个事件。");
        }

        private void printEvent(AgentEventType eventType, String detail) {
            System.out.print("\n[事件 " + eventCount + "] " + eventType);
            if (detail != null && !detail.isBlank()) {
                System.out.print(" | " + detail);
            }
            System.out.println();
        }
    }

    private static void requireEnvironmentVariable(String name) {
        if (System.getenv(name) == null || System.getenv(name).isBlank()) {
            throw new IllegalStateException(
                    "缺少环境变量 " + name + "，请先配置 DashScope API Key 再运行本课。"
            );
        }
    }
}
