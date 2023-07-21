package mealplanner;

import java.util.LinkedHashSet;
import java.util.Scanner;

public class Meal   {
    private String name;
    private String category;
    private final LinkedHashSet<String> ingredients = new LinkedHashSet<>();
    static int  Ing_id = 0;
    public Meal(String name,String category){
        this.name = name;
        this.category = category;
    }
    public Meal(){

    }
    public void addingredient()
    {
        System.out.println("Input the ingredients:");
        Scanner scan = new Scanner(System.in);
        boolean valid = false;


        while( !valid) {

            //scan.nextLine();
            String line = scan.nextLine().trim();

            String[] ings = line.split("(, | ,|,)+");

            boolean ok = true;

            for (String i : ings) {
                //System.out.println(i);
                if (!i.matches("([a-zA-Z]+\\s*){1,}")){
                    ok = false;
                    break;
                } else {
                    ingredients.add(i);
                }
            }

            if(!ok){
                ingredients.clear();
                System.out.println("Wrong format. Use letters only!");
            }else{
                valid = true;
            }

        }
        System.out.println("The meal has been added!");
       // ingredients.forEach(elem -> System.out.println(elem+" "));
    }
    public void print() {
       // System.out.println("Category: " + category);

        System.out.println("Name: " + name);

        System.out.println("Ingredients: ");
        ingredients.forEach(System.out::println);
        System.out.println("");



    }
    public void setIngredients(String i)
    {
        ingredients.add(i);
    }
    public LinkedHashSet<String> getIngredients()
    {
        return ingredients;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
    public static void nextId() {
        Ing_id++;
    }

}
