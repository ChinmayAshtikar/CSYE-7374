/**
 * 
 */
package edu.neu.csye7374;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import edu.neu.csye7374.Inventory.Item.ItemBuilder;

/**
 * @author pratiknakave
 *
 */
public class Inventory {

	/**
	 * StoreAPI interface TODO Student complete implementation
	 */
	private interface StoreAPI {

		void add(SellableAPI item);

		void addEmployees(Person person);

		void saveEmployees();

		void loadEmployees();

		List<Person> getEmployees();

		void sortEmployees(Comparator<Person> c);

		void sortItems(Comparator<SellableAPI> c);
	}

	private class Store implements StoreAPI {
		private String name = null;
		private List<SellableAPI> perishableItemList = new ArrayList<>();
		private List<SellableAPI> nonPerishableItemList = new ArrayList<>();
		private List<SellableAPI> allItems = new ArrayList<>();
		private List<Person> employeeList = new ArrayList<>();
		FacadeAPI facadeAPI;

		public Store() {
			super();
			facadeAPI = new Facade();
		}

		@Override
		public void add(SellableAPI item) {

			if (item != null) {
				if (item.isPerishable() && !perishableItemList.contains(item)) {
					perishableItemList.add(item);
					allItems.add(item);
				}
				if (!item.isPerishable() && !nonPerishableItemList.contains(item)) {
					nonPerishableItemList.add(item);
					allItems.add(item);
				} else {
					System.out.println("Item already exists");
				}
			}
		}

		@Override
		public void addEmployees(Person person) {
			if (!employeeList.contains(person)) {
				employeeList.add(person);
			}
		}

		@Override
		public void saveEmployees() {
			facadeAPI.save(employeeList);
		}

		@Override
		public void loadEmployees() {
			this.employeeList = facadeAPI.load();
		}

		@Override
		public List<Person> getEmployees() {
			return employeeList;
		}

		@Override
		public void sortEmployees(Comparator<Person> c) {
			Collections.sort(employeeList, c);
		}

		@Override
		public void sortItems(Comparator<SellableAPI> c) {
			Collections.sort(allItems, c);
		}

		/**
		 * TODO BY STUDENT
		 *
		 * Implement StoreAPI, etc.
		 */

	} // end Store class

	public static class Item implements SellableAPI, Comparable<SellableAPI> {
		private int id;
		private String itemTag;
		private double price;
		private String description;
		private String boughtItem;
		private boolean isPerishable;

		public Item(ItemBuilder builder) {
			this.id = builder.id;
			this.itemTag = builder.itemTag;
			this.price = builder.price;
			this.description = builder.description;
			this.isPerishable = builder.isPerishable;
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
			private boolean isPerishable;

			public static ItemBuilder newInstance() {
				return new ItemBuilder();
			}

			private ItemBuilder() {
			}

			public ItemBuilder setIsPerishable(boolean val) {
				this.isPerishable = val;
				return this;
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
			return "id = " + this.id + ", name = " + this.itemTag + ", price = " + this.price + ", Description = "
					+ this.description + ", Bought Item = " + this.boughtItem;
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
		public boolean isPerishable() {
			return this.isPerishable;
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

		boolean isPerishable();
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
			if (factoryType.equalsIgnoreCase("item")) {
				instance = ItemFactory.getInstance();
			} else if (factoryType.equalsIgnoreCase("person")) {
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

		public Person(PersonBuilder builder) {
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
			return "Person [age=" + age + ", fName=" + fName + ", id=" + id + ", lName=" + lName + ", Salary =" + salary
					+ "]";
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

			return Employee.PersonBuilder.newInstance().setId(id).setAge(age).setfName(fName).setlName(lName)
					.setSalary(salary).build();
		}

	}

	// Strategy Pattern
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

	private static interface FacadeAPI {
		void save(List<Person> programData);

		List<Person> load();
	}

	static public class Facade implements FacadeAPI {
		private static final String fileName = "EmployeeRoster";

		@Override
		public void save(List<Person> programData) {
			try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
					ObjectOutputStream out = new ObjectOutputStream(fileOutputStream)) {

				for (Person person : programData) {
					out.writeObject(person);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}

		@Override
		public List<Person> load() {
			List<Person> persons = new ArrayList<>();

			try (FileInputStream fileInputStream = new FileInputStream(fileName);
					ObjectInputStream inputStream = new ObjectInputStream(fileInputStream)) {
				while (true) {
					try {
						Person p = (Person) inputStream.readObject();
						persons.add(p);
					} catch (EOFException | ClassNotFoundException e) {
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return persons;
		}
	}

	/*
	 * Enum, Eager and Lazy factory implementations for Order
	 */

	static class OrderAdapter implements SellableAPI {
		private OrderAPI order;

		public OrderAdapter(OrderAPI order) {
			this.order = order;
		}

		public double getPrice() {
			return order.getPrice();
		}

		public String getName() {
			return order.getName();
		}

		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return order.getId();
		}

		@Override
		public String getItemName() {
			// TODO Auto-generated method stub
			return order.getName();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return order.getDescription();
		}

		@Override
		public boolean isPerishable() {
			// TODO Auto-generated method stub
			return order.isPerishable();
		}
	}

	/*
	 * Using Factory design pattern and supplied OrderAdapterFactoryAPI, develop
	 * inner classes:
	 * 
	 * OrderAdapterFactory, OrderAdapterFactoryEnumSingleton,
	 * OrderAdapterFactoryEagerSingleton and OrderAdapterFactoryLazySingleton
	 * 
	 */
	private interface OrderAdapterComponentFactoryAPI {

		OrderAdapterFactory getObject();
	}

	/**
	 * OrderAdapterFactory creates OrderAdapter Object
	 */
	public static class OrderAdapterFactory implements OrderAdapterComponentFactoryAPI {

		@Override
		public OrderAdapterFactory getObject() {
			// TODO Auto-generated method stub
			return new OrderAdapterFactory();
		}

	}

	public enum OrderAdapterEnumSingleton {
		INSTANCE;

		private OrderAdapterFactory mySingleton() {
			return new OrderAdapterFactory();
		}
	}

	/**
	 * OrderAdapterEagerSingleton creates OrderAdapter factory
	 */
	public static class OrderAdapterEagerSingleton {
		private final static OrderAdapterFactory instance = new OrderAdapterFactory();

		private OrderAdapterEagerSingleton() {
		}

		public static OrderAdapterFactory getInstance() {
			return instance;
		}
	}

	/**
	 * OrderAdapterLazySingleton creates OrderAdapter factory
	 */
	public static class OrderAdapterLazySingleton {

		private static OrderAdapterFactory instance;

		private OrderAdapterLazySingleton() {
			// TODO Auto-generated constructor stub
			instance = null;
		}

		public static synchronized OrderAdapterFactory getInstance() {
			if (instance == null) {
				instance = new OrderAdapterFactory();
			}
			return instance;
		}
	}

	/**
	 * OrderAPI implemented by all Order objects for customer orders
	 *
	 */
	private interface OrderAPI {
		int getId();

		double getPrice();

		String getName();

		String getDescription();

		boolean isPerishable();
	}

	private interface OrderComponentAPI {
		void addItem(OrderAPI orderAPI, SellableAPI api);
	}

	/**
	 * 
	 * Using Composite and Builder design pattern, inner classes include:
	 * 
	 * Order, IndividualOrder, IndividualOrderBuilder, ComboOrder and
	 * ComboOrderBuilder
	 * 
	 */

	static class Order implements OrderAPI {
		private int id;
		private String name;
		private double price;
		private String description;
		private boolean perishable;
		private List<SellableAPI> items = new ArrayList<>();

		public int getId() {
			return id;
		}

		public double getPrice() {
			return price;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		private List<SellableAPI> orders = new ArrayList<>();

		public void add(SellableAPI api) {
			items.add(api);
			price += api.getPrice();
		}

		@Override
		public String toString() {
			return "Order [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description
					+ ", items=" + items + ", orders=" + orders + "]";
		}

		static class IndividualOrder extends Order implements OrderComponentAPI {
			private int id;
			private String name;
			private double price;
			private String desc;
			private List<SellableAPI> items = new ArrayList<>();
			DecimalFormat f = new DecimalFormat("##.00");

			private IndividualOrder(IndividualOrderBuilder builder) {
				this.name = builder.name;
				this.price = builder.price;
				this.id = builder.id;
				this.desc = builder.desc;
			}

			public double getPrice() {
				for (SellableAPI item : items) {
					price += item.getPrice();
				}
				return price;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

			public List<SellableAPI> getItems() {
				return items;
			}

			public void setItems(List<SellableAPI> items) {
				this.items = items;
			}

			public void setId(int id) {
				this.id = id;
			}

			public void setPrice(double price) {
				this.price = price;
			}

			public void addItem(OrderAPI o, SellableAPI item) {
				items.add(item);
			}

			public static class IndividualOrderBuilder extends Order {
				private String name;
				private double price;
				private int id;
				private String desc;
				private boolean perishable;

				public IndividualOrderBuilder withPrice(double price) {
					this.price = price;
					return this;
				}

				public IndividualOrderBuilder withName(String name) {
					this.name = name;
					return this;
				}

				public IndividualOrderBuilder withDesc(String desc) {
					this.desc = desc;
					return this;
				}
				
				public IndividualOrderBuilder withPerishable(boolean perishable) {
					this.perishable = perishable;
					return this;
				}

				public IndividualOrderBuilder withId(int id) {
					this.id = id;
					return this;
				}

				public IndividualOrder build() {
					return new IndividualOrder(this);
				}

				@Override
				public String toString() {
					return "IndividualOrderBuilder [name=" + name + ", price=" + price + ", id=" + id + 
							", desc=" + desc + ", perishable=" + perishable
							+ "]";
				}
			}

			@Override
			public int getId() {
				// TODO Auto-generated method stub
				return this.id;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return this.desc;
			}

			@Override
			public String toString() {
				return "IndividualOrder [id=" + id + ", name=" + name + ", price=" + f.format(getPrice()) + ", desc="
						+ desc + ", items=" + items + "]";
			}
		}

		@Override
		public boolean isPerishable() {
			return this.perishable;
		}
	}

	public static class ComboOrder extends Order implements OrderComponentAPI {
		private int id;
		private String name;
		private double price;
		private String desc;
		private boolean perishable;
		DecimalFormat f = new DecimalFormat("##.00");

		private List<SellableAPI> items = new ArrayList<>();

		private ComboOrder(ComboOrderBuilder b) {
			this.items = b.items;
			this.id = b.id;
			this.name = b.name;
			this.desc = b.desc;
			this.price = b.price;
			this.perishable = b.perishable;
		}

		public void addItem(OrderAPI order, SellableAPI item) {
			items.add(item);
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public boolean isPerishable() {
			return perishable;
		}

		public void setPerishable(boolean perishable) {
			this.perishable = perishable;
		}

		public List<SellableAPI> getItems() {
			return items;
		}

		public void setItems(List<SellableAPI> items) {
			this.items = items;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public double getPrice() {
			double price = 0;
			for (SellableAPI item : items) {
				price += item.getPrice();
			}
			return price;
		}

		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return this.id;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return this.desc;
		}

		public String getName() {
			StringBuilder nameBuilder = new StringBuilder();
			for (SellableAPI item : items) {
				if (nameBuilder.length() > 0) {
					nameBuilder.append(", ");
				}
				nameBuilder.append(item.getItemName());
			}
			return nameBuilder.toString();
		}

		@Override
		public String toString() {
			return "ComboOrder [id=" + id + ", name=" + getName() + ", price=" + f.format(getPrice()) + ", desc=" + desc
					+ ", items=" + items + "]";
		}

		public static class ComboOrderBuilder extends Order implements OrderComponentAPI {
			private List<SellableAPI> items = new ArrayList<>();
			private int id;
			private String name;
			private double price;
			private String desc;
			private boolean perishable;

			public ComboOrderBuilder addItem(SellableAPI item) {
				items.add(item);
				return this;
			}

			public ComboOrderBuilder withPrice(double price) {
				this.price = price;
				return this;
			}

			public ComboOrderBuilder withName(String name) {
				this.name = name;
				return this;
			}

			public ComboOrderBuilder withDesc(String desc) {
				this.desc = desc;
				return this;
			}
			
			public ComboOrderBuilder withPerishable(boolean perishable) {
				this.perishable = perishable;
				return this;
			}

			public ComboOrderBuilder withId(int id) {
				this.id = id;
				return this;
			}

			public ComboOrder build() {
				return new ComboOrder(this);
			}

			@Override
			public String toString() {
				return "ComboOrderBuilder [items=" + items + "]";
			}

			@Override
			public void addItem(OrderAPI orderAPI, SellableAPI api) {
				// TODO Auto-generated method stub
				items.add(api);
			}
		}
	}

	private interface OrderComponentFactoryAPI {
		OrderComponentAPI getObject();

		OrderComponentAPI getObject(OrderAPI b);
	}

	public static class IndividualOrderFactory implements OrderComponentFactoryAPI {

		@Override
		public OrderComponentAPI getObject() {
			Order.IndividualOrder.IndividualOrderBuilder b = new Order.IndividualOrder.IndividualOrderBuilder();
			return new Order.IndividualOrder(b);
		}

		@Override
		public OrderComponentAPI getObject(OrderAPI b) {
			return new Order.IndividualOrder((Order.IndividualOrder.IndividualOrderBuilder) b);
		}
	}

	public enum IndividualOrderEnumSingleton {
		INSTANCE;

		private IndividualOrderFactory mySingleton() {
			return new IndividualOrderFactory();
		}
	}

	/**
	 * IndividualOrderEagerSingleton creates IndividualOrder factory
	 */
	public static class IndividualOrderEagerSingleton {
		private final static IndividualOrderFactory instance = new IndividualOrderFactory();

		private IndividualOrderEagerSingleton() {
		}

		public static IndividualOrderFactory getInstance() {
			return instance;
		}
	}

	/**
	 * IndividualOrderLazySingleton creates IndividualOrder factory
	 */
	public static class IndividualOrderLazySingleton {

		private static IndividualOrderFactory instance;

		private IndividualOrderLazySingleton() {
			// TODO Auto-generated constructor stub
			instance = null;
		}

		public static synchronized IndividualOrderFactory getInstance() {
			if (instance == null) {
				instance = new IndividualOrderFactory();
			}
			return instance;
		}
	}

	/**
	 * ComboOrderComponentFactory, ComboOrderComponentFactoryEnumSingleton,
	 * ComboOrderComponentFactoryEagerSingleton and
	 * ComboOrderComponentFactoryLazySingleton
	 */
	public static class ComboOrderComponentFactory implements OrderComponentFactoryAPI {

		@Override
		public OrderComponentAPI getObject() {
			ComboOrder.ComboOrderBuilder b = new ComboOrder.ComboOrderBuilder();
			return new ComboOrder(b);
		}

		@Override
		public OrderComponentAPI getObject(OrderAPI b) {
			return new ComboOrder((ComboOrder.ComboOrderBuilder) b);
		}
	}

	public enum ComboOrderComponentFactoryEnumSingleton {
		INSTANCE;

		private ComboOrderComponentFactory mySingleton() {
			return new ComboOrderComponentFactory();
		}
	}

	/**
	 * ComboOrderComponentFactoryEagerSingleton creates ComboOrder factory
	 */
	public static class ComboOrderComponentFactoryEagerSingleton {
		private final static ComboOrderComponentFactory instance = new ComboOrderComponentFactory();

		private ComboOrderComponentFactoryEagerSingleton() {
		}

		public static ComboOrderComponentFactory getInstance() {
			return instance;
		}
	}

	/**
	 * ComboOrderComponentFactoryLazySingleton creates ComboOrder factory
	 */
	public static class ComboOrderComponentFactoryLazySingleton {

		private static ComboOrderComponentFactory instance;

		private ComboOrderComponentFactoryLazySingleton() {
			// TODO Auto-generated constructor stub
			instance = null;
		}

		public static synchronized ComboOrderComponentFactory getInstance() {
			if (instance == null) {
				instance = new ComboOrderComponentFactory();
			}
			return instance;
		}
	}

	/**
	 * 
	 * Using Decorator design pattern, develop inner classes:
	 * 
	 * ItemDecoratorAPI, and others as needed for all ItemAPI options
	 * 
	 */

	// Decorator design pattern
	static class ItemDecoratorAPI implements SellableAPI {
		private final SellableAPI item;

		public ItemDecoratorAPI(SellableAPI item) {
			this.item = item;
		}

		public double getPrice() {
			return item.getPrice();
		}

		public String getName() {
			return item.getItemName();
		}

		@Override
		public String toString() {
			return "ItemDecoratorAPI [item=" + item + ", getPrice()=" + getPrice() + ", getName()=" + getName() + "]";
		}

		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return item.getId();
		}

		@Override
		public String getItemName() {
			// TODO Auto-generated method stub
			return item.getItemName();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return item.getDescription();
		}

		@Override
		public boolean isPerishable() {
			// TODO Auto-generated method stub
			return item.isPerishable();
		}

	}
	
	static class InsuranceDecorator extends ItemDecoratorAPI {
		private static final double INSURANCE_PRICE = 100.0;

		public InsuranceDecorator(SellableAPI item) {
			super(item);
		}

		public double getPrice() {
			return super.getPrice() + INSURANCE_PRICE;
		}

		public String getName() {
			return super.getName() + " , Insurance";
		}

		@Override
		public String toString() {
			return getName() + " Total Price: " + getPrice();
		}

		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return super.getId();
		}

		@Override
		public String getItemName() {
			// TODO Auto-generated method stub
			return super.getItemName();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return super.getDescription();
		}

		@Override
		public boolean isPerishable() {
			// TODO Auto-generated method stub
			return super.isPerishable();
		}

	}
	
	public interface IInventoryOperations{
		public String generateReceipt(OrderAPI orders);
	}
	
	static class InventoryOperations implements IInventoryOperations{

		@Override
		public String generateReceipt(OrderAPI order) {
			// TODO Auto-generated method stub
			System.out.println("Optional Menu:");

			StringBuilder builder = new StringBuilder();
			builder.append("Id").append("\t").append("Name").append("\t").append("Price").append("\n");
			System.out.println(builder.toString());
			System.out.println("---------------------------");

			order.g.forEach(x -> System.out.println(x));

			System.out.println();
			return null;
		}
		
	}

	public static void demo() {

		/*
		 * TODO Create objects using Enum, Eager and Lazy factory implementations for
		 * Item and Person
		 */
	}
}
