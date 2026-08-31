package com.example.agentscopedemo.lesson03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingToolsTest {

    private final ShoppingTools tools = new ShoppingTools();

    @Test
    void shouldCalculateKnownProductTotal() {
        String result = tools.calculateProductTotal("机械键盘", 3);

        assertEquals(
                "查询成功：商品=机械键盘，单价=399.00元，数量=3，总价=1197.00元。",
                result
        );
    }

    @Test
    void shouldRejectInvalidQuantity() {
        String result = tools.calculateProductTotal("机械键盘", 0);

        assertEquals("工具执行失败：购买数量必须是 1 到 100 之间的整数。", result);
    }

    @Test
    void shouldRejectUnknownProduct() {
        String result = tools.calculateProductTotal("笔记本电脑", 1);

        assertEquals(
                "工具执行失败：不支持该商品。可选商品：机械键盘、无线鼠标、显示器。",
                result
        );
    }
}
