package ui;

import domain.User;
import exceptions.RepositoryException;
import exceptions.ServiceException;
import exceptions.ValidationException;
import service.NetworkService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Console interface with a numbered menu that is shown again after every action. */
public class ConsoleUI {
    private final NetworkService service;
    private final Scanner in = new Scanner(System.in);

    public ConsoleUI(NetworkService service) {
        this.service = service;
    }

    public void run() {
        while (true) {
            printMenu();
            System.out.print("Choose an option: ");
            if (!in.hasNextLine()) return;
            String option = in.nextLine().trim();
            System.out.println();

            try {
                switch (option) {
                    case "1" -> addUser();
                    case "2" -> removeUser();
                    case "3" -> addFriend();
                    case "4" -> removeFriend();
                    case "5" -> showUsers();
                    case "6" -> showFriendships();
                    case "7" -> showNumberOfCommunities();
                    case "8" -> showMostSociableCommunity();
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option. Please choose a number between 0 and 8.");
                }
            } catch (ValidationException | RepositoryException | ServiceException e) {
                System.out.println("Action failed: " + e.getMessage());
            }
            System.out.println();
        }
    }

    // ------------------------------------------------------------ menu

    private void printMenu() {
        System.out.println("========== SOCIAL NETWORK ==========");
        System.out.println(" 1. Add user");
        System.out.println(" 2. Remove user");
        System.out.println(" 3. Add friendship");
        System.out.println(" 4. Remove friendship");
        System.out.println(" 5. Show all users");
        System.out.println(" 6. Show all friendships");
        System.out.println(" 7. Show number of communities");
        System.out.println(" 8. Show most sociable community");
        System.out.println(" 0. Exit");
        System.out.println("====================================");
    }

    // ------------------------------------------------------------ actions

    private void addUser() {
        String firstName = readText("First name: ");
        String lastName = readText("Last name: ");
        User u = service.addUser(firstName, lastName);
        System.out.println("User added: " + u);
    }

    private void removeUser() {
        Long id = readLong("Id of the user to remove: ");
        User u = service.removeUser(id);
        System.out.println("User removed: " + u);
    }

    private void addFriend() {
        Long id1 = readLong("Id of the first user: ");
        Long id2 = readLong("Id of the second user: ");
        service.addFriend(id1, id2);
        System.out.println("Friendship added between " + id1 + " and " + id2 + ".");
    }

    private void removeFriend() {
        Long id1 = readLong("Id of the first user: ");
        Long id2 = readLong("Id of the second user: ");
        service.removeFriend(id1, id2);
        System.out.println("Friendship removed between " + id1 + " and " + id2 + ".");
    }

    private void showUsers() {
        boolean any = false;
        for (User u : service.getUsers()) {
            System.out.println(u);
            any = true;
        }
        if (!any) System.out.println("There are no users yet.");
    }

    private void showFriendships() {
        boolean any = false;
        for (var f : service.getFriendships()) {
            System.out.println(f);
            any = true;
        }
        if (!any) System.out.println("There are no friendships yet.");
    }

    private void showNumberOfCommunities() {
        System.out.println("Number of communities: " + service.numberOfCommunities());
    }

    private void showMostSociableCommunity() {
        List<Long> ids = service.mostSociableCommunity();
        if (ids.isEmpty()) {
            System.out.println("There are no users yet, so there is no community.");
            return;
        }
        Map<Long, User> byId = new HashMap<>();
        for (User u : service.getUsers()) byId.put(u.getId(), u);

        System.out.println("Most sociable community:");
        for (Long id : ids) System.out.println("  " + byId.get(id));
    }

    // ------------------------------------------------------------ input helpers

    /** Reads a line as it is; the validator decides if it is acceptable. */
    private String readText(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    /** Keeps asking until the user types a valid number. */
    private Long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}