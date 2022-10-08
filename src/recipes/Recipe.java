package recipes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({ "id" })
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String category;
    @NotBlank
    private String description;
    @Column(nullable = false)
    private LocalDateTime date;
    @NotEmpty
    @ElementCollection
    @CollectionTable(
            name = "INGREDIENTS",
            joinColumns=@JoinColumn(name = "id", referencedColumnName = "id")
    )
    private List<String> ingredients = new ArrayList<>();
    @NotEmpty
    @ElementCollection
    @CollectionTable(
            name = "DIRECTIONS",
            joinColumns=@JoinColumn(name = "id", referencedColumnName = "id")
    )
    private List<String> directions = new ArrayList<>();

    public Recipe(String name, String category, String description, List<String> ingredients, List<String> directions) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.date = LocalDateTime.now();
        this.ingredients = ingredients;
        this.directions = directions;
    }

    public Recipe() {

    }
}

