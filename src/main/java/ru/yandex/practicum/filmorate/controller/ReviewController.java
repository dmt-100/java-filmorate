package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.addLikeToReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.addDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.deleteLikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable("id") int reviewId, @PathVariable("userId") int userId) {
        reviewService.deleteDislikeToReview(reviewId, userId);
    }

    @PostMapping
    public Review addReview(@RequestBody @Valid Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review editReview(@RequestBody @Valid Review review) {
        return reviewService.editReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getReviewsByFilmAndCount(
            @RequestParam(name = "filmId", required = false) Long filmId,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }


}
