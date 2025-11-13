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
public enum DeployApiStatus implements ArrayValuable<Integer> {

    ONLINE(1, "已上线"),
    OFFLINE(2, "已下线"),

    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(DeployApiStatus::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static DeployApiStatus valueOf(int value) {
        return ArrayUtil.firstMatch(item -> item.getValue().equals(value), DeployApiStatus.values());
    }

}