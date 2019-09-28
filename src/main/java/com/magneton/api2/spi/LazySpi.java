package com.magneton.api2.spi;

/**
 * 懒加载的SPI，由代码注册
 *
 * @author zhangmingshuang
 * @since 2019/9/25
 */
public interface LazySpi {
    String name();
}
