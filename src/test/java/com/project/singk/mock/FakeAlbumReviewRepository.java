package com.project.singk.mock;

import com.project.singk.domain.member.domain.Gender;
import com.project.singk.domain.review.domain.AlbumReview;
import com.project.singk.domain.review.domain.AlbumReviewStatistics;
import com.project.singk.domain.review.service.port.AlbumReviewRepository;
import com.project.singk.global.api.ApiException;
import com.project.singk.global.api.AppHttpStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeAlbumReviewRepository implements AlbumReviewRepository {
	private final AtomicLong autoGeneratedId = new AtomicLong(0);
	private final List<AlbumReview> data = Collections.synchronizedList(new ArrayList<>());

    @Override
    public AlbumReview save(AlbumReview albumReview) {
        if (albumReview.getId() == null || albumReview.getId() == 0) {
            AlbumReview newAlbumReview = AlbumReview.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .content(albumReview.getContent())
                    .score(albumReview.getScore())
                    .prosCount(albumReview.getProsCount())
                    .consCount(albumReview.getConsCount())
                    .album(albumReview.getAlbum())
                    .writer(albumReview.getWriter())
                    .build();
            data.add(newAlbumReview);
            return newAlbumReview;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), albumReview.getId()));
            data.add(albumReview);
            return albumReview;
        }
    }

    @Override
    public AlbumReview getById(Long id) {
        return findById(id).orElseThrow(() -> new ApiException(AppHttpStatus.NOT_FOUND_ALBUM_REVIEW));
    }

    @Override
    public Optional<AlbumReview> findById(Long id) {
        return data.stream().filter(item -> item.getId().equals(id)).findAny();
    }

    @Override
    public AlbumReviewStatistics getAlbumReviewStatisticsByAlbumId(String albumId) {
        List<AlbumReview> albumReviews = data.stream().filter(item -> item.getAlbum().getId().equals(albumId)).toList();

        long totalReviewer = albumReviews.size();
        long totalScore = (long) albumReviews.stream().map(AlbumReview::getScore).reduce(0, Integer::sum);

        return AlbumReviewStatistics.builder()
                .totalReviewer(totalReviewer)
                .totalScore(totalScore)
                .averageScore((double) totalScore / totalReviewer)
                .score1Count(albumReviews.stream().filter(item -> item.getScore() == 1).count())
                .score2Count(albumReviews.stream().filter(item -> item.getScore() == 2).count())
                .score3Count(albumReviews.stream().filter(item -> item.getScore() == 3).count())
                .score4Count(albumReviews.stream().filter(item -> item.getScore() == 4).count())
                .score5Count(albumReviews.stream().filter(item -> item.getScore() == 5).count())
                .maleCount(albumReviews.stream().filter(item -> item.getWriter().getGender() == Gender.MALE).count())
                .femaleCount(albumReviews.stream().filter(item -> item.getWriter().getGender() == Gender.FEMALE).count())
                .build();
    }

}
