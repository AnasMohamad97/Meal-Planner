package mealplanner;

import java.util.LinkedHashSet;

public class Breakfast extends Meal {

    public Breakfast(String name , String category)
    {
        super(name,category);
    }

    @Override
    public void setIngredients(String i) {
        super.setIngredients(i);
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
    public LinkedHashSet<String> getIngredients() {
        return super.getIngredients();
    }

    @Override
    public void addingredient() {
        super.addingredient();
    }

    @Override
    public void print() {
        super.print();
    }
}
