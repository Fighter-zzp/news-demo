package com.zzp.es.suggest.demo.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchTemplateConfig extends ElasticsearchConfigurationSupport {

    @Bean
    public Client elasticsearchClient() throws UnknownHostException {
        var settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        var client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        return client;
    }

    @Bean(name = {"elasticsearchOperations", "elasticsearchTemplate"})
    public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
        return new ElasticsearchTemplate(elasticsearchClient(),entityMapper());
    }

    @Bean
    @Override
    public EntityMapper entityMapper() {
        var entityMapper = new ElasticsearchEntityMapper(elasticsearchMappingContext(),
                new DefaultConversionService());
        entityMapper.setConversions(elasticsearchCustomConversions());
        return entityMapper;
    }
}
