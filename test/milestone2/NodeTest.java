package milestone2;

import java.util.Scanner;


/**
 * Created by Juan on 2/19/2016.
 */
public class NodeTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int bootstrap_port = 50001, my_port = 50100;
        String bootstrap_host = "localhost";
        int option;

        System.out.println("Port number: ");
        my_port = sc.nextInt();

        final Node node = new Node(bootstrap_host, my_port);
        System.out.println("Node created, joining in chord");
        System.out.println("Node id: " + node.getID());

        node.join(bootstrap_host, bootstrap_port);

        while (true) {
            System.out.println("\n-------------------------------\n");
            System.out.println("Select an option:");
            System.out.println("1: print table");
            System.out.println("2: leave");

            option = sc.nextInt();
            switch (option)
            {
                case 1: // print table
                    node.printTable();
                    break;

                case 2: // print table
                    System.out.println("BYE");
                    node.leave();
                    System.exit(0);
            }
        }


    }
}
