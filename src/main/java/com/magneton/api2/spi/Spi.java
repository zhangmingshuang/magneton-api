package com.magneton.api2.spi;

/**
 * 自动加载SPI。 由META-INFO/service配置
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public interface Spi {

    String[] name();
}
