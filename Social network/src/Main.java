import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.FriendshipFileRepository;
import repository.UserFileRepository;
import service.NetworkService;
import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        var userRepo = new UserFileRepository(new UserValidator(), "data/users.txt");
        var friendRepo = new FriendshipFileRepository(new FriendshipValidator(), "data/friendships.txt");
        new ConsoleUI(new NetworkService(userRepo, friendRepo)).run();
    }
}
