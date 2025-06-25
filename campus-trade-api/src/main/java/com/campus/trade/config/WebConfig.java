package com.campus.trade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 的额外配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射。
     * 这是让图片能够通过URL被访问到的关键。
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 【最终修正】当浏览器请求 /uploads/ 开头的URL时，
        // Spring Boot会去项目的运行根目录下的 /uploads/ 文件夹中查找文件。
        // "file:uploads/" 这种写法是健壮且跨平台的。
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
