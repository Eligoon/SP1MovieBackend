package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String overview;

    private LocalDate releaseDate;

    private Double rating;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @Builder.Default
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;
}
