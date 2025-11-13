package cn.iocoder.yudao.module.lowcode.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author leo
 */
@RequiredArgsConstructor
@Getter
public enum MaterialFileStatus implements ArrayValuable<Integer> {

    DISABLED(0, "禁用"),
    NORMAL(1, "正常"),
    LOCKED(2, "锁定"),
    DEPRECATED(3, "弃用"),

    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MaterialFileStatus::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static MaterialFileStatus valueOf(int value) {
        return ArrayUtil.firstMatch(item -> item.getValue().equals(value), MaterialFileStatus.values());
    }

}