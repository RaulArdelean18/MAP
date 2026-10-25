import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import repository.RepositoryConfig;
import repository.RepositoryFactory;
import repository.UsersExistValidator;
import service.CommunityService;
import service.ConnectivityLoader;
import service.LargestCommunityFinder;
import service.NetworkService;
import service.UserIdGenerator;
import structures.Connectivity;
import structures.DynamicConnectivity;
import ui.ConsoleUI;

public class Main {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    public static void main(String[] args) {
        ConsoleUI ui;
        try {
            ui = createUi();
        } catch (RepositoryException | ValidationException | UnsupportedOperationException e) {
            System.err.println("Cannot start the application: " + e.getMessage());
            return;
        }
        ui.run();
    }

    private static ConsoleUI createUi() {
        RepositoryFactory factory = new RepositoryFactory(new RepositoryConfig(CONFIG_FILE));

        Repository<Long, User> userRepo = factory.createUserRepository(new UserValidator());
        Validator<Friendship> friendshipValidator = new UsersExistValidator(new FriendshipValidator(), userRepo);
        Repository<Tuple<Long, Long>, Friendship> friendRepo = factory.createFriendshipRepository(friendshipValidator);

        Connectivity connectivity = new DynamicConnectivity();
        new ConnectivityLoader().load(connectivity, userRepo, friendRepo);

        CommunityService communities = new CommunityService(connectivity, new LargestCommunityFinder());
        NetworkService network = new NetworkService(userRepo, friendRepo, connectivity, new UserIdGenerator(), communities);
        return new ConsoleUI(network);
    }
}