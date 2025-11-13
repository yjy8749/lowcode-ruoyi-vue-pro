package cn.iocoder.yudao.module.lowcode.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author leo
 */
@RequiredArgsConstructor
@Getter
public enum MaterialFileDataType implements ArrayValuable<Integer> {

    MAIN(0, "主数据"),

    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MaterialFileDataType::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
