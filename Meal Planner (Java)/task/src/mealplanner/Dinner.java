package mealplanner;

import java.util.LinkedHashSet;

public class Dinner extends Meal{
    @Override
    public void setIngredients(String i) {
        super.setIngredients(i);
    }

    @Override
    public LinkedHashSet<String> getIngredients() {
        return super.getIngredients();
    }

    public Dinner(String name , String category)
    {
        super(name,category);
    }

    @Override
    public void addingredient() {
        super.addingredient();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getCategory() {
        return super.getCategory();
    }

    @Override
    public void print() {
        super.print();
    }
}
