import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AuctionTestClient {
	
	public static void main(String[] args) {

		Auction auction = null;
		User user1 = null;
		User user2 = null;
		
		try {
			//create an auction and 2 users
			auction = (Auction) Naming.lookup("rmi://localhost/CalculatorService");
			User initial1 = new UserImpl("test1");
			User initial2 = new UserImpl("test2");
			user1 = auction.newUser(initial1);
			user2 = auction.newUser(initial2);
			
		} catch (RemoteException murle) {
			System.out.println("MalformedURLException");
			System.out.println(murle);
			
		} catch (MalformedURLException re) {
			System.out.println("RemoteException");
			System.out.println(re);
			
		} catch (NotBoundException nbe) {
			System.out.println("NotBoundException");
			System.out.println(nbe);
		}
		
		//timing 1000 item creations
		long startCreationTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			try {
				auction.createItem(String.valueOf(i), (float)100, (long)30, user1, auction);
				auction.createItem(String.valueOf(i), (float)200, (long)30, user2, auction);
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long elapsedCreationTime = System.currentTimeMillis() - startCreationTime;
		System.out.format("1000 item creations in %d ms - %d.%03d ms/call\n", elapsedCreationTime, elapsedCreationTime/10000, (elapsedCreationTime%10000)/10);
		
		//timing 1000 item bids
		long startBiddingTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			try {
				auction.bid(i, 400, user1);
				auction.bid(i, 300, user2);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long elapsedBiddingTime = System.currentTimeMillis() - startBiddingTime;
		System.out.format("1000 item bids in %d ms - %d.%03d ms/call\n", elapsedBiddingTime, elapsedBiddingTime/10000, (elapsedBiddingTime%10000)/10);
		
		//timing 1000 auction lists
		long startListingTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			try {
				auction.listItems();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long elapsedListingTime = System.currentTimeMillis() - startListingTime;
		System.out.format("1000 item lists in %d ms - %d.%03d ms/call\n", elapsedListingTime, elapsedListingTime/10000, (elapsedListingTime%10000)/10);
		
		System.exit(0);
		
	}

}
