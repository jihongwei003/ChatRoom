
/*
CREATE TABLE IF NOT EXISTS `market`.`item` (
  `item_id` INT(11) NOT NULL AUTO_INCREMENT,
  `item_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`item_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;
*/

//数据库表单
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
