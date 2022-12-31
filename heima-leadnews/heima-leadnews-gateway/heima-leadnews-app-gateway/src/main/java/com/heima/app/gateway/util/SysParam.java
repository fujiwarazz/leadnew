//package com.heima.app.gateway.util;
//
//import com.heima.common.annotation.SysParam;
//import com.heima.model.common.enums.TypeEnum;
//
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//
//import java.lang.reflect.Field;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author peelsannaw
// * @create 13/11/2022 下午10:58
// */
//@Slf4j
//@Getter
//@SuppressWarnings({"FieldMayBeFinal", "unused"})
//public class SysParams {
//    private final Map<String, Object> OUTPUT_MAP = new HashMap<>();
//
//    /******
//     * 系统属性参数, 使用SysParam注解标记
//     * 类型只支持: String, LocalDateTime, Number(Integer, Double, Long, Short)
//     */
//
//    @SysParam(name = "系统项目名称", type = TypeEnum.STRING)
//    private String systemName = "XujcOJ";
//
//    @SysParam(name = "测试日期参数", type = TypeEnum.DATETIME)
//    private LocalDateTime systemDateTime = LocalDateTime.of(2023, 1, 1, 0, 0);
//
//    @SysParam(name = "测试数字参数", type = TypeEnum.NUMBER)
//    private Double systemDouble = 3.1415926d;
//
//    /**
//     * 更新单个参数值
//     *
//     * @param param 要更新的参数值
//     */
//    protected void loadOne(ParamEntity param) {
//        if (param != null) {
//            Field field = null;
//            try {
//                // 根据code在反射取到对应的变量
//                field = this.getClass().getDeclaredField(param.getCode());
//            } catch (NoSuchFieldException ignored) {
//            }
//
//            // 检查变量是否合法且是系统参数变量
//            if (field != null && include(field)) {
//                setField(field, param);
//            }
//        }
//    }
//
//    /**
//     * 从map中更新参数值
//     *
//     * @param map 参数字典
//     */
//    protected void loadByMap(Map<String, ParamEntity> map) {
//        if (map != null) {
//            // 挨个遍历
//            for (Map.Entry<String, ParamEntity> entry : map.entrySet()) {
//                loadOne(entry.getValue());
//            }
//        }
//    }
//
//    /**
//     * 设置具体单个变量
//     *
//     * @param field 被设置的变量
//     * @param val   要设置的值
//     */
//    private void setField(Field field, ParamEntity val) {
//        if (val != null && field!=null) {
//            // 获取修改前的正确值
//            Object defaultVal;
//            try {
//                defaultVal = field.get(this);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                // 根据注解上的类型解析变量, 目前仅支持有限类型, 详见TypeEnum
//                Object obj = db2sys(val.getValue(), field.getAnnotation(SysParam.class).type(), field.getType());
//                // 设置可访问, 该操作关闭了反射的安全类型检查, 可加速
//                field.setAccessible(true);
//                // 设置值
//                field.set(this, obj);
//                // 同步对外字典
//                OUTPUT_MAP.put(field.getName(), obj);
//            } catch (Exception e) {
//                log.error("SysParamsManager: reset field: '{}' to {} error", field.getName(), val.getValue());
//
//                // 如果新修改的值有错误, 就设置为原值
//                try {
//                    field.set(this, defaultVal);
//                    OUTPUT_MAP.put(field.getName(), defaultVal);
//                    log.error("SysParamsManager: reset field: '{}' to default {}", field.getName(), val.getValue());
//                } catch (IllegalAccessException ex) {
//                    throw new RuntimeException(ex);
//                }
//                throw new BusinessException(e, EmBusinessErr.UNKNOWN_ERROR);
//            }
//            try {
//                log.debug("SysParamsManager: set field: '{}' to {}", field.getName(), field.get(this));
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    /**
//     * 获取到代码中定义的系统参数和相关的默认值, 并包装成ParamEntity的map返回
//     * 用于系统初始化数据库中的系统参数
//     *
//     * @return Map\<Code, ParamEntity\>
//     */
//    public Map<String, ParamEntity> getDefined() {
//        Map<String, ParamEntity> map = new HashMap<>();
//        for (Field field : this.getClass().getDeclaredFields()) {
//            // 遍历被SysParam注解的属性
//            if (include(field)) {
//                ParamEntity param = new ParamEntity();
//                SysParam annotation = field.getAnnotation(SysParam.class);
//
//                // 设置代码中定义的信息
//                param.setName(annotation.name());
//                param.setDescription(annotation.description().isBlank() ? null : annotation.description());
//                param.setCode(field.getName());
//
//                // 设置代码中的默认值
//                try {
//                    Object val = field.get(this);
//                    String str = sys2db(val);
//                    param.setValue(str);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//
//                map.put(field.getName(), param);
//            }
//        }
//        return map;
//    }
//
//    /**
//     * 检查变量是否被SysParam注解 只有被注解的才是系统参数
//     */
//    private boolean include(Field field) {
//        return field.getAnnotation(SysParam.class) != null;
//    }
//
//
//    /**
//     * 将字符串转换为对应的类型
//     * 数据库中存储的都是String字符, 需要根据type枚举转换
//     *
//     * @param content     数据库的字符串值
//     * @param type        类型枚举
//     * @param numberClazz 数字具体类型
//     * @return 对应类型的值
//     */
//
//    private Object db2sys(String content, TypeEnum type, Class<?> numberClazz) {
//        // 如果是空或空串 都是null
//        if (content == null || content.isBlank()) {
//            return null;
//        }
//        // 目前仅支持 数字(四种数字类型), 日期(localDateTime, yyyy-MM-dd HH:mm:ss), 字符串
//        switch (type) {
//            case NUMBER:
//                if (Integer.class == numberClazz) {
//                    return Integer.parseInt(content);
//                } else if (Double.class == numberClazz) {
//                    return Double.parseDouble(content);
//                } else if (Short.class == numberClazz) {
//                    return Short.parseShort(content);
//                } else if (Long.class == numberClazz) {
//                    return Long.parseLong(content);
//                }
//                return null;
//            case DATETIME:
//                return LocalDateTime.parse(content, FormatterConstant.DATE_TIME_FORMATTER);
//            default:
//                return content;
//        }
//    }
//
//    private Object db2sys(String content, TypeEnum type) {
//        return db2sys(content, type, null);
//    }
//
//    /**
//     * 系统值转换数据库值(字符串)
//     *
//     * @param obj 值
//     * @return 值对应的字符串
//     */
//    private String sys2db(Object obj) {
//        // 如果是字符串
//        if (obj instanceof String) {
//            if (((String) obj).isBlank()) {
//                return null;
//            }
//            return obj.toString();
//        } else if (obj instanceof LocalDateTime) {
//            // 如果是日期, 返回格式化后的值 yyyy-MM-dd HH:mm:ss
//            return ((LocalDateTime) obj).format(FormatterConstant.DATE_TIME_FORMATTER);
//        } else if (obj instanceof Integer
//                || obj instanceof Double
//                || obj instanceof Short
//                || obj instanceof Long) {
//            // 如果是数字
//            return obj.toString();
//        } else {
//            throw new RuntimeException("不支持该类型参数");
//        }
//    }
//
//    public Map<String, Object> output() {
//        return OUTPUT_MAP;
//    }
//}
