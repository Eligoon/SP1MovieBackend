package app.services;

import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.ActorDTO;
import app.dtos.CreditsDTO;
import app.dtos.CrewMemberDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
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
    private final ActorDAO actorDAO;
    private final DirectorDAO directorDAO;

    public MovieService(
            MovieDAO movieDAO,
            GenreDAO genreDAO,
            ActorDAO actorDAO,
            DirectorDAO directorDAO
    ) {
        this.movieDAO = movieDAO;
        this.genreDAO = genreDAO;
        this.actorDAO = actorDAO;
        this.directorDAO = directorDAO;
    }

    public Movie saveMovie(MovieDTO movieDTO, CreditsDTO creditsDTO) {

        Movie existingMovie = movieDAO.getByTmdbId(movieDTO.getId());

        if (existingMovie != null) {
            System.out.println("Movie already exists - skipping save");
            return existingMovie;
        }

        Movie movie = Movie.builder()
                .tmdbId(movieDTO.getId())
                .title(movieDTO.getTitle())
                .overview(movieDTO.getOverview())
                .releaseDate(parseReleaseDate(movieDTO.getReleaseDate()))
                .rating(movieDTO.getRating())
                .genres(convertGenres(movieDTO.getGenres()))
                .actors(convertActors(creditsDTO))
                .director(convertDirector(creditsDTO))
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

    private Set<Actor> convertActors(CreditsDTO creditsDTO) {

        if (creditsDTO == null || creditsDTO.getCast() == null) {
            return Collections.emptySet();
        }

        return creditsDTO.getCast().stream()
                .map(this::findOrCreateActor)
                .collect(Collectors.toSet());
    }

    private Actor findOrCreateActor(ActorDTO dto) {

        Actor existingActor = actorDAO.getByTmdbId(dto.getId());

        if (existingActor != null) {
            return existingActor;
        }

        Actor newActor = Actor.builder()
                .tmdbId(dto.getId())
                .name(dto.getName())
                .build();

        return actorDAO.create(newActor);
    }

    private Director convertDirector(CreditsDTO creditsDTO) {

        if (creditsDTO == null || creditsDTO.getCrew() == null) {
            return null;
        }

        return creditsDTO.getCrew().stream()
                .filter(crewMember ->
                        "Director".equalsIgnoreCase(crewMember.getJob())
                )
                .map(this::findOrCreateDirector)
                .findFirst()
                .orElse(null);
    }

    private Director findOrCreateDirector(CrewMemberDTO dto) {

        Director existingDirector =
                directorDAO.getByTmdbId(dto.getId());

        if (existingDirector != null) {
            return existingDirector;
        }

        Director newDirector = Director.builder()
                .tmdbId(dto.getId())
                .name(dto.getName())
                .build();

        return directorDAO.create(newDirector);
    }

    private LocalDate parseReleaseDate(String releaseDate) {

        if (releaseDate == null || releaseDate.isBlank()) {
            return null;
        }

        return LocalDate.parse(releaseDate);
    }

    public List<Movie> searchMoviesByTitle(String searchString) {
        return movieDAO.searchByTitle(searchString);
    }

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieDAO.getMoviesByGenre(genreId);
    }

    public List<Genre> getAllGenres() {
        return genreDAO.getAll();
    }

    public List<Actor> getAllActors() {
        return actorDAO.getAll();
    }

    public List<Director> getAllDirectors() {
        return directorDAO.getAll();
    }
}