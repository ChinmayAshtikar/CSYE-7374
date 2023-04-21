/**
 * 
 */
package edu.neu.csye7374;

import java.util.ArrayList;
import java.util.List;

import edu.neu.csye7374.Inventory.Item.ItemBuilder;
import edu.neu.csye7374.Inventory.Person.PersonBuilder;

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

        public Item(Builder builder) {
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

        public static class Builder {
            private int id;
            private String itemTag;
            private double price;
            private String description;

            public static Builder newInstance() {
                return new Builder();
            }

            private Builder() {
            }

            public Builder setId(int id) {
                this.id = id;
                return this;
            }

            public Builder setItemTag(String name) {
                this.itemTag = name;
                return this;
            }

            public Builder setPrice(double price) {
                this.price = price;
                return this;
            }

            public Builder setDescription(String desc) {
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

        @Override
        public int compareTo(Driver.SellableAPI o) {
            Item new_o = (Item) o;
            return this.getId() - new_o.getId();
        }

    }
    
    static class Employee extends Person {
        private boolean isStudent;
        private boolean isEmployed;

        public Employee(Builder builder) {
            this.id = builder.id;
            this.fName = builder.fName;
            this.lName = builder.lName;
            this.age = builder.age;
            this.salary = builder.salary;
        }

        public boolean getIsStudent() {
            return isStudent;
        }

        public boolean getIsEmployed() {
            return isEmployed;
        }

        static class Builder {   
            private int id;
            private String fName;
            private String lName;
            private int age;
            private double salary;

            public static Builder newInstance() {
                return new Builder();
            }

            private Builder() {
            }

            public Builder setSalary(double salary) {
                this.salary = salary;
                return this;
            }

            public Builder setId(int id) {
                this.id = id;
                return this;
            }

            public Builder setfName(String name) {
                this.fName = name;
                return this;
            }

            public Builder setlName(String name) {
                this.lName = name;
                return this;
            }

            public Builder setAge(int age) {
                this.age = age;
                return this;
            }

            public Person build() {
                return new Employee(this);
            }

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

            return Item.Builder.newInstance().setId(id).setItemTag(name).setDescription(description).setPrice(Price)
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

        @Override
        public int compareTo(Driver.Person o) {
            return Integer.compare(this.getId(), o.getId());
        }

        @Override
        public String toString() {
            return "Person [age=" + age + ", fName=" + fName + ", id=" + id + ", lName=" + lName
                    + ", Salary =" + salary + "]";
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

            return Employee.Builder.newInstance().setId(id).setAge(age).setfName(fName).setlName(lName).setSalary(salary).build();
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
