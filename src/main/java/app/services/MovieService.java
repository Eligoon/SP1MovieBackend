package app.services;

import app.daos.MovieDAO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Genre;
import app.entities.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieService {

    private final MovieDAO movieDAO;

    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public Movie saveMovie(MovieDTO dto) {

        Movie movie = Movie.builder()
                .tmdbId(dto.getId())
                .title(dto.getTitle())
                .overview(dto.getOverview())
                .releaseDate(parseReleaseDate(dto.getReleaseDate()))
                .rating(dto.getRating())
                .genres(convertGenres(dto.getGenres()))
                .build();

        return movieDAO.create(movie);
    }

    private Set<Genre> convertGenres(List<GenreDTO> genres) {

        return genres.stream()
                .map(this::convertGenre)
                .collect(Collectors.toSet());
    }

    private Genre convertGenre(GenreDTO dto) {

        return Genre.builder()
                .tmdbId(dto.getId())
                .name(dto.getName())
                .build();
    }

    private LocalDate parseReleaseDate(String releaseDate) {

        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }

        return LocalDate.parse(releaseDate);
    }
}