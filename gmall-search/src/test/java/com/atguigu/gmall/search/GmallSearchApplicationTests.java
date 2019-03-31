package com.atguigu.gmall.search;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.atguigu.gmall.search.bean.Account;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.AvgAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

	@Autowired
	private JestClient jestClient;

	//测试客户端的连接
	@Test
	public void contextLoads() {
		System.out.println(jestClient);
	}

	//测试增加与修改
	@Test
	public void index() throws IOException {

		Account account = new Account(99005L, 25000L, "Lian", "zhenjie", 26, "F", "mill road", "tong teacher", "lfy@atguigu.com", "BJ", "CP");

		Index index = new Index.Builder(account).index("bank")
				.type("account")
				.id(account.getAccount_number() + "")
				.build();
		String s = index.toString();
		System.out.println(s);

		DocumentResult result = jestClient.execute(index);
		System.out.println(result.isSucceeded()+"====>"+result.getJsonString());
	}

	//测试删除
	@Test
	public void delete() throws IOException {

		Delete delete = new Delete.Builder("99000").index("bank")
				.type("account")
				.build();
		DocumentResult result = jestClient.execute(delete);

		System.out.println(result.isSucceeded()+"====>"+result.getJsonString());
	}

	//测试查询--查询所有
	@Test
	public void search() throws IOException {

		Search search = new Search.Builder(null)
				.addIndex("bank")
				.addType("account")
				.build();

		SearchResult result = jestClient.execute(search);
		System.out.println(result.isSucceeded()+"====>"+result.getJsonString());
	}

	@Test
	public void srarchByDSL() throws IOException {

		MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(query);

		Search search = new Search.Builder("")
				.addIndex("bank")
				.addType("account")
				.build();
		SearchResult result = jestClient.execute(search);
		System.out.println(result.isSucceeded()+"====>"+result.getJsonString());
		Long total = result.getTotal();
		System.out.println(total);
	}

	@Test
	public void searchFuz() throws IOException {
		//1、构建queryBuilders
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		//2、构建两个must
		boolQuery.must(QueryBuilders.matchQuery("address","mill"))
				.must(QueryBuilders.matchQuery("gender","M"));

		//3构建一个must not
		boolQuery.mustNot(QueryBuilders.matchQuery("age",28));

		//4、构建一个should
		boolQuery.should(QueryBuilders.matchQuery("firstname","Parker"));


		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(boolQuery);

		//5、构建搜索Action
		Search search = new Search.Builder(sourceBuilder.toString())
				.addIndex("bank")
				.addType("account")
				.build();

		SearchResult result = jestClient.execute(search);
		System.out.println(result.getJsonString());
	}

	@Test
	public void aggs() throws IOException {

		//1、所有的条件都在这里封装
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//2、封装size
		searchSourceBuilder.size(1000);
		//3、封装term query
		searchSourceBuilder.query(buildQueryBuilder());
		//4、封装aggs
		searchSourceBuilder.aggregation(aggregation());

		String dsl = searchSourceBuilder.toString();
		System.out.println(dsl);

		Search search = new Search.Builder(dsl)
				.addIndex("bank")
				.addType("account")
				.build();
		SearchResult execute = jestClient.execute(search);
		System.out.println(execute.getTotal()+"==>"+execute.getErrorMessage());


		//从结果中获取值
		printResult(execute);

	}

	//打印结果
	private void printResult( SearchResult execute){

		//获取返回结果中的aggregations
		MetricAggregation aggregations = execute.getAggregations();

		//获取命中的记录
		SearchResult.Hit<Account, Void> hit = execute.getFirstHit(Account.class);
		//返回的真正查询到的数据
		Account source = hit.source;
		System.out.println(source);


		//聚合结果
		TermsAggregation age_agg = aggregations.getAggregation("age_agg", TermsAggregation.class);

		List<TermsAggregation.Entry> buckets = age_agg.getBuckets();
		buckets.forEach((b)->{
			System.out.println("年龄："+b.getKey()+"；总共有："+b.getCount());
			AvgAggregation balance_avg = b.getAvgAggregation("balance_avg");
			System.out.println("平均薪资"+balance_avg.getAvg());
			TermsAggregation gender_agg = b.getAggregation("gender_agg", TermsAggregation.class);
			gender_agg.getBuckets().forEach((b2)->{
				System.out.println("性别："+b2.getKey()+"；有："+b2.getCount()+"人；平均薪资："+b2.getAvgAggregation("balance_avg").getAvg());

			});
		});

		System.out.println(age_agg);

	}

	private QueryBuilder buildQueryBuilder(){
		TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("gender.keyword", "M", "F");
		return termsQuery;
	}

	private AggregationBuilder aggregation(){
		TermsAggregationBuilder age_agg = AggregationBuilders.terms("age_agg");

		age_agg.size(100).field("age");
		age_agg.subAggregation(genderAggregation());
		age_agg.subAggregation(blanceAvgAgg());

		return age_agg;
	}

	//子聚合分析
	private AggregationBuilder genderAggregation(){
		//gender_agg的子agg
		TermsAggregationBuilder gender_agg = AggregationBuilders.terms("gender_agg");
		gender_agg.field("gender.keyword").size(100);
		gender_agg.subAggregation(AggregationBuilders.avg("balance_avg").field("balance"));

		return gender_agg;
	}

	private AggregationBuilder blanceAvgAgg(){
		AvgAggregationBuilder balance_avg =
				AggregationBuilders.avg("balance_avg").field("balance");
		return balance_avg;
	}

}
