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
public enum DeployApiChannel implements ArrayValuable<Integer> {

    ADMIN(0, "管理后台"),
    CLIENT(1, "C端应用"),

    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(DeployApiChannel::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static DeployApiChannel valueOf(int value) {
        return ArrayUtil.firstMatch(item -> item.getValue().equals(value), DeployApiChannel.values());
    }

}