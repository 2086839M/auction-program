import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionClient {
	
	static User user = null;
	static Auction auction = null;
	
	//failure detector
	static class ping extends TimerTask {
		public synchronized void run() {
			try {
				auction.ping();
			} catch (RemoteException re) {
				System.out.println("Cannot make contact with server");
				System.out.println(re);
				System.exit(0);
			}
		}
	}
	
	
	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		System.out.println("Please enter your name: ");
		String in = input.nextLine();
		
		//create a new user and connect to the auction service
		try {
			
			auction = (Auction) Naming.lookup("rmi://localhost/CalculatorService");
			User initial = new UserImpl(in);
			user = auction.newUser(initial);
			
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
		
		//ping the server every 3 seconds to check if it is still running
		Timer timer = new Timer();
		timer.schedule(new ping(), 0, 3000);

		do {
			
			
			//available options for the user
			System.out.println("Please enter an option number: \n"
					+ "1 - Create an auction item \n"
					+ "2 - Bid on an item \n"
					+ "3 - View current auctions \n" 
					+ "4 - Initialise auction from external file \n" 
					+ "5 - Exit");
			in = input.nextLine();
			
			try {
				//get the user's choice
				int choice = Integer.parseInt(in);
				//execute the user's selected choice
				switch (choice) {
				
				case 1:
					System.out.println("\n" + "Enter the name of the item: ");
					String itemName = input.nextLine();
					System.out.println("Enter the minimum item value: ");
					float itemMinVal = input.nextFloat();
					System.out.println("How long do you want the auction to last? (in seconds): ");
					long itemClosingTime = input.nextLong();
					input.nextLine();
					long creationSuccess = auction.createItem(itemName, itemMinVal, itemClosingTime, user, auction);
					System.out.println("Item has been created with ID: " + creationSuccess + "\n");
					break;
					
				case 2:
					
					System.out.println("\n" + "Enter the ID of the item you want to bid on: ");
					long itemId = input.nextLong();
					System.out.println("Enter the amount you want to bid: ");
					float bid = input.nextFloat();
					String result = auction.bid(itemId, bid, user);
					System.out.println(result + "\n");
					input.nextLine();
					break;
					
				case 3:
					ArrayList<String> items = auction.listItems();
					System.out.println("\n" + "Current auctions:");
					for (String item : items) {
						System.out.println(item);
					}
					System.out.println("\n");
					
					break;
					
				case 4:
					boolean restored = auction.restoreState(user, auction);
					if (restored == true) {
						System.out.println("Auction successfully bootstrapped");
						break;
					} else {
						System.out.println("Bootstrapping failed");
						break;
					}
				case 5:
					input.close();
					System.exit(0);
					return;
					
				default: 
					System.out.println("Invalid option, please press Enter to try again");
					input.nextLine();
				}
				
			} catch (RemoteException re) {
				System.out.println("RemoteException");
				System.out.println(re);
			} catch (NumberFormatException nfe) {
				System.out.println("Your input was invalid, please try again");
				System.out.println(nfe);
			} catch (InputMismatchException ime) {
				System.out.println("Your input was invalid, please try again");
				System.out.println(ime);
			}
		} while (true);
		
	}

}
