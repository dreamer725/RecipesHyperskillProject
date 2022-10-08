package recipes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RecipesController {
    final RecipesService recipesService;
    final UserRepository userRepository;
    final PasswordEncoder encoder;

    public RecipesController(RecipesService recipesService, UserRepository userRepository, PasswordEncoder encoder) {
        this.recipesService = recipesService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping("/api/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("User with email = " + user.getEmail() + "has been registered.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User with email = " + user.getEmail() + "already exists.");
    }

    @PostMapping("/api/recipe/new")
    public Map<String, Long> postRecipe(@Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails details) {
        userRepository.findByEmail(details.getUsername()).addRecipe(recipe);
        recipesService.saveRecipe(recipe);
        return Map.of("id", recipe.getId());
    }

    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable Long id, @Valid @RequestBody Recipe recipe, @AuthenticationPrincipal UserDetails details) {
        if (recipesService.findRecipeById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There is no recipe with such id.");
        }
        if(!userRepository.findByEmail(details.getUsername()).getRecipes().contains(recipesService.findRecipeById(id).get())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Recipe can be updated only by its author.");
        }

        recipesService.updateRecipe(id, recipe);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Recipe with id = " + id + "has been updated.");
    }

    @GetMapping("/api/recipe/{id}")
    public Optional<Recipe> getRecipe(@PathVariable Long id) {
        if (recipesService.findRecipeById(id).isPresent()) {
            return recipesService.findRecipeById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no recipe with such id.");
        }
    }

    @GetMapping("/api/recipe/search")
    public List<Recipe> getRecipe(@Size(min = 1, max = 1) @RequestParam Map<String, String> query) {
        if (query.containsKey("category")) {
            return recipesService.findRecipeByCategory(query.get("category"));
        } else if (query.containsKey("name")) {
            return recipesService.findRecipeByName(query.get("name"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no recipe with such id.");
        }
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id, @AuthenticationPrincipal UserDetails details) {
        if (recipesService.findRecipeById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There is no recipe with such id.");
        }
        if(!userRepository.findByEmail(details.getUsername()).getRecipes().contains(recipesService.findRecipeById(id).get())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Recipe can be deleted only by its author.");
        }

        userRepository.findByEmail(details.getUsername()).deleteRecipe(recipesService.findRecipeById(id).get());
        recipesService.deleteRecipeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Recipe with id = " + id + "has been deleted.");
    }
}
