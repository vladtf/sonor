package com.pweb.backend.controllers;

import com.pweb.backend.services.NewsService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    @Secured("ROLE_USER")
    public List<NewsService.NewsResponse> getNews() {
        return newsService.getNews();
    }
}
