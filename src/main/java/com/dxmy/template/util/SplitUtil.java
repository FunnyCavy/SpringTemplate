package com.dxmy.template.util;

import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 字符串分割工具类
 */
@SuppressWarnings("unused")
public class SplitUtil {

    /**
     * 分割字符串为 List
     *
     * @param str       字符串
     * @param delimiter 分隔符
     * @return 按分隔符分割后的 List
     */
    @NonNull
    public static List<String> splitToList(String str, String delimiter) {
        if (str == null || str.isBlank())
            return Collections.emptyList();
        return Arrays.stream(str.split(delimiter))
                     .map(String::strip)
                     .toList();
    }

    /**
     * 分割字符串为 Set
     *
     * @param str       字符串
     * @param delimiter 分隔符
     * @return 按分隔符分割后的 Set
     */
    @NonNull
    public static Set<String> splitToSet(String str, String delimiter) {
        if (str == null || str.isBlank())
            return Collections.emptySet();
        return Arrays.stream(str.split(delimiter))
                     .map(String::strip)
                     .collect(Collectors.toSet());
    }

    /**
     * 分割字符串为 List, 并转变元素类型
     *
     * @param str       字符串
     * @param delimiter 分隔符
     * @param converter 类型转换器
     * @return 按分隔符分割且转变元素类型后的 List
     */
    @NonNull
    public static <T> List<T> splitToListAndConvert(String str, String delimiter, Function<String, T> converter) {
        if (str == null || str.isBlank())
            return Collections.emptyList();
        return Arrays.stream(str.split(delimiter))
                     .map(e -> converter.apply(e.strip()))
                     .toList();
    }

    /**
     * 分割字符串为 Set, 并转变元素类型
     *
     * @param str       字符串
     * @param delimiter 分隔符
     * @param converter 类型转换器
     * @return 按分隔符分割且转变元素类型后的 Set
     */
    @NonNull
    public static <T> Set<T> splitToSetAndConvert(String str, String delimiter, Function<String, T> converter) {
        if (str == null || str.isBlank())
            return Collections.emptySet();
        return Arrays.stream(str.split(delimiter))
                     .map(e -> converter.apply(e.strip()))
                     .collect(Collectors.toSet());
    }

    /**
     * 分割字符串为 Pair
     *
     * @param str       字符串
     * @param delimiter 分隔符
     * @return 按分隔符分割后的 Pair
     */
    @NonNull
    public static Pair<String, String> splitToPair(String str, String delimiter) {
        if (str == null || str.isBlank())
            throw new IllegalArgumentException("无法将空字符串分割成对");
        String[] split = str.split(delimiter);
        if (split.length != 2)
            throw new IllegalArgumentException("无法将字符串分割成对: " + str);
        return Pair.of(split[0].strip(), split[1].strip());
    }

}
