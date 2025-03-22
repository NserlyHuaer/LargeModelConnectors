package com.nserly.Tools.String;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 本类适用于格式化（格式{sth}{sth}）
 */
public class Formation {
    //第一个：sth集合  第二个：将其设置的值
    private HashMap<String, String> information;//名称
    private List<String> keys;
    public final String originalString;
    private final String directSuffix;
    private final String directPrefix;
    private final String escapedSuffix;

    private final String escapedPrefix;


    /**
     * 初始化，改变有效值格式为{sth}{sth}
     *
     * @param CastString 被转换字符串
     */
    public Formation(String CastString) {
        // 初始化
        originalString = CastString;
        directSuffix = "{";
        directPrefix = "}";
        escapedSuffix = Pattern.quote(directSuffix);
        escapedPrefix = Pattern.quote(directPrefix);
        information = new HashMap<>();
        keys = new ArrayList<>();
        load();
    }

    /**
     * 初始化，改变有效值格式
     *
     * @param directSuffix 前缀
     * @param directPrefix 后者
     * @param CastString   操作字符串
     */

    public Formation(String directSuffix, String directPrefix, String CastString) {
        // 初始化
        this.originalString = CastString;
        information = new HashMap<>();
        keys = new ArrayList<>();
        this.directPrefix = directPrefix;
        this.directSuffix = directSuffix;
        escapedSuffix = Pattern.quote(directSuffix);
        escapedPrefix = Pattern.quote(directPrefix);
        load();
    }

    private void load() {
        // 构建正则表达式
        var pattern = Pattern.compile(escapedSuffix + "(.*?)" + escapedPrefix);
        var matcher = pattern.matcher(originalString);
        while (matcher.find()) {
            String string = matcher.group();
            string = string.substring(directPrefix.length(), string.length() - directSuffix.length());
            information.put(string, "");
            keys.add(string);
        }
    }

    /**
     * 将数组转化为本类支持读取的字符串格式
     *
     * @param array 要转化的数组
     * @return 输出本类支持读取的字符串格式
     */
    public static String ArrayToString(String... array) {
        StringPro st = new StringPro();
        for (String i : array) {
            st.append("{" + i + "}");
        }
        return st.toString();
    }

    /**
     * 获取结果
     *
     * @return 获取结果，返回String对象
     */

    public String getProcessingString() {
        if (information == null || information.isEmpty()) return originalString;
        String result = originalString;
        String cache;
        for (String key : keys) {
            cache = information.get(key);
            if (cache != null && !cache.isEmpty()) {
                result = result.replaceAll(escapedSuffix + key + escapedPrefix, cache);
            }
        }
        return result;
    }

    /**
     * 重置
     */
    public void reset() {
        for (String string : keys) {
            information.remove(string);
            information.put(string, "");
        }
    }


    /**
     * 改变字符串
     *
     * @param revalued 改变其中文本（非{sth}格式）
     * @param value    改变它的值
     * @return
     */
    public void change(String revalued, String value) {//revalued文本,value改变值
        if (keys.contains(revalued)) {
            information.remove(revalued);
            information.put(revalued, value);
        }
    }

    public List<String> getArray() {
        return keys;
    }

    /**
     * @param c        修改某类的Class对象
     * @param variable 被改变类的变量名（区分大小写）
     * @param value    改变后的值
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void Revise(Class c, String variable, Object value) throws NoSuchFieldException, IllegalAccessException {//c为类，variable为被修改变量，value为修改的变量
        var f = c.getDeclaredField(variable);//获取属性列表
        f.setAccessible(true);//设置为可修改
        f.set(variable, value);//将变量对应名称里面的值设置为指定值
    }

}
