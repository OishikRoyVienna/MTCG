package model;

public class Card
{
	
    private String name;
    private int damage;
    private String id;

    public Card() {
    	
    }
    public Card(String name, int damage, String id)
    {
        this.name = name;
        this.damage = damage;
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name)
    {
        this.name = name;
    }

    public int getDamage()
    {
        return damage;
    }

    public void setDamage(int damage)
    {
        this.damage = damage;
    }
}