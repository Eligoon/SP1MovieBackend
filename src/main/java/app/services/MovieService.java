package app.services;

import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Genre;
import app.entities.Movie;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieService {

    private final MovieDAO movieDAO;
    private final GenreDAO genreDAO;

    public MovieService(MovieDAO movieDAO, GenreDAO genreDAO) {
        this.movieDAO = movieDAO;
        this.genreDAO = genreDAO;
    }

    public Movie saveMovie(MovieDTO dto) {

        Movie existingMovie = movieDAO.getByTmdbId(dto.getId());

        if (existingMovie != null) {
            return existingMovie;
        }

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

    public Movie getMovieById(Long id) {
        return movieDAO.getById(id);
    }

    public List<Movie> getAllMovies() {
        return movieDAO.getAll();
    }

    public Movie updateMovie(Movie movie) {
        return movieDAO.update(movie);
    }

    public boolean deleteMovie(Long id) {
        return movieDAO.delete(id);
    }

    private Set<Genre> convertGenres(List<GenreDTO> genreDTOs) {

        if (genreDTOs == null) {
            return Collections.emptySet();
        }

        return genreDTOs.stream()
                .map(this::findOrCreateGenre)
                .collect(Collectors.toSet());
    }

    private Genre findOrCreateGenre(GenreDTO dto) {

        Genre existingGenre = genreDAO.getByTmdbId(dto.getId());

        if (existingGenre != null) {
            return existingGenre;
        }

        Genre newGenre = Genre.builder()
                .tmdbId(dto.getId())
                .name(dto.getName())
                .build();

        return genreDAO.create(newGenre);
    }

    private LocalDate parseReleaseDate(String releaseDate) {

        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }

        return LocalDate.parse(releaseDate);
    }
}