package edu.neu.csye7374;

import java.util.ArrayList;
import java.util.List;

public class Consumer {
	private int id;
	private String name;
	private double money;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public Consumer(int id, String name, double money) {
		super();
		this.id = id;
		this.name = name;
		this.money = money;
	}
	public boolean updateMoney(double money) {
		if(this.money<money)
			return false;
		this.money-=money;
		return true;
	}
	
	public interface Observer {
	    void update(boolean isFull);
	}
	
	public interface Subject {
	    void registerObserver(Observer observer);
	    void removeObserver(Observer observer);
	    void notifyObservers();
	}
	
	public class User implements Observer {
	    private String name;

	    public User(String name) {
	        this.name = name;
	    }

	    @Override
	    public void update(boolean isFull) {
	        if (isFull) {
	            System.out.println("Notification to " + name + ": Inventory is full.");
	        } else {
	            System.out.println("Notification to " + name + ": Inventory is not full.");
	        }
	    }
	}
	
	public class Inventory implements Subject {
	    private List<Observer> observers;
	    private int capacity;
	    private int itemCount;

	    public Inventory(int capacity) {
	        observers = new ArrayList<>();
	        this.capacity = capacity;
	        itemCount = 0;
	    }

	    @Override
	    public void registerObserver(Observer observer) {
	        observers.add(observer);
	    }

	    @Override
	    public void removeObserver(Observer observer) {
	        observers.remove(observer);
	    }

	    @Override
	    public void notifyObservers() {
	        boolean isFull = itemCount == capacity;
	        for (Observer observer : observers) {
	            observer.update(isFull);
	        }
	    }

	    public void addItem() {
	        if (itemCount < capacity) {
	            itemCount++;
	            notifyObservers();
	        }
	    }
	}
	
	

}


