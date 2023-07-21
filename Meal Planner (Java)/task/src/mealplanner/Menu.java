package mealplanner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class Menu {

    public static LinkedHashSet<Meal> meals = new LinkedHashSet<>(); // linkedHashSet -> to reserve the order
    public static HashMap<String,Meal> HashMeal = new HashMap<>();
    public static HashMap<String,Integer> mealId = new HashMap<>();
    public static HashSet<Integer>  s = new HashSet<>();


    static int last_id;
    static Scanner scan = new Scanner(System.in);

    String DB_URL = "jdbc:postgresql:meals_db";
    String USER = "postgres";
    String PASS = "1111";
    Connection connection ;
    Statement statement ;

    public Menu() throws SQLException, ClassNotFoundException {
        
       this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
       this.statement = connection.createStatement();
        statement.executeUpdate("CREATE table IF NOT EXISTS meals (" +
                "meal_id integer," +
                "category varchar(50)," +
                "meal varchar(60)"+
                ")");
        statement.executeUpdate("CREATE table IF NOT EXISTS ingredients (" +
                "meal_id integer," +
                "ingredient varchar(50)," +
                "ingredient_id integer"+
                ")");
        statement.executeUpdate("CREATE table IF NOT EXISTS plan (" +
                "meal_id integer," +
                "category varchar(50)," +
                "meal varchar(60),"+
                "weekDay varchar(15)"+
                ")");

    }
    public void loadDataBase() throws SQLException {

        ResultSet rs = statement.executeQuery("select * from meals");

        while (rs.next()) {

            String type =  rs.getString("category");
            String name =  rs.getString("meal");
            last_id = rs.getInt("meal_id");
            Meal m = new Meal(name,type);
            meals.add(m);
            HashMeal.put(name,m);
            mealId.put(name,last_id);
        }
        List<Meal>tmp = new ArrayList<>(meals);
        int ind = 1;

        while (ind <= tmp.size()) {
            rs = statement.executeQuery("select * from ingredients where meal_id = " + Integer.toString(ind));
            while (rs.next()) {
                String s = rs.getString("ingredient");
                tmp.get(ind-1).setIngredients(s);
                HashMeal.get(tmp.get(ind-1).getName()).setIngredients(s);
            }ind++;
        }


    }
    public LinkedList<Meal> LoadType(String type) throws SQLException {

        LinkedList<Meal> res = new LinkedList<>();
        ResultSet rs = statement.executeQuery("select * from meals where category = " +type);

        Map<Integer,Meal> m= new LinkedHashMap<>();

        while (rs.next()) {
            String t = rs.getString("category");
            String name = rs.getString("meal");
            int meal_id = rs.getInt("meal_id");
            Meal meal = new Meal(name,t);
            m.put(meal_id,meal);

        }

        for (Map.Entry<Integer , Meal> entry : m.entrySet()) {
            rs = statement.executeQuery("select ingredient from ingredients where meal_id = "
                    + Integer.toString(entry.getKey()));
            while (rs.next())
            {
                String ing = rs.getString("ingredient");
                entry.getValue().setIngredients(ing);
            }
            res.add(entry.getValue());
        }

        return res;
    }
    public void SaveToDataBase(Meal meal) throws SQLException {
        statement.executeUpdate("insert into meals (category, meal,meal_id) values ("+ String.format("'%s'",meal.getCategory())+", "+String.format("'%s'",meal.getName())+", "+Integer.toString(++last_id) + ")");

        LinkedHashSet<String> s = meal.getIngredients();
        for (String i : s) {
            statement.executeUpdate("insert into ingredients (ingredient, ingredient_id,meal_id) values(" + String.format("'%s'",i) + ", "+Integer.toString(Meal.Ing_id)
                                    +", "+ Integer.toString(last_id) +")" );
            Meal.nextId();
        }


    }
   private static String ReadName() // utilities
    {
        System.out.println("Input the meal's name:");
        String name  = scan.nextLine();
        while(!name.matches("[a-zA-Z ]+") ) {
            System.out.println("Wrong format. Use letters only!");
            name = scan.nextLine();
        }
        return name;
    }
    public  void add() throws SQLException {



        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String type,name;
        Meal tmp;

        while(true) {

             type = scan.nextLine().trim().toLowerCase();

                if( type.equals("breakfast")) {

                    name = ReadName();
                    Meal m = new Breakfast(name, type);
                    m.addingredient();
                    meals.add(m);
                    tmp = m;

                    break ;

                }else if (type.equals("lunch")) {

                     name = ReadName();
                    Meal m = new Launch(name, type);
                    m.addingredient();
                    meals.add(m);
                    tmp = m;
                    break ;

                }
                 else if(type.equals("dinner")) {

                     name = ReadName();
                    Meal m = new Dinner(name, type);
                    m.addingredient();
                    meals.add(m);
                    tmp = m;
                    break ;
                }else {
                     System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                }
        }
        HashMeal.put(tmp.getName(),tmp);
        mealId.put(tmp.getName(),last_id);
        SaveToDataBase(tmp);


    }

    private void printType( String type) throws SQLException {
        LinkedList<Meal> res = LoadType(String.format("'%s'" , type));
        if(res.isEmpty()) {
            System.out.println("No meals found.");
        }

        System.out.println("Category: "+type+"\n" );
        for(Meal m : res)
        {
            if(m.getCategory().equals(type))
            {
                m.print();
            }
        }

    }
    public void show() throws SQLException {


        

            while (true) {

                System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
                String input = scan.nextLine();
                if(input.equals("breakfast") || input.equals("lunch") || input.equals("dinner") ) {
                    printType(input);break;
                }else {
                    System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                }


            }


     }

    public  void plan() {

        String Delete = "DELETE FROM plan";
        try (PreparedStatement preparedStatement = connection.prepareStatement(Delete)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        HashMap<String ,  LinkedList<String> > plans = new HashMap<>();
        String[] week = {"Monday","Tuesday","Wednesday","Thursday","Friday",
                "Saturday","Sunday"};
        String[] types = {"breakfast" , "lunch" , "dinner"};
        PriorityQueue<String> pq;

        for (String d : week) {
            System.out.println(d);
            plans.put(d,new LinkedList<String>());
            for(String t : types) {
               pq = new PriorityQueue<String>();
                for(Meal meal : meals )
                {
                    if(meal.getCategory().equals(t))
                    {
                       // System.out.println(meal.getName());
                        pq.add(meal.getName());
                    }
                }
                while(!pq.isEmpty()){
                    System.out.println(pq.peek());
                    pq.poll();
                }
                System.out.print("Choose the "+t+" for "+d+" from the list above:\n");
                while (true) {

                    String choice = scan.nextLine(); // possiable error
                    if (HashMeal.containsKey(choice)) {
                        plans.get(d).add(t+": "+choice);
                        Meal i = HashMeal.get(choice);
                        String insert = "INSERT INTO plan (meal_id,category,meal,weekDay)" +
                                "VALUES(?,?,?,?)";

                        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                            preparedStatement.setInt(1, mealId.get(choice));
                            preparedStatement.setString(2, String.format("'%s'", HashMeal.get(choice).getCategory()));
                            preparedStatement.setString(3, String.format("'%s'", choice));
                            preparedStatement.setString(4, String.format("'%s'", d));
                            preparedStatement.executeUpdate();
                            break;
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }



                    } else {
                        System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                    }
                }




            }
            System.out.println("Yeah! We planned the meals for " + d + ".\n");

        }
        for(String day : week)
        {
            System.out.println(day);
            for(String m : plans.get(day)) {
                    System.out.println(m);
            }
            System.out.println();
        }


    }

    public void save() {

        HashMap<String,Integer> freq = new HashMap<>();
        String select = "SELECT meal from plan";
        boolean SAVE = false;
        try(ResultSet meals = statement.executeQuery(select)){

            while(meals.next()) {

                String mealName = meals.getString("meal");
                String res = mealName.substring(1,mealName.length()-1);
                LinkedHashSet<String> ing = HashMeal.get(res).getIngredients();

                for (String i : ing) {

                    if (freq.containsKey(i)) {
                        freq.put(i, freq.get(i) + 1);
                    } else {
                        freq.put(i, 1);
                    }

                }
                SAVE = true;

            }
        }catch (SQLException exp)
        {
            exp.printStackTrace();
        }

        if(!SAVE) {
            System.out.println("Unable to save. Plan your meals first.");
            return ;
        }

        System.out.println("Input a filename:");
        String FileName = "./"+ scan.nextLine();
        File file = new File(FileName);



        try(PrintWriter write = new PrintWriter(file)) {

            freq.forEach(
                    (key,value) -> write.printf("%s" + ((value > 1) ? " x%d\n":"\n"), key,value)
            );

        }catch (IOException exp) {
            exp.printStackTrace();
        }


        System.out.println("Saved!");
    }
    public  void run() throws SQLException {


         connection.setAutoCommit(true);
         loadDataBase();
         
         label:
         while(true)
         {
             System.out.print("What would you like to do (add, show, plan, save, exit)?\n");
             String input = scan.nextLine().toLowerCase();
             switch (input) {
                 case "add":
                     add();
                     break;
                 case "show":
                     show();
                     break;
                 case "plan":
                     plan();
                     break;
                 case "save":
                     save();
                     break ;
                 case "exit":
                     System.out.println("Bye!");
                     break label;
             }

         }
         statement.close();
         connection.close();
     }





}
