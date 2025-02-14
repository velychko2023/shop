package com.example.soccer_club_backend.controllers;

import com.example.soccer_club_backend.dtos.news.NewsDTO;
import com.example.soccer_club_backend.dtos.news.NewsGetAllDTO;
import com.example.soccer_club_backend.mapper.NewsMapper;
import com.example.soccer_club_backend.models.News;
import com.example.soccer_club_backend.service.FileStorageService;
import com.example.soccer_club_backend.service.NewsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:3000") // Разрешить запросы с указанного источника
@AllArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final FileStorageService fileStorageService;

    @GetMapping
    public List<NewsGetAllDTO> getAllNews() {
        return newsMapper.toNewsDTOList(newsService.getAllNews());
    }

    @GetMapping("/{newsId}")
    public NewsGetAllDTO getNewsById(@PathVariable int newsId) {
        return newsMapper.toNewsDTO(newsService.getNewsById(newsId));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<News> createNews(@RequestPart("file") MultipartFile file, @RequestPart("news") NewsDTO news) {
        News createdNews = newsService.createNews(news,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }

    @PutMapping(value = "/{newsId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public News updateNews(@PathVariable int newsId, @RequestBody @Valid NewsDTO updatedNews, @RequestPart("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        return newsService.updateNews(newsId, updatedNews, fileName);
    }

    @DeleteMapping("/{newsId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteNews(@PathVariable int newsId) {
        if (newsService.deleteNews(newsId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
