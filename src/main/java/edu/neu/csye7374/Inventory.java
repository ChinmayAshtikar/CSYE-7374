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
	
	static List<ItemAPI> itemList = new ArrayList<>();
	static List<PersonAPI> personList = new ArrayList<>();

	/**
	 * ItemAPI implemented by all Item objects for sale
	 *
	 * @author pratiknakave
	 */
	private interface ItemAPI {
		int getId();
		String getName();
	}
	
	/** 
	 * Builder Design Pattern:
	 * 
	 * Using Builder design pattern, develop inner class ItemBuilder 
	 * to custom configure an Item object
	 */
	/**
	 * @author pratiknakave
	 *
	 */
	public static class Item implements ItemAPI{

		protected String name;
		protected int id;
		
		//No need for setters as this is immutable
		
		public Item(ItemBuilder builder) {
			this.name = builder.name;
			this.id = builder.id;
		}
		
		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}
		
		@Override
		public String toString() {
			return "Item [id=$" + id + ", name=" + name + "]";
		}

		public static class ItemBuilder {
			private int id;
			private String name;
			
			public ItemBuilder newInstance() {
				return new ItemBuilder();
			}
			
			public ItemBuilder setId(int id) {
				this.id = id;
				return this;
			}
			
			public ItemBuilder setName(String name) {
				this.name = name;
				return this;
			}
			
			public Item build() {
				return new Item(this);
			}
		}
		
	}
	/*
	 * TODO builder for AutoPart
	 */
	public static class AutoPart extends Item{

		private int makeId;
		
		public int getMakeId() {
			return makeId;
		}

		public AutoPart(ItemBuilder builder) {
			super(builder);
			// TODO Auto-generated constructor stub
		}	
	}
	
	/**
	 * PersonAPI implemented by all Item objects for sale
	 *
	 * @author pratiknakave
	 */
	private interface PersonAPI {
		int getAge();
		String getName();
	}
	
	/** 
	 * Builder Design Pattern:
	 * 
	 * Using Builder design pattern, develop inner class PersonBuilder 
	 * to custom configure an Person object
	 */
	/**
	 * @author pratiknakave
	 *
	 */
	public static class Person implements PersonAPI{

		int age;
		String name;
		
		@Override
		public int getAge() {
			// TODO Auto-generated method stub
			return age;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}
		
		public Person(PersonBuilder builder) {
			this.name = builder.name;
			this.age = builder.age;
		}
		
		@Override
		public String toString() {
			return "Person [age=" + age + ", name=" + name + "]";
		}
		
		public static class PersonBuilder {
			private int age;
			private String name;
			
			public PersonBuilder newInstance() {
				return new PersonBuilder();
			}
			
			public PersonBuilder setAge(int age) {
				this.age = age;
				return this;
			}
			
			public PersonBuilder setName(String name) {
				this.name = name;
				return this;
			}
			
			public Person build() {
				return new Person(this);
			}
		}
		
	}
	/*
	 * TODO builder for Employee
	 */
	public static class Employee extends Person{

		int wage;
		
		public int getWage() {
			return wage;
		}

		public Employee(PersonBuilder builder) {
			super(builder);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	/**
	 * ItemFactoryAPI for Factory method design pattern
	 * @author pratiknakave
	 *
	 */
	private interface ItemFactoryAPI {
		ItemAPI getObject();
		ItemAPI getObject(Item.ItemBuilder b);
	}
	
	/**
	 * @author pratiknakave
	 *
	 */
	public static class ItemFactory implements ItemFactoryAPI{

		private static ItemFactory instance;
		
		private ItemFactory() {
		}
		
		public static synchronized ItemFactory getInstance() {
			if (instance == null) {
				instance = new ItemFactory();
			}
			return instance;
		}
		
		@Override
		public ItemAPI getObject() {
			// TODO Auto-generated method stub
			return new Item.ItemBuilder().newInstance().setName("Item1").setId(1).build();
		}

		@Override
		public ItemAPI getObject(ItemBuilder b) {
			// TODO Auto-generated method stub
			return new Item(b);
		}
		
	}
	
	
	/**
	 * PersonFactoryAPI for Factory method design pattern
	 * @author pratiknakave
	 *
	 */
	private interface PersonFactoryAPI {
		PersonAPI getObject();
		PersonAPI getObject(Person.PersonBuilder b);
	}
	
	/**
	 * @author pratiknakave
	 *
	 */
	public static class PersonFactory implements PersonFactoryAPI{

		private static PersonFactory instance;
		
		private PersonFactory() {
		}
		
		public static synchronized PersonFactory getInstance() {
			if (instance == null) {
				instance = new PersonFactory();
			}
			return instance;
		}
		
		@Override
		public PersonAPI getObject() {
			// TODO Auto-generated method stub
			return new Person.PersonBuilder().newInstance().setName("Alex").setAge(15).build();
		}

		@Override
		public PersonAPI getObject(PersonBuilder b) {
			// TODO Auto-generated method stub
			return new Person(b);
		}
		
	}
	
	/*
	 * TODO Enum, Eager and Lazy factory implementations for Item and Person
	 */
	
	public static void demo() {
		
		System.out.println("------------Creating Items-------------");
		ItemAPI itemAPI = ItemFactory.getInstance().getObject();
		System.out.println(itemAPI);
		itemList.add(itemAPI);
		
		ItemBuilder itemBuilder = new Item.ItemBuilder().newInstance().setName("Item2").setId(2);
		ItemAPI itemAPI1 = ItemFactory.getInstance().getObject(itemBuilder);
		System.out.println(itemAPI1);
		itemList.add(itemAPI1);
		
		System.out.println("\n------------Creating Persons-------------");
		PersonAPI personAPI = PersonFactory.getInstance().getObject();
		System.out.println(personAPI);
		personList.add(personAPI);
		
		PersonBuilder personBuilder = new Person.PersonBuilder().newInstance().setAge(26).setName("Bob");
		PersonAPI personAPI1 = PersonFactory.getInstance().getObject(personBuilder);
		System.out.println(personAPI1);
		personList.add(personAPI1);
		
		/*
		 * TODO Create objects using Enum, Eager and Lazy factory implementations for Item and Person
		 */
	}
}
