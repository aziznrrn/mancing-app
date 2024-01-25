import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static int playerId;
    private static int avatarId;

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:fishing_database.db");
            createTables(connection);

            if (!isSeedingDone(connection)) {
                seedData(connection);
                markSeedingAsDone(connection);
            }

            greeting();

            clearScreen();
            displayPlayers(connection);

            playerId = choosePlayer(connection);

            clearScreen();
            System.out.println("Halo Player, " + getPlayerName(connection, playerId) + "!");

            avatarId = chooseAvatar(connection);
            System.out.println("Anda telah memilih avatar " + getAvatarName(connection, avatarId) + "!");

            while (true) {
                displayMenu(connection);
                int choice = getUserChoice();

                switch (choice) {
                    case 1:
                        showAllBait(connection);
                        break;
                    case 2:
                        showAllFish(connection);
                        break;
                    case 3:
                        showAllAvatar(connection);
                        break;
                    case 4:
                        showAllPonds(connection);
                        break;
                    case 5:
                        fishingMenu(connection, playerId, avatarId);
                        break;
                    case 6:
                        showPlayerFishes(connection);
                        break;
                    case 7:
                        showPlayerFishingLog(connection);
                        break;
                    case 8:
                        hallOfFame(connection);
                        break;
                    case 9:
                        clearScreen();
                        System.out.println("Keluar dari aplikasi. Sampai jumpa!");
            
                        connection.close();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Pilihan tidak valid. Tolong coba kembali.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void greeting() {
        System.out.println("Selamat datang di game memancing!");
        System.out.println();
        System.out.println("O     O          ,");
        System.out.println("  o o         .:/");
        System.out.println("    o      ,,///;,   ,;/");
        System.out.println("      o   o:::::::;;///");
        System.out.println("         >::::::::;;\\\\\\");
        System.out.println("           ''\\\\\\\\\\'\" ';\\");
        System.out.println("              ';\\");
        System.out.println();

        System.out.println("Tekan enter untuk melanjutkan ...");
        scanner.nextLine();
    }

    private static void displayMenu(Connection connection) throws SQLException {
        clearScreen();

        System.out.println("Player: " + getPlayerName(connection, playerId));
        System.out.println("Avatar: " + getAvatarName(connection, avatarId));

        System.out.println("\nMenu:");
        System.out.println("1. Tampilkan daftar");
        System.out.println("2. Tampilkan daftar ikan");
        System.out.println("3. Tampilkan daftar avatar");
        System.out.println("4. Tampilkan daftar kolam");
        System.out.println("5. Mancing");
        System.out.println("6. Tampilkan hasil pancingan");
        System.out.println("7. Log pancingan");
        System.out.println("8. Hall of Fame");
        System.out.println("9. Keluar");
    }

    private static void showPlayerFishes(Connection connection) throws SQLException {
        clearScreen();
  
        System.out.println();
        System.out.println("        _        _   ");
        System.out.println("  _   ><_>     ><_>  ");
        System.out.println("><_>           _     ");
        System.out.println("             ><_>    ");
        System.out.println("       _             ");
        System.out.println("     ><_>            ");
        System.out.println("                     ");
        System.out.println("  _                  ");
        System.out.println("><_>            _    ");
        System.out.println("              ><_>   ");
        System.out.println();
    
        displayPlayerFishes(connection);
        
        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void displayPlayerFishes(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
            "SELECT "
            +"fishes.name, "
            +"fishes.level "
            +"FROM player_fishes "
            +"INNER JOIN fishes ON player_fishes.fish_id = fishes.id "
            +"WHERE player_fishes.player_id = " + playerId);

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada ikan yang ditemukan. Tolong tambahkan ikan ke database.");
        } else {
            System.out.println("Ikan hasil pancingan anda:");
            System.out.println("--------------------------------");
            System.out.println("| Nama Ikan            | Level |");
            System.out.println("|----------------------|-------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-20s | %-5d |\n",
                    resultSet.getString("name"),
                    resultSet.getInt("level")
                );
            }
            System.out.println("--------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static void showPlayerFishingLog(Connection connection) throws SQLException {
        clearScreen();
        System.out.println();
        System.out.println("                         _.'.__");
        System.out.println("                      _.'      .");
        System.out.println("':'.               .''   __ __  .");
        System.out.println("  '.:._          ./  _ ''     \"-'.__");
        System.out.println(".'''-: \"\"\"-._    | .                \"-\"._");
        System.out.println(" '.     .    \"._.'                       \"");
        System.out.println("    '.   \"-.___ .        .'          .  :o'.");
        System.out.println("      |   .----  .      .           .'     (");
        System.out.println("       '|  ----. '   ,.._                _-'");
        System.out.println("        .' .---  |.\"\"  .-:;.. _____.----'");
        System.out.println("        |   .-\"\"\"\"    |      '");
        System.out.println("      .'  _'         .'    _'");
        System.out.println("     |_.-'            '-.'");
        System.out.println();

        displayPlayerFishingLog(connection);

        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void displayPlayerFishingLog(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(
            "SELECT p.id, "
            +"p.status, "
            +"p.timestamp, "
            +"pl.name AS player_name, "
            +"a.name AS avatar_name, "
            +"f.name AS fish_name, "
            +"b.name AS bait_name, "
            +"po.name AS pond_name "
            +"FROM fishing_logs p "
            +"INNER JOIN players pl ON p.player_id = pl.id "
            +"INNER JOIN avatars a ON p.avatar_id = a.id "
            +"LEFT JOIN fishes f ON p.fish_id = f.id "
            +"INNER JOIN baits b ON p.bait_id = b.id "
            +"INNER JOIN ponds po ON p.pond_id = po.id "
            +"WHERE p.player_id = " + playerId + " "
            +"ORDER BY p.timestamp DESC");

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada log pancingan. Tolong tambahkan fishing logs ke database.");
        } else {
            System.out.println("Log Pancingan:");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("| Status  | Player               | Avatar               | Nama Ikan            | Nama Umpan           | Nama Kolam       | Time Stamp          |");
            System.out.println("|---------|----------------------|----------------------|----------------------|----------------------|------------------|---------------------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-7s | %-20s | %-20s | %-20s | %-20s | %-16s | %-17s |\n",
                    resultSet.getString("status"),
                    resultSet.getString("player_name"),
                    resultSet.getString("avatar_name"),
                    resultSet.getString("fish_name"),
                    resultSet.getString("bait_name"),
                    resultSet.getString("pond_name"),
                    resultSet.getString("timestamp")
                );
            }
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static int getUserChoice() {
        int choice = -1;
        while (true) {
            System.out.print("Masukkan pilihan Anda: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                break;
            } else {
                scanner.nextLine();
                System.out.println("Input tidak valid. Tolong masukkan bilangan bulat.");
            }
        }
        return choice;
    }

    private static void showAllBait(Connection connection) throws SQLException {
        clearScreen();

        System.out.println();
        System.out.println(" ,+.      ,=|=.     ,+.       ,-\"-. ");
        System.out.println("((|))    (XXXXX)   //|\\\\     / ,-. \\");
        System.out.println(" )|(      |   |    |||||     |(:::)|");
        System.out.println("((|))     \\   /    \\\\|//     \\ `-' /");
        System.out.println(" `-'       `+'      `+'       `-.-'");
        System.out.println();

        displayBaits(connection);

        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void displayBaits(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM baits");

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada umpan yang ditemulan. Tolong tambahkan umpan ke database.");
        } else {
            System.out.println("Daftar umpan:");
            System.out.println("--------------------------------------");
            System.out.println("| ID  | Nama Umpan           | Level |");
            System.out.println("|-----|----------------------|-------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-3d | %-20s | %-5d |\n",
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("level")
                );
            }
            System.out.println("--------------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static void showAllFish(Connection connection) throws SQLException {
        clearScreen();

        System.out.println();
        System.out.println("               ,--..., ");
        System.out.println("               .''-..' ");
        System.out.println("              /@    `.-: ");
        System.out.println("  ,--..       > )<  ,-.: ");
        System.out.println(" .''-.,'       `..-',:- ");
        System.out.println("/@    `.-:       `-' ");
        System.out.println("> )<  ,-.: ");
        System.out.println(" `..-',`   ");
        System.out.println("   `-'     ");
        System.out.println();
    
        displayFishes(connection);
        
        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void displayFishes(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM fishes");

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada ikan yang ditemukan. Tolong tambahkan ikan ke database.");
        } else {
            System.out.println("Daftar ikan:");
            System.out.println("--------------------------------------");
            System.out.println("| ID  | Nama Ikan            | Level |");
            System.out.println("|-----|----------------------|-------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-3d | %-20s | %-5d |\n",
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("level")
                );
            }
            System.out.println("--------------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static void showAllAvatar(Connection connection) throws SQLException {
        clearScreen();

        System.out.println();
        System.out.println(" ,    ,     /\\   /\\");
        System.out.println("/( /\\ )\\   _\\ \\_/ /_");
        System.out.println("|\\_||_/|  < \\_   _/ >");
        System.out.println("\\______/   \\|0   0|/");
        System.out.println("  _\\/_    _(_  ^  _)_");
        System.out.println(" ( () )  /`\\|V\"\"\"V|/`\\");
        System.out.println("   {}    \\  \\_____/  /");
        System.out.println("   ()    /\\   )=(   /\\");
        System.out.println("   {}   /  \\_/\\=/\\_/  \\");
        System.out.println();
        
        displayAvatars(connection);

        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void showAllPonds(Connection connection) throws SQLException {
        clearScreen();

        System.out.println();
        System.out.println("        .n.                     |");
        System.out.println("       /___\\          _.---.  \\ _ /");
        System.out.println("       [|||]         (_._ ) )--;_) =-");
        System.out.println("       [___]           '---'.__,' \\");
        System.out.println("       }-=-{                    |");
        System.out.println("       |-\" |");
        System.out.println("       |.-\"|                p");
        System.out.println("~^=~^~-|_.-|~^-~^~ ~^~ -^~^~|\\ ~^-~^~-");
        System.out.println("^   .=.| _.|__  ^       ~  /| \\");
        System.out.println(" ~ /:. \\\" _|_/\\    ~      /_|__\\  ^");
        System.out.println(".-/::.  |   |\"\"|-._    ^   ~~~~");
        System.out.println("  `===-'-----'\"\"`  '-.              ~");
        System.out.println();

        displayPonds(connection);

        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void displayPonds(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM ponds");

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada kolam yang ditemukan. Tolong tambahkan ponds ke database.");
        } else {
            System.out.println("Daftar kolam:");
            System.out.println("------------------------------");
            System.out.println("| ID  | Nama Kolam           |");
            System.out.println("|-----|----------------------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-3d | %-20s |\n",
                    resultSet.getInt("id"),
                    resultSet.getString("name")
                );
            }
            System.out.println("------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static void fishingMenu(Connection connection, int playerId, int avatarId) throws SQLException {
        clearScreen();

        System.out.println();
        System.out.println("  ,-.       _,---._ __  / \\");
        System.out.println(" /  )    .-'       `./ /   \\");
        System.out.println("(  (   ,'            `/    /|");
        System.out.println(" \\  `-\"             \\'\\   / |");
        System.out.println("  `.              ,  \\ \\ /  |");
        System.out.println("   /`.          ,'-`----Y   |");
        System.out.println("  (            ;        |   '");
        System.out.println("  |  ,-.    ,-'         |  /");
        System.out.println("  |  | (   |            | /");
        System.out.println("  )  |  \\  `.___________|/");
        System.out.println("  `--'   `--'");
        System.out.println();

        String status = "failed";

        int pondId = -1;
        pondId = choosePond(connection);

        int baitId = -1;
        baitId = chooseBait(connection);

        ArrayList<Integer> fishesId = new ArrayList<Integer>();

        float fishProbability = randomFishProbability();
        
        int fishId = -1;

        if (fishProbability > 0.5) {
            fishesId = getFishesIdInPondAndSameLevelWithBait(connection, pondId, baitId);

            fishId = randomFishId(fishesId);

            System.out.println("Anda mendapatkan " + getFishName(connection, fishId) + "!");
            System.out.println("Tekan enter untuk menarik ikan ...");
            scanner.nextLine();
            scanner.nextLine();

            fishProbability = randomFishProbability();

            if (fishProbability > 0.5) {
                status = "success";

                System.out.println("Anda berhasil mendapatkan ikan!");

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_fishes (player_id, fish_id) VALUES (?, ?)");
                preparedStatement.setInt(1, playerId);
                preparedStatement.setInt(2, fishId);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                preparedStatement = connection.prepareStatement("UPDATE players SET exp = exp + 1 WHERE id = ?");
                preparedStatement.setInt(1, playerId);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                System.out.println("Ikan berhasil ditambahkan ke daftar ikan pancingan!");
            } else {
                System.out.println("Ikan gagal diangkat!");
            }
        } else {
            System.out.println("Anda tidak mendapatkan ikan sama sekali!");
        }
        
        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();

        if (fishId == -1) {
            fishId = 0;
        }

        PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO fishing_logs (status, player_id, avatar_id, fish_id, bait_id, pond_id, timestamp) "
            +"VALUES (?, ?, ?, ?, ?, ?, datetime('now'))"
        );
        preparedStatement.setString(1, status);
        preparedStatement.setInt(2, playerId);
        preparedStatement.setInt(3, avatarId);
        preparedStatement.setInt(4, fishId);
        preparedStatement.setInt(5, baitId);
        preparedStatement.setInt(6, pondId);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private static float randomFishProbability() {
        return (float) Math.random();
    }

    private static int randomFishId(ArrayList<Integer> fishesId) {
        return fishesId.get((int) (Math.random() * fishesId.size()));
    }

    private static int choosePond(Connection connection) throws SQLException {
        displayPonds(connection);

        int pondId = -1;
        while (true) {
            System.out.print("Masukkan ID kolam: ");

            if (scanner.hasNextInt()) {
                pondId = scanner.nextInt();

                if (isValidPondId(connection, pondId)) {
                    break;
                } else {
                    System.out.println("ID kolam tidak valid. Tolong coba kembali.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Input tidak valid. Tolong masukkan bilangan bulat.");
            }
        }

        return pondId;
    }

    private static boolean isValidPondId(Connection connection, int pondId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM ponds WHERE id = ?");
        preparedStatement.setInt(1, pondId);
        ResultSet resultSet = preparedStatement.executeQuery();
        int count = resultSet.getInt(1);
        resultSet.close();
        preparedStatement.close();

        return count > 0;
    }

    private static int chooseBait(Connection connection) throws SQLException {
        displayBaits(connection);

        int baitId = -1;
        while (true) {
            System.out.print("Masukkan ID umpan: ");

            if (scanner.hasNextInt()) {
                baitId = scanner.nextInt();

                if (isValidBaitId(connection, baitId)) {
                    break;
                } else {
                    System.out.println("ID umpan tidak valid. Tolong coba kembali.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Input tidak valid. Tolong masukkan bilangan bulat.");
            }
        }

        return baitId;
    }

    private static boolean isValidBaitId(Connection connection, int baitId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM baits WHERE id = ?");
        preparedStatement.setInt(1, baitId);
        ResultSet resultSet = preparedStatement.executeQuery();
        int count = resultSet.getInt(1);
        resultSet.close();
        preparedStatement.close();

        return count > 0;
    }

    private static ArrayList<Integer> getFishesIdInPondAndSameLevelWithBait(Connection connection, int pondId, int baitId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
            "SELECT fishes.id "
            +"FROM fishes "
            +"INNER JOIN pond_fishes ON fishes.id = pond_fishes.fish_id "
            +"INNER JOIN baits ON baits.level = fishes.level "
            +"WHERE pond_fishes.pond_id = ? AND baits.id = ?"
        );
        preparedStatement.setInt(1, pondId);
        preparedStatement.setInt(2, baitId);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Integer> fishesId = new ArrayList<Integer>();
        while (resultSet.next()) {
            fishesId.add(resultSet.getInt("id"));
        }

        resultSet.close();
        preparedStatement.close();

        return fishesId;
    }

    private static String getFishName(Connection connection, int fishId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM fishes WHERE id = ?");
        preparedStatement.setInt(1, fishId);
        ResultSet resultSet = preparedStatement.executeQuery();
        String fishName = resultSet.getString(1);
        resultSet.close();
        preparedStatement.close();

        return fishName;
    }

    private static void hallOfFame(Connection connection) throws SQLException {
        clearScreen();

        System.out.println();
        System.out.println(" _________         .    .");
        System.out.println("(..       \\_    ,  |\\  /|");
        System.out.println(" \\       O  \\  /|  \\ \\/ /");
        System.out.println("  \\______    \\/ |   \\  / ");
        System.out.println("     vvvv\\    \\ |   /  |");
        System.out.println("     \\^^^^  ==   \\_/   |");
        System.out.println("      `\\_   ===    \\.  |");
        System.out.println("      / /\\_   \\ /      |");
        System.out.println("      |/   \\_  \\|      /");
        System.out.println("             \\________/");
        System.out.println();

        displayHallOfFame(connection);

        System.out.println("Tekan enter untuk keluar menu ...");
        scanner.nextLine();
        scanner.nextLine();
    }

    private static void displayHallOfFame(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
            "SELECT players.name, players.exp "
            +"FROM players "
            +"ORDER BY players.exp DESC "
            +"LIMIT 10"
        );

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada pemain yang ditemukan. Tolong buat player baru.");
        } else {
            System.out.println("Hall of Fame:");
            System.out.println("--------------------------------");
            System.out.println("| Nama                 | Exp   |");
            System.out.println("|----------------------|-------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-20s | %-5d |\n",
                    resultSet.getString("name"),
                    resultSet.getInt("exp")
                );
            }
            System.out.println("--------------------------------");
        }

        resultSet.close();
        preparedStatement.close();
    }

    private static int chooseAvatar(Connection connection) throws SQLException {
        displayAvatars(connection);

        int avatarId = -1;
        while (true) {
            System.out.print("Masukkan ID Avatar: ");

            if (scanner.hasNextInt()) {
                avatarId = scanner.nextInt();

                if (isValidAvatarId(connection, avatarId)) {
                    break;
                } else {
                    System.out.println("ID avatar tidak valid. Tolong coba kembali.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Input tidak valid. Tolong masukkan bilangan bulat.");
            }
        }

        return avatarId;
    }

    private static String getAvatarName(Connection connection, int avatarId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM avatars WHERE id = ?");
        preparedStatement.setInt(1, avatarId);
        ResultSet resultSet = preparedStatement.executeQuery();
        String avatarName = resultSet.getString(1);
        resultSet.close();
        preparedStatement.close();

        return avatarName;
    }

    private static void displayAvatars(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM avatars");

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada avatar yang ditemukan. Tolong tambahkan avatars ke database.");
        } else {
            System.out.println("Daftar avatar:");
            System.out.println("------------------------------");
            System.out.println("| ID  | Nama Avatar          |");
            System.out.println("|-----|----------------------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-3d | %-20s |\n",
                    resultSet.getInt("id"),
                    resultSet.getString("name")
                );
            }
            System.out.println("------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static boolean isValidAvatarId(Connection connection, int avatarId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM avatars WHERE id = ?");
        preparedStatement.setInt(1, avatarId);
        ResultSet resultSet = preparedStatement.executeQuery();
        int count = resultSet.getInt(1);
        resultSet.close();
        preparedStatement.close();

        return count > 0;
    }

    private static void displayPlayers(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM players");

        if (!resultSet.isBeforeFirst()) {
            System.out.println("Tidak ada player ditemukan. Tolong buat player baru.");
        } else {
            System.out.println("Available Players:");
            System.out.println("--------------------------------------");
            System.out.println("| ID  | Nama                 | Exp   |");
            System.out.println("|-----|----------------------|-------|");
            while (resultSet.next()) {
                System.out.printf(
                    "| %-3d | %-20s | %-5d |\n",
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("exp")
                );
            }
            System.out.println("--------------------------------------");
        }

        resultSet.close();
        statement.close();
    }

    private static int choosePlayer(Connection connection) throws SQLException {
        while (true) {
            System.out.print("Masukkan ID player (atau 0 untuk membuat player baru): ");

            if (scanner.hasNextInt()) {
                int playerId = scanner.nextInt();

                if (playerId == 0) {
                    scanner.nextLine();
                    System.out.print("Masukkan nama player: ");
                    String playerName = scanner.nextLine();
                    insertNewPlayer(connection, playerName);
                    System.out.println("Player baru berhasil dibuat!");
                    displayPlayers(connection);
                } else {
                    if (isValidPlayerId(connection, playerId)) {
                        return playerId;
                    } else {
                        System.out.println("ID player tidak valid. Tolong coba kembali.");
                    }
                }
            } else {
                scanner.nextLine();
                System.out.println("Input tidak valid. Tolong masukkan bilangan bulat.");
            }
        }
    }

    private static String getPlayerName(Connection connection, int playerId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM players WHERE id = ?");
        preparedStatement.setInt(1, playerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        String playerName = resultSet.getString(1);
        resultSet.close();
        preparedStatement.close();

        return playerName;
    }
    

    private static void insertNewPlayer(Connection connection, String playerName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (name) VALUES (?)");
        preparedStatement.setString(1, playerName);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private static boolean isValidPlayerId(Connection connection, int playerId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM players WHERE id = ?");
        preparedStatement.setInt(1, playerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        int count = resultSet.getInt(1);
        resultSet.close();
        preparedStatement.close();

        return count > 0;
    }

    private static boolean isSeedingDone(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM seeder_status");

        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            resultSet.close();
            statement.close();
            return count > 0;
        } else {
            resultSet.close();
            statement.close();
            return false;
        }
    }

    private static void markSeedingAsDone(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO seeder_status VALUES (1)");
        statement.close();
    }

    private static void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute(
            "CREATE TABLE IF NOT EXISTS players "
            +"("
                +"id INTEGER PRIMARY KEY, "
                +"name TEXT UNIQUE, "
                +"exp integer DEFAULT 0"
            +")"
        );
        
        statement.execute(
            "CREATE TABLE IF NOT EXISTS avatars "
            +"("
                +"id INTEGER PRIMARY KEY, "
                +"name TEXT UNIQUE, "
                +"exp_needed INTEGER DEFAULT 0"
            +")"
        );

        statement.execute(
            "CREATE TABLE IF NOT EXISTS player_avatars "
            +"("
                +"player_id INTEGER, "
                +"avatar_id INTEGER, "
                +"PRIMARY KEY (player_id, avatar_id), "
                +"FOREIGN KEY (player_id) REFERENCES players(id), "
                +"FOREIGN KEY (avatar_id) REFERENCES avatars(id)"
            +")"
        );

        statement.execute(
            "CREATE TABLE IF NOT EXISTS fishes "
            +"("
                +"id INTEGER PRIMARY KEY, "
                +"name TEXT UNIQUE, "
                +"level INTEGER DEFAULT 1"
            +")"
        );

        statement.execute(
            "CREATE TABLE IF NOT EXISTS baits "
            +"("
                +"id INTEGER PRIMARY KEY, "
                +"name TEXT UNIQUE, "
                +"level INTEGER DEFAULT 1"
            +")"
        );

        statement.execute(
            "CREATE TABLE IF NOT EXISTS player_fishes "
            +"("
                +"player_id INTEGER, "
                +"fish_id INTEGER, "
                +"PRIMARY KEY (player_id, fish_id), "
                +"FOREIGN KEY (player_id) REFERENCES players(id), "
                +"FOREIGN KEY (fish_id) REFERENCES fishes(id)"
            +")"
        );

        statement.execute(
            "CREATE TABLE IF NOT EXISTS fishing_logs "
            +"("
                +"id INTEGER PRIMARY KEY, "
                +"status TEXT, "
                +"player_id INTEGER, "
                +"avatar_id INTEGER, "
                +"fish_id INTEGER, "
                +"bait_id INTEGER, "
                +"pond_id INTEGER, "
                +"timestamp DATETIME, "
                +"FOREIGN KEY (player_id) REFERENCES players(id), "
                +"FOREIGN KEY (avatar_id) REFERENCES avatars(id), "
                +"FOREIGN KEY (fish_id) REFERENCES fishes(id), "
                +"FOREIGN KEY (bait_id) REFERENCES baits(id), "
                +"FOREIGN KEY (pond_id) REFERENCES ponds(id)"
            +")"
        );

        statement.execute("CREATE TABLE IF NOT EXISTS ponds "
            +"("
                +"id INTEGER PRIMARY KEY, "
                +"name TEXT UNIQUE"
            +")");

        statement.execute(
            "CREATE TABLE IF NOT EXISTS pond_fishes "
            +"("
                +"pond_id INTEGER, "
                +"fish_id INTEGER, "
                +"PRIMARY KEY (pond_id, fish_id), "
                +"FOREIGN KEY (pond_id) REFERENCES ponds(id), "
                +"FOREIGN KEY (fish_id) REFERENCES fishes(id)"
            +")"
        );

        statement.execute(
            "CREATE TABLE IF NOT EXISTS seeder_status "
            +"("
                +"id INTEGER PRIMARY KEY"
            +")"
        );

        statement.close();
    }

    private static void seedData(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute(
            "INSERT INTO baits (name) VALUES "
            +"('Cacing'), ('Ulat'), ('Jangkrik'), ('Belalang'), ('Udang'), ('Kodok'), ('Ayam')"
        );

        statement.execute(
            "INSERT INTO fishes (name) VALUES "
            +"('Lele Megalodon'),('Lele'),('Gurame'),('Wader'),('Betik'),('Sepat'),('Seluang'),"
            +"('Nila'),('Mas'),('Koi'),('Koki'),('Bandeng'),('Teri'),('Belut'),('Sidat'),('Bawal'),"
            +"('Bilis'),('Cupang'),('Guppy'),('Mujair'),('Sapu-sapu'),('Gabus'),('Patin')"
        );

        statement.execute(
            "INSERT INTO avatars (name) VALUES "
            +"('Lumut Wizard'),('Kangkung Ranger'),('Sepat Paladin'),('Yuyu Mage'),('Wader Warrior')"
        );

        statement.execute(
            "INSERT INTO ponds (name) VALUES "
            +"('Kolam Mbah Wardi'),('Kolam Mang Juli'),('Kolam Pak Wahono')"
        );

        statement.execute("INSERT INTO pond_fishes (pond_id, fish_id) SELECT 1, id FROM fishes");
        statement.execute("INSERT INTO pond_fishes (pond_id, fish_id) SELECT 2, id FROM fishes WHERE id % 2 = 1");
        statement.execute("INSERT INTO pond_fishes (pond_id, fish_id) SELECT 3, id FROM fishes WHERE id % 2 = 0");

        statement.close();
    }
}
