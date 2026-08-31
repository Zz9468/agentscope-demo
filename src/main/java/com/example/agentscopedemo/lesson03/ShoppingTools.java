package com.example.agentscopedemo.lesson03;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 第三课使用的演示工具。
 *
 * <p>这是一个普通 Java 类。AgentScope 通过反射发现带有 {@link Tool} 的方法，
 * 并把方法参数转换成提供给大模型的 JSON Schema。
 */
public class ShoppingTools {

    private static final Map<String, BigDecimal> UNIT_PRICES = Map.of(
            "机械键盘", new BigDecimal("399.00"),
            "无线鼠标", new BigDecimal("129.00"),
            "显示器", new BigDecimal("1599.00")
    );

    @Tool(
            name = "calculate_product_total",
            description = "查询演示商品的固定单价并计算购买总价。用户询问机械键盘、无线鼠标或显示器的购买总价时使用。",
            readOnly = true,
            concurrencySafe = true
    )
    public String calculateProductTotal(
            @ToolParam(
                    name = "productName",
                    description = "商品名称，只支持：机械键盘、无线鼠标、显示器"
            )
            String productName,
            @ToolParam(
                    name = "quantity",
                    description = "购买数量，必须是 1 到 100 之间的整数"
            )
            Integer quantity
    ) {
        System.out.printf(
                "%n[Java 工具真正执行] productName=%s, quantity=%s%n",
                productName,
                quantity
        );

        if (productName == null || productName.isBlank()) {
            return "工具执行失败：商品名称不能为空。";
        }
        if (quantity == null || quantity < 1 || quantity > 100) {
            return "工具执行失败：购买数量必须是 1 到 100 之间的整数。";
        }

        BigDecimal unitPrice = UNIT_PRICES.get(productName);
        if (unitPrice == null) {
            return "工具执行失败：不支持该商品。可选商品：机械键盘、无线鼠标、显示器。";
        }

        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return "查询成功：商品=" + productName
                + "，单价=" + unitPrice.toPlainString() + "元"
                + "，数量=" + quantity
                + "，总价=" + totalPrice.toPlainString() + "元。";
    }
}
