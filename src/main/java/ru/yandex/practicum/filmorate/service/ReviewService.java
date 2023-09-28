package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.dao.ReviewDao;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;

    @Autowired
    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    public Review addReview(Review review) {
        return reviewDao.saveReview(review);
    }

    public void addLikeToReview(int reviewId, int userId) {
        reviewDao.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(int reviewId, int userId) {
        reviewDao.addDislikeToReview(reviewId, userId);
    }

    public void deleteLikeToReview(int reviewId, int userId) {
        reviewDao.deleteLikeToReview(reviewId, userId);
    }

    public void deleteDislikeToReview(int reviewId, int userId) {
        reviewDao.deleteDislikeToReview(reviewId, userId);
    }

    public Review editReview(Review review) {
        return reviewDao.updateReview(review);
    }

    public void deleteReview(int id) {
        reviewDao.deleteReview(id);
    }

    public Review getReviewById(int id) {
        return reviewDao.getReviewById(id);
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        return reviewDao.getReviewsByFilmId(filmId, count);
    }

}
