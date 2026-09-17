package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreDTO {

    private Long id;

    private String name;
}