import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AuctionImpl extends java.rmi.server.UnicastRemoteObject implements Auction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long itemId;
	private ConcurrentHashMap<Long, Item> items;
	private static AtomicLong uid = new AtomicLong();
	private long userId;
	private static AtomicLong uniqueUser = new AtomicLong();
	
	//constructor
	public AuctionImpl() throws java.rmi.RemoteException {
		super();
		items = new ConcurrentHashMap<Long, Item>();
		itemId = 0;
		userId = 0;
		
	}

	//create an item in the auction
	public long createItem(String name, float minVal, long closingTime, User creator, Auction auction) throws java.rmi.RemoteException {
		itemId = uid.getAndIncrement();
		Item item = new Item(name, minVal, closingTime, creator, itemId, auction);
		items.put(itemId, item);
		return itemId;
	}
	
	//assign a unique id to new users
	public User newUser(User user) throws RemoteException {
		userId = uniqueUser.getAndIncrement();
		user.setId(userId);
		return user;
	}


	//method to allow bidding on items
	public String bid(long id, float bidAmount, User bidder) throws java.rmi.RemoteException {
		String result = "Something failed";
		//if item doesn't exist, return relevant message
		if (!items.containsKey(id)) {
			result = "Selected item does not exist";
			return result;
		}
		//if the bidder is the item's creator, do not allow the bid
		if (bidder.equals(items.get(id).getCreator())) {
			result = "You cannot bid on your own item";
			return result;
		}
		//if the item is not availabe, return relevant message
		if (items.get(id).getAvailability() == false) {
			result = "Bidding for this item has closed";
			return result;
		}
		//if the bid is lower than the item's current highest bid, return relevant message
		if (bidAmount < items.get(id).getHighestBid()) {
			result = "Your bid is lower than the current highest bid";
			return result;
		}
		//if the bid is the same as the item's current highest bid, return relevant message
		if (bidAmount == items.get(id).getHighestBid()) {
			result = "Someone has already bid your amount";
			return result;
		}
		//bid is valid
		if ((bidAmount > items.get(id).getHighestBid())) {
			items.get(id).setHighestBidder(bidder);
			items.get(id).setHighestBid(bidAmount);
			items.get(id).addToBidders(bidder, bidAmount);
			result = "Bid successfully placed";
			return result;
		}
		
		return result;
	}

	//list all items on auction
	public ArrayList<String> listItems() throws java.rmi.RemoteException {
		//hold the entries for each item
		ArrayList<String> listOfItems = new ArrayList<String>();
		
		for (Long i : items.keySet()) {
			
		   String id = i.toString();
		   String name = items.get(i).getName();
		   String available;
		   float curBid = items.get(i).getHighestBid();
		   //check that the item is available
		   if (items.get(i).getAvailability() == false) {
			   available = "Closed";
		   } else {
			   available = "Open";
		   }
		   //add item entry to list
		   listOfItems.add("Item ID: " + id + ", Item name: " + name + ", Highest bid: £" + curBid + ", Bidding: " + available);
		}
		
		return listOfItems;
	}

	//initialise an auction system from an external file
	public boolean restoreState(User user, Auction auction) throws java.rmi.RemoteException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("items.txt"));
			for (String line; (line = reader.readLine()) != null;) {
				String[] split = line.split(" ");
				this.createItem(split[0], Float.parseFloat(split[1]), Long.parseLong(split[2]), user, auction);
			}
			reader.close();
			return true;
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	public void removeItem(long id) throws RemoteException {
		items.remove(id);
		
	}

	public void ping() throws RemoteException {
		
	}
}
