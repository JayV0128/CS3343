package DataModel;

public class Ticket {
    private String name;
    private int age;
    
	public Ticket() {
	}

    public Ticket(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("Passenger: %s, Age: %d", name, age);
    }
}
