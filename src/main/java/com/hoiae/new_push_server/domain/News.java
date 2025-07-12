package com.hoiae.new_push_server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "translated_news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {
    @Id
    private String id;

    private String title;

    private String content;

    private LocalDateTime publishedAt;

    protected News(String id, String title, String content, LocalDateTime publishedAt){
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
    }
}
