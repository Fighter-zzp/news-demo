package com.zzp.es.suggest.demo.controller;

import com.zzp.es.suggest.demo.extractor.NewsResultsExtractor;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
public class NewsController {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 获取推荐词
     */
    @GetMapping("/suggest")
    public Object suggestSearch(String prefix){
        // 设置completion属性
        var tags = new CompletionSuggestionBuilder("tags")
            .prefix(prefix)
            .skipDuplicates(true);
        // 设置suggest体
        var suggestBuilder = new SuggestBuilder()
                .addSuggestion("tags_suggest",tags);

        var searchRequestBuilder = elasticsearchTemplate.getClient()
                .prepareSearch("news")//指定索引名
                .suggest(suggestBuilder)
                .setFetchSource(new String[]{"id"}, new String[]{});// 指定_source

        // 底层List使用的泛型
        var suggestList = searchRequestBuilder.get()
                .getSuggest()
                .getSuggestion("tags_suggest")
                .getEntries();

        // 设置结果接收为value：xxx的集合
        var results = new ArrayList<Map<String, String>>();
        suggestList.forEach(obj->{
            // 仅要Complete属性的推荐
            if (obj instanceof CompletionSuggestion.Entry){
                var entry = (CompletionSuggestion.Entry)obj;
                // 获取Options（集合），由options获取text对象
                entry.getOptions().forEach(option -> {
                    var suggestText = option.getText().toString();
                    var map = new HashMap<String, String>();
                    map.put("value",suggestText);
                    results.add(map);
                });
            }
        });
        return results;
    }

    /**
     * 由关键词查找，并且关键词高亮
     */
    @GetMapping("/search")
    public Object executeSearch(String text){
        var searchQueryBuilder = new NativeSearchQueryBuilder()
                .withIndices("news")
                .withSourceFilter(new FetchSourceFilter(new String[]{"id", "title", "url", "content"}, new String[]{}))
                .withQuery(new MultiMatchQueryBuilder(text,"title", "content"))
                .withHighlightBuilder(
                        new HighlightBuilder()
                        .preTags("<font color='red'>")
                        .postTags("</font>")
                        .field("title")
                        .field("content")
                ).build();
        return elasticsearchTemplate.query(searchQueryBuilder, new NewsResultsExtractor());
    }


}
