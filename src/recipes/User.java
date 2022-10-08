package recipes;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    @Pattern(regexp=".+@.+\\..+", message="Email address is not valid.")
    @NotNull
    private String email;
    @NotBlank
    @Length(min = 8)
    private String password;
    @OneToMany
    private List<Recipe> recipes = new ArrayList<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(){}

    public void addRecipe(Recipe recipe){
        this.recipes.add(recipe);
    }

    public void deleteRecipe(Recipe recipe){
        this.recipes.remove(recipe);
    }
}
