import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Item implements java.rmi.Remote {
	
	private String name;
	private float minVal;
	private long closingTime;
	private float highestBid;
	private boolean available;
	private User creator;
	private User highestBidder;
	private long id;
	private Auction auction;
	ConcurrentHashMap<User, Float> bidders;
	
	class itemDeleter extends TimerTask {
		public void run() {
			try {
				auction.removeItem(id);
			} catch (RemoteException re) {
				System.out.println("RemoteException");
				System.out.println(re);
				re.printStackTrace();
			}
		}
	}
	
	class bidNotifier extends TimerTask {
		public void run() {
			//close the item's auction
			available = false;
			//delete the item from the auction list after 1 minute
			Timer timer = new Timer();
			timer.schedule(new itemDeleter(), 60000);
			//if the highest bid is higher than or equal to the item's minimum value
			if (highestBid >= minVal) {
				User winner = highestBidder;
				try {
					//notify the winner
					winner.notifyResult("You have won the item " + '"' + name + '"' + " with ID " + id + " for £" + highestBid );
					//notify the item creator
					creator.notifyResult("Your item " + '"' + name + '"' + " with ID " + id + " was sold to " + '"' + highestBidder.getName() + '"' + " with user ID " + highestBidder.getId() + " for £" + highestBid);
					//notify the unsuccessful bidders
					for (User i : bidders.keySet()) {
						ArrayList<User> notified = new ArrayList<User>();
						notified.add(highestBidder);
						if (notified.contains(i)) {
							continue;
						}		
						notified.add(i);
					    i.notifyResult("You did not win the item " + '"' + name + '"' + " with ID " + id +" as you were outbid");
					 }
				} catch (RemoteException re) {
					System.out.println("RemoteException");
					System.out.println(re);
					re.printStackTrace();
					return;
				}
			} else {
				//the item did not sell or the minimum value was not met
				try {
					//notify the item creator
					creator.notifyResult("Your item " + '"' + name + '"' + " with ID " + id + " did not sell");
					//if anyone made a bid, notify them
					if (!bidders.isEmpty()) {
						for (User i : bidders.keySet()) {
							ArrayList<User> notified = new ArrayList<User>();
							if (notified.contains(i)) {
								continue;
							}
							notified.add(i);
							i.notifyResult("You did not win the item " + '"' + name + '"' + " with ID " + id + " as your bid was lower than the item's minimum value");
						}
					}
				} catch (RemoteException re) {
					System.out.println("RemoteException");
					System.out.println(re);
					re.printStackTrace();
				}
			}
		}
	}

	
	//item constructor
	public Item(String name, float minVal, long closingTime, User creator, long id, Auction auction) {
		this.name = name;
		this.minVal = minVal;
		this.closingTime = closingTime;
		this.creator = creator;
		this.highestBid = 0;
		this.highestBidder = null;
		this.bidders = new ConcurrentHashMap<User, Float>();
		this.id = id;
		this.auction = auction;
		//start the timer for the item
		Timer timer = new Timer();
		timer.schedule(new bidNotifier(), closingTime * 1000);
		this.available = true;
	}
	
	//getter and setter functions
	public String getName() {
		return name;
	}
	
	public float getMinVal() {
		return minVal;
	}
	
	public long getClosingTime() {
		return closingTime;
	}
	
	public User getCreator() {
		return creator;
	}
	
	public void setHighestBidder(User bidder) {
		this.highestBidder = bidder;
	}
	
	public void setHighestBid(float bid) {
		this.highestBid = bid;
	}
	
	public User getHighestBidder() {
		return highestBidder;
	}
	
	public float getHighestBid() {
		return highestBid;
	}
	
	public boolean getAvailability() {
		return available;
	}
	
	//if a successful bid is made, the bidder and bid are added to the item's bid history
	public void addToBidders(User bidder, float bid) {
		this.bidders.put(bidder, bid);
	}

}
