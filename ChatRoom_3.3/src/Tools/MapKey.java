package Tools;

public class MapKey {

	private String userName;
	private String friendName;

	public MapKey(String userName, String friendName) {
		this.userName = userName;
		this.friendName = friendName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getFriendName() {
		return this.friendName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MapKey))
			return false;
		
		MapKey key = (MapKey) o;
		if (userName.equals(key.userName) && friendName.equals(key.friendName))
			return true;
		if (friendName.equals(key.userName) && userName.equals(key.friendName))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return userName.hashCode() + friendName.hashCode();
	}
}
