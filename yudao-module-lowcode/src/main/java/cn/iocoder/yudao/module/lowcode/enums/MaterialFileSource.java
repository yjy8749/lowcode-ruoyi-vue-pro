package cn.iocoder.yudao.module.lowcode.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author leo
 */
@RequiredArgsConstructor
@Getter
public enum MaterialFileSource implements ArrayValuable<Integer> {

    NONE(0, "无"),
    QUERIER(1, "查询器"),
    DESIGNER(2, "设计器"),
    INTEGRATOR(3, "集成器"),


    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MaterialFileSource::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static Optional<MaterialFileSource> of(Integer value) {
        return Arrays.stream(MaterialFileSource.values()).filter(i -> i.getValue().equals(value)).findFirst();
    }

}
