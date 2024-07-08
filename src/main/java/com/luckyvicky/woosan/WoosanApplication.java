package com.luckyvicky.woosan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.luckyvicky.woosan.domain.board.repository.jpa",
		"com.luckyvicky.woosan.domain.member.repository",
		"com.luckyvicky.woosan.domain.likes.repository",
		"com.luckyvicky.woosan.domain.report.repository",
		"com.luckyvicky.woosan.domain.messages.repository",
		"com.luckyvicky.woosan.domain.fileImg.repository" // MemberRepository가 있는 패키지 추가
})
@EnableElasticsearchRepositories(basePackages = "com.luckyvicky.woosan.domain.board.repository.elasticsearch")
public class WoosanApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoosanApplication.class, args);
		System.out.println("======================================================");
		System.out.println("es Test");
		System.out.println("======================================================");
	}
}
