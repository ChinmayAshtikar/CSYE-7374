/**
 * 
 */
package edu.neu.csye7374;

import edu.neu.csye7374.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author pratiknakave
 *
 */
public class Inventory {

	public static final Inventory instance = new Inventory();

	private Inventory() {

	}

	public static Inventory getInstance() {
		return instance;
	}

	/**
	 * StoreAPI interface TODO Student complete implementation
	 */
	private interface StoreAPI {

		void add(SellableAPI item);

		void addEmployees(Person person);

		void loadEmployees();

		void loadItems();

		List<Person> getEmployees();

		List<SellableAPI> getPerishableItems();

		List<SellableAPI> getNonPerishableItems();

		List<SellableAPI> getAllItems();

		void sortEmployees(Comparator<Person> c);

		void sortItems(Comparator<SellableAPI> c);
	}

	private Store store;

	public void setStore(Store store) {
		this.store = store;
	}

	public Store getStore() {
		return this.store;
	}

	public static class Store implements StoreAPI {
		private String name = null;
		private List<SellableAPI> perishableItemList = new ArrayList<>();
		private List<SellableAPI> nonPerishableItemList = new ArrayList<>();
		private List<SellableAPI> allItems = new ArrayList<>();
		private List<Person> employeeList = new ArrayList<>();
		FacadeAPI facadeAPI;
		private StoreState state;

		public Store() {
			super();
			this.state = new Inventory.OpenStoreState();
			facadeAPI = new Facade();
		}

		@Override
		public void add(SellableAPI item) {

			if (item != null) {
				if (item.isPerishable() && !perishableItemList.contains(item)) {
					Date today = new Date();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(today);
					calendar.add(Calendar.DAY_OF_MONTH, 10);
					Date expiry = calendar.getTime();
					item.setExpiryDate(expiry);
					perishableItemList.add(item);
				}
				if (!item.isPerishable() && !nonPerishableItemList.contains(item)) {
					nonPerishableItemList.add(item);
				}
				allItems.add(item);
			}
		}

		@Override
		public void addEmployees(Person person) {
			if (!employeeList.contains(person)) {
				employeeList.add(person);
			}
		}

		@Override
		public void loadEmployees() {
			this.employeeList = facadeAPI.load();
		}

		@Override
		public void loadItems() {
			facadeAPI.loadItems();
		}

		@Override
		public List<Person> getEmployees() {
			return employeeList;
		}

		@Override
		public List<SellableAPI> getPerishableItems() {
			return perishableItemList;
		}

		@Override
		public List<SellableAPI> getNonPerishableItems() {
			return nonPerishableItemList;
		}

		@Override
		public List<SellableAPI> getAllItems() {
			return allItems;
		}

		@Override
		public void sortEmployees(Comparator<Person> c) {
			Collections.sort(employeeList, c);
		}

		@Override
		public void sortItems(Comparator<SellableAPI> c) {
			Collections.sort(allItems, c);
		}

		public StoreState getState() {
			return state;
		}

		public void open() {
			state = new OpenStoreState();
		}

		public void close() {
			state = new ClosedStoreState();
		}

		/**
		 * TODO BY STUDENT
		 *
		 * Implement StoreAPI, etc.
		 */

	} // end Store class

	public static class AnnualReviewTask extends TimerTask {
		Inventory inventory = Inventory.getInstance();
		List<Person> employees = inventory.getStore().getEmployees();
		Date curr = new Date();
		int currDay = curr.getDay();
		int currMonth = curr.getMonth();
		int currYear = curr.getYear();
		SendEmail sendMail = new SendEmail();

		@Override
		public void run() {
			synchronized (Employee.class) {
				for (Person p : employees) {
					Employee e = (Employee) p;
					Date d = e.getHireDate();
					int day = d.getDay();
					int year = d.getYear();
					int month = d.getMonth();
					if (day == currDay && month == currMonth && (currYear - year == 1)) {
						sendMail.sendAnnualReviewMail(p);
					}

					// Testing
//					if(true){
//						sendMail.sendAnnualReviewMail(p);
//					}
				}
			}

		}
	}

	public static class PerishableItemTask extends TimerTask {
		Inventory inventory = Inventory.getInstance();
		List<SellableAPI> perishableItems = inventory.getStore().getPerishableItems();
		Date currentDate = new Date();
		SendEmail sendEmail = new SendEmail();

		@Override
		public void run() {

			for (SellableAPI item : perishableItems) {
				if (item.getExpiryDate() != null) {
					Date expiry = item.getExpiryDate();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(expiry);
					// subtract 10 days from the expiry date
					calendar.add(Calendar.DAY_OF_MONTH, -10);
					Date tenDaysAgo = calendar.getTime();

					if (currentDate.getTime() - tenDaysAgo.getTime() == 0) {
						sendEmail.sendItemMail(item);
					}
//					if(true){
//						sendEmail.sendItemMail(item);
//					}

				}

			}
		}
	}

	public static class TaskScheduler {

		public static void scheduleTasks() {

			Timer timer = new Timer();
			timer.schedule(new AnnualReviewTask(), 0, 180000);
			timer.schedule(new PerishableItemTask(), 0, 180000);

//			timer.schedule(new AnnualReviewTask(), 0, 24 * 60 * 60 * 1000); //runs every day
//			timer.schedule(new PerishableItemTask(), 0, 24 * 60 * 60 * 1000);
		}
	}

	public static class SendEmail {

		Session session;
		String FromEmail = "daycare23csye6200@gmail.com";
		String fBase = "eXd6aHlpdXdkc3htbmJqcg==";

		public SendEmail() {
			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.port", 587);

			session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("daycare23csye6200",
							new String(Base64.getDecoder().decode(fBase)));
				}
			});
		}

		public void sendAnnualReviewMail(Person person) {
			if (person != null) {
				MimeMessage message = new MimeMessage(session);
				try {
					message.setFrom(new InternetAddress(FromEmail));
					message.setSubject("Reminder: Your annual review is due today");
					message.setText("Dear " + person.getfName() + "," + "\n" + "Your annual review is due today" + "\n"
							+ "Sincerly, \n StoreTeam");
					if (person.getEmailID() != null) {
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(person.getEmailID()));
						Transport.send(message);
					}
				} catch (MessagingException ex) {
					System.out.println(ex.getMessage());
				}
			}
		}

		public void sendItemMail(SellableAPI item) {

			MimeMessage message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(FromEmail));
				message.setSubject("Reminder: Items Expiry");
				message.setText("Dear Store Manager" + "," + "\n" + "Item name : " + item.getItemName()
						+ " Item will expire in next 10 days" + "\n" + "Sincerly, \n StoreTeam");
				message.addRecipient(Message.RecipientType.TO, new InternetAddress("bhatti.r@northeastern.edu"));
				Transport.send(message);

			} catch (MessagingException ex) {
				System.out.println(ex.getMessage());
			}

		}

	}

	static class Item implements SellableAPI, Comparable<SellableAPI> {
		private int id;
		private String itemTag;
		private double price;
		private String description;
		private boolean isPerishable;
		private Date expiryDate;

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

		@Override
		public Date getExpiryDate() {
			return expiryDate;
		}

		@Override
		public void setExpiryDate(Date expiryDate) {
			this.expiryDate = expiryDate;
		}

		static class ItemBuilder {
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

		@Override
		public String toString() {
			return this.id + " " + this.itemTag + " " + this.price;
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

		void setExpiryDate(Date d);

		Date getExpiryDate();
	}

	private static List<Employee> readEmployeesFromCSV(String fileName) {
		List<Employee> emps = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);

		// create an instance of BufferedReader
		// using try with resource, Java 7 feature to close resources
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

			// read the first line from the text file
			String line = br.readLine();

			// loop until all lines are read
			while (line != null) {

				// use string.split to load a string array with the values from
				// each line of
				// the file, using a comma as the delimiter
				String[] attributes = line.split(",");

				Employee emp = createEmployee(attributes);

				// adding book into ArrayList
				emps.add(emp);

				// read next line before looping
				// if end of file reached, line would be null
				line = br.readLine();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return emps;
	}

	private static Employee createEmployee(String[] metadata) {
		int id = Integer.parseInt(metadata[0]);
		String fName = metadata[1];
		String lName = metadata[2];
		int age = Integer.parseInt(metadata[3]);
		double salary = Double.parseDouble(metadata[4]);
		String emailId = metadata[5];

		int price = Integer.parseInt(metadata[1]);
		String author = metadata[2];

		// create and return book of this metadata

		return new Employee(EmployeeBuilder.newInstance().setId(id).setAge(age).setfName(fName).setlName(lName)
				.setSalary(salary).setEmailID(emailId));
		// Employee.PersonBuilder.newInstance().setId(id).setAge(age).setfName(fName).setlName(lName)
		// .setSalary(salary).setEmailID(emailID).build();
	}

	static class EmployeeBuilder extends edu.neu.csye7374.Inventory.Person.PersonBuilder {
		private List<Employee> subordinates;
		private boolean isStudent;
		private boolean isEmployed;

		public boolean isStudent() {
			return isStudent;
		}

		public void setStudent(boolean isStudent) {
			this.isStudent = isStudent;
		}

		public boolean isEmployed() {
			return isEmployed;
		}

		public void setEmployed(boolean isEmployed) {
			this.isEmployed = isEmployed;
		}

		public static EmployeeBuilder newInstance() {
			return new EmployeeBuilder();
		}

		private EmployeeBuilder() {
		}

		public EmployeeBuilder setSubordinates(List<Employee> subordinates) {
			this.subordinates = subordinates;
			return this;
		}

		public Employee build() {
			return new Employee(this);
		}
	}

	static class Employee extends Person {
		private boolean isStudent;
		private boolean isEmployed;
		private Date hireDate = new Date();
		// private List<Employee> subordinates;

//		public List<Employee> getSubordinates() {
//			return subordinates;
//		}

		public Employee(EmployeeBuilder builder) {
			super(builder);
			this.isEmployed = builder.isEmployed;
			this.isStudent = builder.isStudent;
		}

		public Employee(PersonBuilder builder) {
			super(builder);
		}

		public boolean getIsStudent() {
			return isStudent;
		}

		public boolean getIsEmployed() {
			return isEmployed;
		}

		public Date getHireDate() {
			return hireDate;
		}

		public void setHireDate(Date hireDate) {
			this.hireDate = hireDate;
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
			double price;
			boolean perishable;
			List<String> items = new ArrayList<>();

			Scanner scan = new Scanner(csv);
			scan.useDelimiter(",");
			while (scan.hasNext()) {
				items.add(scan.next());
			}
			scan.close();

			id = Integer.parseInt(items.get(0));
			price = Double.parseDouble(items.get(1));
			name = items.get(2);
			description = items.get(3);
			perishable = Boolean.parseBoolean(items.get(4));

			return Item.ItemBuilder.newInstance().setId(id).setItemTag(name).setDescription(description).setPrice(price)
					.setIsPerishable(perishable).build();
		}

	}

	public class FactoryObjectInitializer {
		private SingletonFactory instance;

		public FactoryObjectInitializer(String factoryType) {
			if (factoryType.equalsIgnoreCase("item")) {
				instance = ItemFactory.getInstance();
			} else if (factoryType.equalsIgnoreCase("person")) {
				instance = PersonFactory.getInstance();
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
		protected String emailID;

		public Person(PersonBuilder builder) {
			this.id = builder.id;
			this.fName = builder.fName;
			this.lName = builder.lName;
			this.age = builder.age;
			this.salary = builder.salary;
			this.emailID = builder.emailID;

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

		public String getEmailID() {
			return emailID;
		}

		static class PersonBuilder {
			private int id;
			private String fName;
			private String lName;
			private int age;
			private double salary;
			private String emailID;

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

			public PersonBuilder setEmailID(String emailID) {
				this.emailID = emailID;
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

	public static class PersonFactory implements SingletonFactory {
		private static PersonFactory instance;

		private PersonFactory() {
		}

		synchronized public static PersonFactory getInstance() {
			if (instance == null) {
				instance = new PersonFactory();
			}
			return instance;
		}

		@Override
		public Person getObject(String csv) {
			int id, age;
			double salary;
			String fName, lName;
			String emailID;
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
			emailID = people.get(5);

			return Employee.PersonBuilder.newInstance().setId(id).setAge(age).setfName(fName).setlName(lName)
					.setSalary(salary).setEmailID(emailID).build();
		}

	}

	// Strategy Pattern
	public interface DiscountStrategy {
		double getDiscountRate();
	}

	static class SaleDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying SaleDiscount");
			return 0.05;
		}

	}

	static class PresidentDayDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying PresidentDayDiscount");
			return 0.10;
		}

	}

	static class MemorialDayDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying MemorialDayDiscount");
			return 0.15;
		}

	}

	static class ChristmasDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying ChristmasDiscount");
			return 0.20;
		}

	}

	static class ClearanceDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying ClearanceDiscount");
			return 0.25;
		}

	}

	static class WholesalerDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying WholesalerDiscount");
			return 0.30;
		}

	}

	static class MembersOnlyDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying MembersOnlyDiscount");
			return 0.35;
		}

	}

	static class LiquidationDiscount implements DiscountStrategy {

		@Override
		public double getDiscountRate() {
			System.out.println("Applying LiquidationDiscount");
			return 0.40;
		}

	}

	static class DiscountContext {
		private DiscountStrategy strategy;
		private OrderAPI order;
		DecimalFormat df = new DecimalFormat("##");

		public DiscountContext(DiscountStrategy strategy, OrderAPI order) {
			this.strategy = strategy;
			this.order = order;
		}

		public double executeStrategy() {
			double total = (1 - strategy.getDiscountRate()) * order.getPrice();
			return Double.valueOf(df.format(total));
		}
	}

	private static interface FacadeAPI {
		List<Person> load();

		void loadItems();
	}

	static public class Facade implements FacadeAPI {
		private static final String fileName = "src/main/resources/Employees.txt";
		private static final String itemFileName = "src/main/resources/items.txt";

		@Override
		public List<Person> load() {
			Inventory inventory = Inventory.getInstance();
			Store store = inventory.getStore();
			List<Person> employees = new ArrayList<>();

			try (FileReader fileReader = new FileReader(fileName); BufferedReader br = new BufferedReader(fileReader)) {
				String line;
				while ((line = br.readLine()) != null && line.length() > 0) {
					Employee emp1 = (Employee) PersonFactory.getInstance().getObject(line);
					emp1.setIsEmployed(true);
					emp1.setHireDate(new Date());
					employees.add(emp1);
				}
				store.getEmployees().addAll(employees);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return employees;
		}

		@Override
		public void loadItems() {
			// TODO Auto-generated method stub
			Inventory inventory = Inventory.getInstance();
			Store store = inventory.getStore();

			try (FileReader fileReader = new FileReader(itemFileName);
					BufferedReader br = new BufferedReader(fileReader)) {
				String line;
				while ((line = br.readLine()) != null && line.length() > 0) {
					SellableAPI item = (SellableAPI) ItemFactory.getInstance().getObject(line);
					store.add(item);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

		@Override
		public void setExpiryDate(Date d) {

		}

		@Override
		public Date getExpiryDate() {
			return new Date();
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

		List<SellableAPI> getItems();
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
		private OrderState state;
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

		public void setDescription(String description) {
			this.description = description;
		}

		public OrderState getState() {
			return state;
		}

//		public void setState(OrderState state) {
//			this.state = state;
//		}

		public void orderPlaced() {
			state = new OrderPlacedState();
			System.out.println("Order State : " + this.state);
		}

		public void orderDelivered() {
			state = new OrderDeliveredState();
			System.out.println("Order State : " + this.state);
		}
		public void orderShipped() {
			state = new OrderShippedState();
			System.out.println("Order State : " + this.state);
		}


		public void setPerishable(boolean perishable) {
			this.perishable = perishable;
		}

		public void add(SellableAPI api) {
			items.add(api);
			price += api.getPrice();
		}

		@Override
		public String toString() {
			return "Order [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description
					+ ", items=" + items;
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
					return "IndividualOrderBuilder [name=" + name + ", price=" + price + ", id=" + id + ", desc=" + desc
							+ ", perishable=" + perishable + "]";
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

		@Override
		public void setExpiryDate(Date d) {

		}

		@Override
		public Date getExpiryDate() {
			return null;
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

	// Observer Pattern to notify when inventory is full

//	public interface Observer {
//	    void update(boolean isFull);
//	}
//	
//	public interface Subject {
//	    void registerObserver(Observer observer);
//	    void removeObserver(Observer observer);
//	    void notifyObservers();
//	}
//	
//	public class User implements Observer {
//	    private String name;
//
//	    public User(String name) {
//	        this.name = name;
//	    }
//
//	    @Override
//	    public void update(boolean isFull) {
//	        if (isFull) {
//	            System.out.println("Notification to " + name + ": Inventory is full.");
//	        } else {
//	            System.out.println("Notification to " + name + ": Inventory is not full.");
//	        }
//	    }
//	}
//	
//	public class InventoryStorage implements Subject {
//	    private List<Observer> observers;
//	    private int capacity;
//	    private int itemCount;
//
//	    public InventoryStorage(int capacity) {
//	        observers = new ArrayList<>();
//	        this.capacity = capacity;
//	        itemCount = 0;
//	    }
//
//	    @Override
//	    public void registerObserver(Observer observer) {
//	        observers.add(observer);
//	    }
//
//	    @Override
//	    public void removeObserver(Observer observer) {
//	        observers.remove(observer);
//	    }
//
//	    @Override
//	    public void notifyObservers() {
//	        boolean isFull = itemCount == capacity;
//	        for (Observer observer : observers) {
//	            observer.update(isFull);
//	        }
//	    }
//
//	    public void addItem() {
//	        if (itemCount < capacity) {
//	            itemCount++;
//	            notifyObservers();
//	        }
//	    }
//	}

	// The Observer interface
	interface Observer {
		void update();
	}

	// The Observable subject class
	static class InventoryManager {
		private List<Observer> observers = new ArrayList<>();
		private int inventoryCount = 0;

		public int getInventoryCount() {
			return inventoryCount;
		}

		public void setInventoryCount(int count) {
			inventoryCount = count;
			notifyObservers();
		}

		public void addObserver(Observer observer) {
			observers.add(observer);
		}

		public void removeObserver(Observer observer) {
			observers.remove(observer);
		}

		public void notifyObservers() {
			for (Observer observer : observers) {
				observer.update();
			}
		}
	}

	// The Command interface
//	interface Command {
//	    void execute();
//	}

	// The Concrete Command class that buys items
//	static class BuyItemsCommand implements Command {
//		private InventoryManager inventoryManager;
//		private Producer producer;
//		private Consumer consumer;
//
//		public BuyItemsCommand(InventoryManager inventoryManager) {
//			this.inventoryManager = inventoryManager;
//		}
//
//		public BuyItemsCommand(InventoryManager inventoryManager, Producer producer, Consumer consumer) {
//			this.inventoryManager = inventoryManager;
//			this.producer = producer;
//			this.consumer = consumer;
//		}
//
//		public void execute() {
//			if (inventoryManager.getInventoryCount() > 0) { // check if inventory is not empty
//				System.out.println("Buying items...");
//				// producer.updateMoney(0);
//				// Code to buy items
//				inventoryManager.setInventoryCount(inventoryManager.getInventoryCount() - 1); // decrease inventory
//																								// count
//			} else {
//				System.out.println("Inventory is empty, cannot buy items.");
//			}
//		}
//	}

	// The Concrete Observer class that invokes the Command
	static class InventoryObserver implements Observer {
		private Command command;

		public InventoryObserver(Command command) {
			this.command = command;
		}

		public void update() {
			command.execute();
		}
	}

	// The main method that sets up the Observer and Command pattern

	public interface IInventoryOperations {
		public void generateReceipt(OrderAPI order, DiscountStrategy discount);
	}

	static class InventoryOperations implements IInventoryOperations {

		@Override
		public void generateReceipt(OrderAPI order, DiscountStrategy discount) {
			DecimalFormat df = new DecimalFormat("##.##");
			// TODO Auto-generated method stub
			System.out.println("Optional Menu:");

			StringBuilder builder = new StringBuilder();
			builder.append("Id").append("\t").append("Name").append("\t").append("Price").append("\n");
			System.out.println(builder.toString());
			System.out.println("---------------------------");

			order.getItems().forEach(x -> System.out.println(x));
			DiscountContext dc = new DiscountContext(discount, order);
			double price = dc.executeStrategy();

			System.out.println("---------------------------");
			System.out.println("Order Total price:" + df.format(order.getPrice()));
			System.out.println("Order Total price after discount:" + price);

			System.out.println();
		}
	}

	// To call in the end - generateReceipt and generateEmail
	interface Command {
		void execute();
	}

	static class GenerateReceiptCmd implements Command {

		private OrderAPI order;
		private IInventoryOperations iInventoryOperations;

		public GenerateReceiptCmd(OrderAPI order, IInventoryOperations iInventoryOperations) {
			this.order = order;
			this.iInventoryOperations = iInventoryOperations;
		}

		public void execute() {
			iInventoryOperations.generateReceipt(order, new SaleDiscount());
		}
	}

	static class CommandExecution {
		private List<Command> commands = new ArrayList<>();

		// invoker class - what to execute but not aware of implementation
		public void addCommand(Command command) {
			this.commands.add(command);
		}

		// batch execution
		public void executeCommands() {
			for (Command command : commands) {
				command.execute();
			}
		}
	}

	// Store open/close with state pattern
	public interface StoreState {
		boolean isOpen();
	}

	public interface OrderState {
		boolean isDelivered();
		boolean isShipped();
		boolean isPlaced();
	}

	public static class OrderDeliveredState implements OrderState {

		@Override
		public boolean isDelivered() {
			return true;
		}

		@Override
		public boolean isShipped() {
			return false;
		}

		@Override
		public boolean isPlaced() {
			return false;
		}

		@Override
		public String toString() {
			return "Delivered";
		}
	}

	public static class OrderShippedState implements OrderState {

		@Override
		public boolean isDelivered() {
			return false;
		}

		@Override
		public boolean isShipped() {
			return true;
		}

		@Override
		public boolean isPlaced() {
			return false;
		}

		@Override
		public String toString() {
			return "Shipped";
		}
	}

	public static class OrderPlacedState implements OrderState {

		@Override
		public boolean isDelivered() {
			return false;
		}

		@Override
		public boolean isShipped() {
			return false;
		}

		@Override
		public boolean isPlaced() {
			return true;
		}

		@Override
		public String toString() {
			return "Placed";
		}
	}
	public static class OpenStoreState implements StoreState {
		public boolean isOpen() {
			return true;
		}
	}

	public static class ClosedStoreState implements StoreState {
		public boolean isOpen() {
			return false;
		}
	}

	public static void checkStoreState(Store store) {
		if (store.getState().isOpen())
			System.out.println("Store is open.");
		else
			System.out.println("Store is closed.");
	}

	public static void demo() {

		InventoryManager inventoryManager = new InventoryManager();
		// InventoryObserver inventoryObserver = new InventoryObserver(buyItemsCommand);

//		inventoryManager.addObserver(inventoryObserver);
//		inventoryManager.setInventoryCount(10); // set initial inventory count
//
//		inventoryManager.setInventoryCount(0); // set inventory count to test if the command is invoked or not

		Inventory inventory = Inventory.getInstance();
		Store store = new Store();
		inventory.setStore(store);

		// Store is open
		System.out.println("Store is open: " + store.getState().isOpen());

		store.loadEmployees();
		System.out.println("Size of Employees list: ");
		System.out.println(store.getEmployees().size());
		store.loadItems();
		System.out.println("Size of Item list: ");
		System.out.println(store.getAllItems().size());
		System.out.println("Size of NonPerishableItems list: ");
		System.out.println(store.getNonPerishableItems().size());
		System.out.println("Size of PerishableItems list: ");
		System.out.println(store.getPerishableItems().size());

		Order order = (Order)orderCreation(store.getAllItems(), "New order", "New order desc");
		order.orderShipped();
		order.orderDelivered();

		// Add Items and add employees then run scheduler

		TaskScheduler.scheduleTasks();

		// Store is close
		store.close();
		checkStoreState(store);
		// System.out.println("Store is open: "+store.getState().isOpen());

		/*
		 * Facade pattern to invoke initially. Read from CSV, invoke builder/factory to
		 * create employee objects.
		 * 
		 * Add items to inventory. (Builder/factory pattern)
		 * 
		 * State Pattern to open/close store
		 * 
		 * Open store -> Create orders (Builder/prototype/factory) [No order while store
		 * is closed]
		 * 
		 * Place an order with decorator (any item to decorate).
		 * 
		 * Observer to invoke command
		 * 
		 * Placed order -> Command pattern to generate receipt (with Strategy)/email
		 * 
		 */

		/*
		 * TODO Create objects using Enum, Eager and Lazy factory implementations for
		 * Item and Person
		 */
	}

	/**
	 * Demonstrating order creating and receipt generation through builder,
	 * singleton, command, composite, decorator and strategy design pattern
	 * 
	 * @param items
	 */
	private static OrderAPI orderCreation(List<SellableAPI> items, String orderName, String desc) {
		CommandExecution ce = new CommandExecution();

		if (items.size() == 1) {
			Order.IndividualOrder order = new Order.IndividualOrder.IndividualOrderBuilder().withName(orderName)
					.withDesc(desc).withPrice(0.0).withId(1).build();
			order.addItem(order, items.get(0));
			order.orderPlaced();
			System.out.println(order);

			// Adapt the order to an item
			SellableAPI item3 = new OrderAdapter((OrderAPI) order);

			// Use the item methods to get the name and price of the order
			System.out.println("Item name: " + item3.getItemName());
			System.out.println("Item price: " + item3.getPrice());
			ce.addCommand(new GenerateReceiptCmd(order, new InventoryOperations()));
			ce.executeCommands();
			return order;
		} else {
			ComboOrder.ComboOrderBuilder orderBuilder = new ComboOrder.ComboOrderBuilder().withName(orderName)
					.withPrice(0.0).withId(1).withDesc(desc);
			for (SellableAPI item : items) {
				orderBuilder.addItem(item);
			}
			orderBuilder.build();
			// lazy singleton
			ComboOrder order = (ComboOrder) ComboOrderComponentFactoryLazySingleton.getInstance()
					.getObject((OrderAPI) orderBuilder);
			order.orderPlaced();
			System.out.println(order);
			ce.addCommand(new GenerateReceiptCmd(order, new InventoryOperations()));
			ce.executeCommands();
			return order;
		}
	}

}
