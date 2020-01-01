package com.zzp.es.suggest.demo.domian;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "news",type = "_doc")
public class News {
    private Integer id;

    private String title;

    private String url;

    private String content;

    private String tags;
}
