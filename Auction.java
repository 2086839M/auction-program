import java.util.ArrayList;

public interface Auction extends java.rmi.Remote {
	
	//create an item to be put on auction
	public long createItem(String name, float minVal, long closingTime, User creator, Auction auction) throws java.rmi.RemoteException;
	
	//bid on an item
	public String bid(long id, float bidAmount, User bidder) throws java.rmi.RemoteException;
	
	//list the items up for auction
	public ArrayList<String> listItems() throws java.rmi.RemoteException;
	
	//removes an item from auction 1 minute after it closes
	public void removeItem(long id) throws java.rmi.RemoteException;

	//initialises the auction system from an external file
	public boolean restoreState(User user, Auction auction) throws java.rmi.RemoteException;
	
	public void ping() throws java.rmi.RemoteException;
	
	//assign unique id to a new user
	public User newUser(User user) throws java.rmi.RemoteException;

}
