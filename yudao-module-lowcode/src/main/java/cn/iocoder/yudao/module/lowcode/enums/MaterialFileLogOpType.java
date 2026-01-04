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
public enum MaterialFileLogOpType implements ArrayValuable<Integer> {

    CREATE(0, "创建"),
    UPDATE(1, "修改"),
    DELETE(2, "删除"),
    COPY(3, "复制"),

    TRANSFER(10, "转移所有权"),
    MOVE(11, "移动位置"),

    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MaterialFileLogOpType::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}