/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mybatis.provider;

import io.mybatis.provider.keysql.GenId;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表对应的实体
 *
 * @author liuzh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Entity {
  /**
   * 对应实体类
   */
  Class<?> value();

  /**
   * 表名
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface Table {
    /**
     * 表名，默认空时使用对象名（不进行任何转换）
     */
    String value() default "";

    /**
     * 备注，仅用于在注解上展示，不用于任何其他处理
     */
    String remark() default "";

    /**
     * catalog 名称，配置后，会在表名前面加上 catalog 名称，规则为：catalog.schema.tableName，支持全局 mybatis.provider.catalog 配置
     */
    String catalog() default "";

    /**
     * schema 名称，配置后，会在表名前面加上 schema 名称，规则为：catalog.schema.tableName，支持全局 mybatis.provider.schema 配置
     */
    String schema() default "";

    /**
     * 名称规则、样式，同时应用于表名和列名，不想用于表名时，直接指定表名 {@link #value()}即可。
     * <p>
     * 2.0版本之前默认为 {@link Style#NORMAL}, 2.0版本之后默认使用 {@link Style#LOWER_UNDERSCORE}
     * <p>
     * 可以通过 {@link Style#DEFAULT_STYLE_KEY} = 格式 来修改默认值
     */
    String style() default "";

    /**
     * 使用指定的 <resultMap>
     */
    String resultMap() default "";

    /**
     * 自动根据字段生成 <resultMap>
     */
    boolean autoResultMap() default false;

    /**
     * 属性配置
     */
    Prop[] props() default {};

    /**
     * 排除指定父类的所有字段
     */
    Class<?>[] excludeSuperClasses() default {};

    /**
     * 排除指定类型的字段
     */
    Class<?>[] excludeFieldTypes() default {};

    /**
     * 排除指定字段名的字段
     */
    String[] excludeFields() default {};
  }

  /**
   * 属性配置，优先级高于 {@link io.mybatis.config.ConfigHelper } 提供的配置
   */
  @interface Prop {
    /**
     * 属性名
     */
    String name();

    /**
     * 属性值
     */
    String value();
  }

  /**
   * 排除列
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Transient {
  }

  /**
   * 列名
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Column {
    /**
     * 列名，默认空时使用字段名（不进行任何转换）
     */
    String value() default "";

    /**
     * 备注，仅用于在注解上展示，不用于任何其他处理
     */
    String remark() default "";

    /**
     * 标记字段是否为主键字段
     */
    boolean id() default false;

    /**
     * 标记字段是否可以为空
     */
    boolean nullable() default true;

    /**
     * 主键策略1，优先级1：是否使用 JDBC 方式获取主键，优先级最高，设置为 true 后，不对其他配置校验
     */
    boolean useGeneratedKeys() default false;

    /**
     * 主键策略2，优先级2：取主键的 SQL，当前SQL只能在 INSERT 语句执行后执行，如果想要在 INSERT 语句执行前执行，可以使用 {@link #genId()}
     */
    String afterSql() default "";

    /**
     * 主键策略3，优先级3：Java 方式生成主键，可以和发号器一类的服务配合使用
     */
    Class<? extends GenId> genId() default GenId.NULL.class;

    /**
     * 执行 genId 的时机，仅当 {@link #genId()} 不为空时有效，默认插入前执行
     */
    boolean genIdExecuteBefore() default true;

    /**
     * 排序方式，默认空时不作为排序字段，只有手动设置 ASC 和 DESC 才有效
     */
    String orderBy() default "";

    /**
     * 排序的优先级，多个排序字段时，根据该值确定顺序，数值越小优先级越高
     */
    int orderByPriority() default 0;

    /**
     * 可查询
     */
    boolean selectable() default true;

    /**
     * 可插入
     */
    boolean insertable() default true;

    /**
     * 可更新
     */
    boolean updatable() default true;

    /**
     * 数据库类型 {, jdbcType=VARCHAR}
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * 类型处理器 {, typeHandler=XXTypeHandler}
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    /**
     * 小数位数 {, numericScale=2}
     */
    String numericScale() default "";

    /**
     * 属性配置
     */
    Prop[] props() default {};
  }
}
