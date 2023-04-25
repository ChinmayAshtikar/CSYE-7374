/**
 * 
 */
package edu.neu.csye7374;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.neu.csye7374.Inventory.Item.ItemBuilder;


/**
 * @author pratiknakave
 *
 */
public class Inventory {
	
	
	
	
	public static class Item implements SellableAPI, Comparable<SellableAPI> {
        private int id;
        private String itemTag;
        private double price;
        private String description;
        private String boughtItem;

        public Item(ItemBuilder builder) {
            this.id = builder.id;
            this.itemTag = builder.itemTag;
            this.price = builder.price;
            this.description = builder.description;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int compareTo(SellableAPI o) {
            Item new_o = (Item) o;
            return this.getId() - new_o.getId();
        }

        public static class ItemBuilder {
            private int id;
            private String itemTag;
            private double price;
            private String description;

            public static ItemBuilder newInstance() {
                return new ItemBuilder();
            }

            private ItemBuilder() {
            }

            public ItemBuilder setId(int id) {
                this.id = id;
                return this;
            }

            public ItemBuilder setItemTag(String name) {
                this.itemTag = name;
                return this;
            }

            public ItemBuilder setPrice(double price) {
                this.price = price;
                return this;
            }

            public ItemBuilder setDescription(String desc) {
                this.description = desc;
                return this;
            }

            public Item build() {
                return new Item(this);
            }

        }

        public void setbought(String bought) {
            this.boughtItem = bought;
        }

        @Override
        public String toString() {
            return "id = " + this.id + ", name = " + this.itemTag +
                    ", price = " + this.price + ", Description = " + this.description + ", Bought Item = " + this.boughtItem;
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public String getItemName() {
            return this.itemTag;
        }

        @Override
        public double getPrice() {
            return this.price;
        }

        @Override
        public String getDescription() {
            return this.description;
        }


        public int compareTo(Item o) {
            return this.getId() - o.getId();
        }

    }

    private interface SellableAPI {
        int getId();

        String getItemName();

        double getPrice();

        String getDescription();
    }
    
    static class Employee extends Person {
        private boolean isStudent;
        private boolean isEmployed;

        public Employee(PersonBuilder builder) {
            super(builder);
        }

        public boolean getIsStudent() {
            return isStudent;
        }

        public boolean getIsEmployed() {
            return isEmployed;
        }


        public void setIsStudent(boolean isStudent) {
            this.isStudent = isStudent;
        }

        public void setIsEmployed(boolean isEmployed) {
            this.isEmployed = isEmployed;
        }

        @Override
        public String toString() {
            return super.toString() + " ,isEmployed=" + isEmployed + ", isStudent=" + isStudent + "]";
        }

    }


    public interface SingletonFactory {
        public Object getObject(String csv);
    }

	public static class ItemFactory implements SingletonFactory {
        private static ItemFactory instance;

        private ItemFactory() {
        }

        synchronized public static ItemFactory getInstance() {
            if (instance == null) {
                instance = new ItemFactory();
            }
            return instance;
        }

        @Override
        public Item getObject(String csv) {
            int id;
            String name, description;
            double Price;
            List<String> items = new ArrayList<>();

            Scanner scan = new Scanner(csv);
            scan.useDelimiter(",");
            while (scan.hasNext()) {
                items.add(scan.next());
            }
            scan.close();

            id = Integer.parseInt(items.get(0));
            Price = Double.parseDouble(items.get(1));
            name = items.get(2);
            description = items.get(3);

            return ItemBuilder.newInstance().setId(id).setItemTag(name).setDescription(description).setPrice(Price)
                    .build();
        }

    }

    public class FactoryObjectInitializer {
        private SingletonFactory instance;

        public FactoryObjectInitializer(String factoryType) {
            if(factoryType.equalsIgnoreCase("item")) {
                instance = ItemFactory.getInstance();
            } else if(factoryType.equalsIgnoreCase("person")) {
                instance = PeopleFactory.getInstance();
            } else {
                instance = null;
            }
        }

        public SingletonFactory getInstance() {
            return this.instance;
        }
    }

	static class Person implements Comparable<Person> {
        protected int id;
        protected String fName;
        protected String lName;
        protected int age;
        protected double salary;

        public Person(PersonBuilder builder)
        {
            this.id = builder.id;
            this.fName = builder.fName;
            this.lName = builder.lName;
            this.age = builder.age;
            this.salary = builder.salary;

        }
        public int getId() {
            return id;
        }

        public String getfName() {
            return fName;
        }

        public String getlName() {
            return lName;
        }

        public int getAge() {
            return age;
        }

        public double getSalary() {
            return salary;
        }


        static class PersonBuilder {
            private int id;
            private String fName;
            private String lName;
            private int age;
            private double salary;

            public static PersonBuilder newInstance() {
                return new PersonBuilder();
            }

            private PersonBuilder() {
            }

            public PersonBuilder setSalary(double salary) {
                this.salary = salary;
                return this;
            }

            public PersonBuilder setId(int id) {
                this.id = id;
                return this;
            }

            public PersonBuilder setfName(String name) {
                this.fName = name;
                return this;
            }

            public PersonBuilder setlName(String name) {
                this.lName = name;
                return this;
            }

            public PersonBuilder setAge(int age) {
                this.age = age;
                return this;
            }

            public Person build() {
                return new Employee(this);
            }

        }


        @Override
        public String toString() {
            return "Person [age=" + age + ", fName=" + fName + ", id=" + id + ", lName=" + lName
                    + ", Salary =" + salary + "]";
        }

        @Override
        public int compareTo(Person o) {
            return Integer.compare(this.getId(), o.getId());
        }
    }

    public static class PeopleFactory implements SingletonFactory {
        private static PeopleFactory instance;

        private PeopleFactory() {
        }

        synchronized public static PeopleFactory getInstance() {
            if (instance == null) {
                instance = new PeopleFactory();
            }
            return instance;
        }

        @Override
        public Person getObject(String csv) {
            int id, age;
            double salary;
            String fName, lName;
            List<String> people = new ArrayList<>();

            Scanner scan = new Scanner(csv);
            scan.useDelimiter(",");
            while (scan.hasNext()) {
                people.add(scan.next());
            }
            scan.close();

            id = Integer.parseInt(people.get(0));
            age = Integer.parseInt(people.get(1));
            fName = people.get(2);
            lName = people.get(3);
            salary = Double.parseDouble(people.get(4));

            return Employee.PersonBuilder.newInstance().setId(id).setAge(age).setfName(fName).setlName(lName).setSalary(salary).build();
        }

    }

	//Strategy Pattern
	public interface DiscountStrategy {
        double getDiscountRate();
    }

    public class SaleDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 5;
        }
        
    }

    public class PresidentDayDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 10;
        }

    }

    public class MemorialDayDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 15;
        }

    }

    public class ChristmasDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 20;
        }

    }

    public class ClearanceDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 25;
        }

    }

    public class WholesalerDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 30;
        }

    }

    public class MembersOnlyDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 35;
        }

    }

    public class LiquidationDiscount implements DiscountStrategy {

        @Override
        public double getDiscountRate() {
            return 40;
        }

    }

    public class DiscountContext {
        private DiscountStrategy strategy;

        public DiscountContext(DiscountStrategy strategy) {
            this.strategy = strategy;
        }

        public double executeStrategy() {
            return strategy.getDiscountRate();
        }
    }
	
	/*
	 * TODO Enum, Eager and Lazy factory implementations for Item and Person
	 */
	
	public static void demo() {
		
		/*
		 * TODO Create objects using Enum, Eager and Lazy factory implementations for Item and Person
		 */
	}
}
