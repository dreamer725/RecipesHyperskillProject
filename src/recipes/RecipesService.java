package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RecipesService {
    private final RecipesRepository recipesRepository;

    private final Comparator<Recipe> byDate = Comparator.comparing(Recipe::getDate);


    @Autowired
    public RecipesService(RecipesRepository recipesRepository){
        this.recipesRepository = recipesRepository;
    }

    public Optional<Recipe> findRecipeById(Long id){
        return recipesRepository.findById(id);
    }

    public List<Recipe> findRecipeByCategory(String category){
        return recipesRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public List<Recipe> findRecipeByName(String name){
        return recipesRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

    public void saveRecipe(Recipe recipe){
        recipe.setDate(LocalDateTime.now());
        this.recipesRepository.save(recipe);
    }

    @Transactional
    public void updateRecipe(Long id,Recipe recipe) {
        Recipe toUpdate = this.recipesRepository.getById(id);
        toUpdate.setName(recipe.getName());
        toUpdate.setCategory(recipe.getCategory());
        toUpdate.setDescription(recipe.getDescription());
        toUpdate.setDate(LocalDateTime.now());
        toUpdate.setIngredients(recipe.getIngredients());
        toUpdate.setDirections(recipe.getDirections());
        this.recipesRepository.save(toUpdate);
    }

    public void deleteRecipeById(Long id){
        this.recipesRepository.deleteById(id);
    }
}
