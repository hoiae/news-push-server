package com.hoiae.new_push_server.repository;

import com.hoiae.new_push_server.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewRepository extends JpaRepository<News,String> {
}
