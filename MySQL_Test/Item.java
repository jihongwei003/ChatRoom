package cn.bupt.ji.server;

public class Item {

	private int m_itemID;
	private String m_name;
	
	public Item()
	{
		m_itemID = 0;
		m_name = "";
	}
	
	public Item(int ID, String name)
	{
		m_itemID = ID;
		m_name = name;
	}
	
	public void setID(int ID)
	{
		m_itemID = ID;
	}
	
	public int getID()
	{
		return m_itemID;
	}
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public String getName()
	{
		return m_name;
	}
}
