package com.example.agentscopedemo.lesson03;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEndEvent;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.AgentStartEvent;
import io.agentscope.core.event.ModelCallStartEvent;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.event.ToolCallDeltaEvent;
import io.agentscope.core.event.ToolCallEndEvent;
import io.agentscope.core.event.ToolCallStartEvent;
import io.agentscope.core.event.ToolResultEndEvent;
import io.agentscope.core.event.ToolResultStartEvent;
import io.agentscope.core.event.ToolResultTextDeltaEvent;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.dashscope.formatter.DashScopeChatFormatter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 第三课：给 ReActAgent 注册一个注解式 Java 工具，并观察工具调用事件。
 *
 * <p>配套讲义：docs/lessons/lesson-03-react-tool.md
 */
public class ToolCallingLesson {

    public static void main(String[] args) {
        String apiKey = requireEnvironmentVariable("DASHSCOPE_API_KEY");

        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new ShoppingTools());
        printRegisteredTools(toolkit);

        DashScopeChatModel model = DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-max")
                .stream(true)
                .enableThinking(false)
                .formatter(new DashScopeChatFormatter())
                .build();

        RuntimeContext context = RuntimeContext.builder()
                .userId("student")
                .sessionId("lesson-03")
                .build();

        UserMessage message = new UserMessage(
                "我想买 3 个机械键盘。请必须调用工具查询演示价格并计算总价，然后告诉我单价、数量和总价。"
        );

        try (ReActAgent agent = ReActAgent.builder()
                .name("shopping-assistant")
                .sysPrompt("""
                        你是购物计算助手。
                        当用户询问商品购买总价时，必须调用 calculate_product_total，不能自己猜测价格或心算。
                        工具成功后，根据工具结果用中文回答；工具失败时如实说明，不得编造数据。
                        """)
                .model(model)
                .toolkit(toolkit)
                .maxIters(5)
                .build()) {

            ToolEventPrinter eventPrinter = new ToolEventPrinter();
            System.out.println("\n[客户端] 已发起请求，等待 ReActAgent 决定是否调用工具……");

            agent.streamEvents(message, context)
                    .doOnNext(eventPrinter::print)
                    .doOnComplete(eventPrinter::printSummary)
                    .doOnError(error -> System.err.println("\n[错误] " + error.getMessage()))
                    .blockLast();
        }
    }

    private static void printRegisteredTools(Toolkit toolkit) {
        System.out.println("[Toolkit] 已注册工具及其 JSON Schema：");
        for (ToolSchema schema : toolkit.getToolSchemas()) {
            System.out.println("- name: " + schema.getName());
            System.out.println("  description: " + schema.getDescription());
            System.out.println("  parameters: " + schema.getParameters());
        }
    }

    private static final class ToolEventPrinter {

        private final Map<String, StringBuilder> argumentsByToolCall = new LinkedHashMap<>();
        private final Map<String, StringBuilder> resultsByToolCall = new LinkedHashMap<>();
        private final StringBuilder assistantText = new StringBuilder();
        private int eventCount;
        private int modelCallCount;
        private int toolCallCount;
        private boolean assistantLabelPrinted;

        private void print(AgentEvent event) {
            eventCount++;

            if (event instanceof AgentStartEvent start) {
                printEvent(start.getType(),
                        "sessionId=" + start.getSessionId() + ", replyId=" + start.getReplyId());
            } else if (event instanceof ModelCallStartEvent) {
                modelCallCount++;
                printEvent(event.getType(), "第 " + modelCallCount + " 次模型调用");
            } else if (event instanceof ToolCallStartEvent toolCall) {
                toolCallCount++;
                argumentsByToolCall.put(toolCall.getToolCallId(), new StringBuilder());
                printEvent(toolCall.getType(),
                        "tool=" + toolCall.getToolCallName()
                                + ", toolCallId=" + toolCall.getToolCallId());
                System.out.print("[工具参数流] ");
            } else if (event instanceof ToolCallDeltaEvent delta) {
                argumentsByToolCall
                        .computeIfAbsent(delta.getToolCallId(), ignored -> new StringBuilder())
                        .append(delta.getDelta());
                System.out.print(delta.getDelta());
                System.out.flush();
            } else if (event instanceof ToolCallEndEvent toolCall) {
                String arguments = argumentsByToolCall
                        .getOrDefault(toolCall.getToolCallId(), new StringBuilder())
                        .toString();
                System.out.println("\n[完整工具参数] " + arguments);
                printEvent(toolCall.getType(), "toolCallId=" + toolCall.getToolCallId());
            } else if (event instanceof ToolResultStartEvent toolResult) {
                resultsByToolCall.put(toolResult.getToolCallId(), new StringBuilder());
                printEvent(toolResult.getType(), "toolCallId=" + toolResult.getToolCallId());
                System.out.print("[工具结果流] ");
            } else if (event instanceof ToolResultTextDeltaEvent delta) {
                resultsByToolCall
                        .computeIfAbsent(delta.getToolCallId(), ignored -> new StringBuilder())
                        .append(delta.getDelta());
                System.out.print(delta.getDelta());
                System.out.flush();
            } else if (event instanceof ToolResultEndEvent toolResult) {
                System.out.println();
                printEvent(toolResult.getType(),
                        "toolCallId=" + toolResult.getToolCallId()
                                + ", state=" + toolResult.getState());
            } else if (event instanceof TextBlockDeltaEvent delta) {
                if (!assistantLabelPrinted) {
                    System.out.print("\n[Agent 回答] ");
                    assistantLabelPrinted = true;
                }
                assistantText.append(delta.getDelta());
                System.out.print(delta.getDelta());
                System.out.flush();
            } else if (event instanceof AgentEndEvent end) {
                printEvent(end.getType(), "replyId=" + end.getReplyId());
            } else if (!event.getType().name().endsWith("_DELTA")) {
                printEvent(event.getType(), null);
            }
        }

        private void printSummary() {
            System.out.println("\n\n[重建后的 Agent 文本]\n" + assistantText);
            System.out.println("\n[统计] 模型调用=" + modelCallCount
                    + "，工具调用=" + toolCallCount
                    + "，事件总数=" + eventCount + "。");
        }

        private void printEvent(AgentEventType eventType, String detail) {
            System.out.print("\n[事件 " + eventCount + "] " + eventType);
            if (detail != null && !detail.isBlank()) {
                System.out.print(" | " + detail);
            }
            System.out.println();
        }
    }

    private static String requireEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "缺少环境变量 " + name + "，请先配置 DashScope API Key 再运行本课。"
            );
        }
        return value;
    }
}
