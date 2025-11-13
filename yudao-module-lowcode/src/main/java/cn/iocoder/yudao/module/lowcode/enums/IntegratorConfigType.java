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
public enum IntegratorConfigType implements ArrayValuable<Integer> {

    LOCAL(0, "本机"),
    REMOTE(1, "远程"),

    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(IntegratorConfigType::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static IntegratorConfigType valueOf(int value) {
        return ArrayUtil.firstMatch(item -> item.getValue().equals(value), IntegratorConfigType.values());
    }

}