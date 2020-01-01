<template>
    <el-container>
        <el-header>
            <el-row>
                <el-col :span="7">
                    <el-autocomplete
                            v-model="searchText" style="width: 98%;"
                            :trigger-on-focus="false"
                            :fetch-suggestions="querySearch"
                            :select-when-unmatched="true"
                            @select="executeSearch"
                            placeholder="请输入内容"
                            clearable>
                        <template slot-scope="{ item }">
                            <div class="name">{{ item.value }}</div>
                            <span class="addr">{{ item.address }}</span>
                        </template>
                    </el-autocomplete>
                </el-col>
                <el-col :span="4">
                    <el-button type="danger" @click="executeSearch">搜索一下</el-button>
                </el-col>
            </el-row>
        </el-header>
        <el-divider/>
        <el-main>
            <el-card class="box-card" v-for="text in searchResults" :key="text.id">
                <div slot="header" class="clearfix">
                    <a :href="text.url" v-html="text.title" target="_blank"/>
                </div>
                <div class="text item" v-html="text.content"/>
            </el-card>
        </el-main>
    </el-container>
</template>

<script>

    export default {
        data() {
            return {
                searchText: '',
                searchResults: []
            };
        },
        methods: {
            querySearch(queryString, cb) {
                this.axios.get("http://localhost:8088/news/suggest?prefix=" + queryString)
                .then(resp=>{
                    cb(resp.data);
                });
            },
            executeSearch() {
                this.axios.get("http://localhost:8088/news//search?text=" + this.searchText)
                .then(resp=>{
                    this.searchResults=resp.data;
                })
            }
        }
    }
</script>

<style>
.el-card{
    margin-top: 2%;
}
</style>
