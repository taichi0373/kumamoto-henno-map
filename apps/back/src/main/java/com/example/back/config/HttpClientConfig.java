package com.example.back.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.SimpleClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HttpClientConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ClientHttpRequestFactoryBuilder clientHttpRequestFactoryBuilder() {
        // Apache HttpClient5で問題が発生する場合はSimpleを使用
        try {
            // Apache HttpClient5が正常に利用可能かチェック
            Class.forName("org.apache.hc.client5.http.ssl.TlsSocketStrategy");
            // 利用可能な場合はデフォルトのHTTPコンポーネントを使用
            return ClientHttpRequestFactoryBuilder.httpComponents();
        } catch (ClassNotFoundException e) {
            // 利用不可能な場合はSimpleを使用
            return ClientHttpRequestFactoryBuilder.simple();
        }
    }
}