package com.hoiae.news_push_server.repository;

import com.hoiae.news_push_server.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News,String> {
}
