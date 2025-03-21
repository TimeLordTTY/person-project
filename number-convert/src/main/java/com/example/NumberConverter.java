package com.example;

public class NumberConverter {
    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] UNITS = {"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿"};
    private static final String[] DECIMAL_UNITS = {"角", "分"};

    public static String convert(double number) {
        // 处理负数
        if (number < 0) {
            return "负" + convert(-number);
        }

        // 处理零
        if (number == 0) {
            return "零元整";
        }

        // 将数字转换为字符串，并分离整数和小数部分
        String numStr = String.format("%.2f", number);
        String[] parts = numStr.split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? parts[1] : "00";

        StringBuilder result = new StringBuilder();

        // 处理整数部分
        result.append(convertInteger(integerPart)).append("元");

        // 处理小数部分
        String decimal = convertDecimal(decimalPart);
        if (decimal.isEmpty()) {
            result.append("整");
        } else {
            result.append(decimal);
        }

        // 处理特殊情况
        String finalResult = result.toString()
            .replace("零零", "零")
            .replace("零元", "元")
            .replace("零角", "")
            .replace("零分", "");

        // 如果以"元"结尾，添加"整"
        if (finalResult.endsWith("元")) {
            finalResult += "整";
        }

        return finalResult;
    }

    private static String convertInteger(String number) {
        StringBuilder result = new StringBuilder();
        int length = number.length();

        for (int i = 0; i < length; i++) {
            int digit = number.charAt(i) - '0';
            int position = length - 1 - i;

            if (digit != 0) {
                result.append(NUMBERS[digit]).append(UNITS[position % 4]);
                if (position % 4 == 0 && position >= 4) {
                    result.append(position >= 8 ? "亿" : "万");
                }
            } else {
                if (position % 4 == 0 && position >= 4) {
                    if (!result.toString().endsWith("万") && !result.toString().endsWith("亿")) {
                        result.append(position >= 8 ? "亿" : "万");
                    }
                } else if (!result.toString().endsWith("零") && i != length - 1) {
                    result.append("零");
                }
            }
        }

        return result.toString();
    }

    private static String convertDecimal(String decimal) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(decimal.length(), 2); i++) {
            int digit = decimal.charAt(i) - '0';
            if (digit != 0) {
                result.append(NUMBERS[digit]).append(DECIMAL_UNITS[i]);
            }
        }
        return result.toString();
    }
} 