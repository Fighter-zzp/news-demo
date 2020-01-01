package com.zzp.es.suggest.demo.extractor;

import com.alibaba.fastjson.JSONObject;
import com.zzp.es.suggest.demo.domian.News;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class NewsResultsExtractor implements ResultsExtractor<List<News>> {
    /**
     * 将高亮的title设置到 News的title中，
     * 将高亮的content设置到News的content中
     */
    @Override
    public List<News> extract(SearchResponse response) {
        var list = new ArrayList<News>();
        var hits = response.getHits().getHits();//获取具体的数据
        Stream.of(hits).forEach(hit->{
            var sourceAsJson = hit.getSourceAsString();//获取到News对象对应JSON字符串
            var news = JSONObject.parseObject(sourceAsJson, News.class);// 将json字符串转换为News类型的对象
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();// 获取高亮的数据

            // 获取title的高亮数据
            var titleField = highlightFields.get("title");
            // 在title中获取高亮碎片并拼接
            var fieldStringBuffer = new StringBuilder();
            if(null != titleField) {
                var texts = titleField.getFragments();  //获取高亮碎片
                for(var text : texts) {
                    fieldStringBuffer.append(text.toString());// 拼接
                }
                news.setTitle(fieldStringBuffer.toString());
            }
            // 获取content的高亮数据
            var contentField = highlightFields.get("content");
            // 在content中获取高亮碎片并拼接
            var contentSb = new StringBuilder();
            if(null != contentField) {
                var texts = contentField.getFragments();
                for(var text : texts) {
                    contentSb.append(text.toString());
                }
                news.setContent(contentSb.toString());
            }
            list.add(news);
        });
        return list;
    }
}
