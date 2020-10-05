import javax.xml.transform.Result;
import java.sql.*;
import java.util.Random;
import java.io.File;

public class TietokannanTestaus {

    public static void main(String[] args) throws SQLException {
        // Ensin luodaan yhteys tietokantaan / luodaan tietokanta
        Connection db = DriverManager.getConnection("jdbc:sqlite:Elokuvat.db");
        Random rand = new Random();
        Statement s = db.createStatement();

        System.out.println("**** Tehtävä 3: Tehokkuus ****");
        System.out.println("");

        for (int k = 1; k <= 3; k++) {

            if (k==1) {
                System.out.println("Testi 1:");
                System.out.println("Indeksiä ei luoda.");
            } else if (k==2) {
                System.out.println("Testi 2:");
            } else {
                System.out.println("Testi 3:");
            }

            long timeAtStart = System.currentTimeMillis();

            s.execute("CREATE TABLE Elokuvat (id INTEGER PRIMARY KEY, nimi TEXT, vuosi INTEGER)");
            long timeAfterTableCreation = System.currentTimeMillis();
            System.out.println("Vaihe 1:");
            System.out.println("Taulun luominen vei " + (timeAfterTableCreation-timeAtStart) + " millisekuntia.");
            db.setAutoCommit(false);

            String tietokanta = "Elokuvat.db";
            File file = new File(tietokanta);
            System.out.println("Tietokannan koko on vaiheen 1 jälkeen " + file.length() + " tavua.");

            if (k == 2) {
                System.out.println("Indeksi luotu ennen vaihetta 2");
                s.execute("CREATE INDEX idx_vuosi ON Elokuvat (vuosi)");
                db.commit();
            }

            for (int i = 0; i < 1000000; i++) {
                int randInt = 1900 + rand.nextInt(101);
                int randInt2 = rand.nextInt(1000001);
                String leffa = "Tuulen viemää, osa: " + randInt2;

                s.execute("INSERT INTO Elokuvat (nimi, vuosi) VALUES ('" + leffa + "'," + randInt + ")");
            }
            db.commit();
            db.setAutoCommit(true);

            System.out.println("Vaihe 2:");
            long timeAfterCreation = System.currentTimeMillis();
            long aikaVaihe1 = timeAfterCreation - timeAtStart;
            System.out.println("Miljoonan rivin luominen vei " + aikaVaihe1 + " millisekuntia.");
            System.out.println("Tietokannan koko on vaiheen 2 jälkeen " + file.length() + " tavua.");

            if (k == 3) {
                System.out.println("Indeksi luotu ennen vaihetta 3.");
                s.execute("CREATE INDEX idx_vuosi ON Elokuvat (vuosi)");
            }

            for (int i = 0; i < 1000; i++) {

                int randVuosi = 1900 + rand.nextInt(101);
                ResultSet r = s.executeQuery("SELECT COUNT(*) AS lkm FROM Elokuvat WHERE vuosi = " + randVuosi);

            }

            long timeAfterSelect = System.currentTimeMillis();
            System.out.println("Vaihe 3:");
            System.out.println("Tuhannen kyselyn teko vei " + (timeAfterSelect - timeAfterCreation) + " millisekuntia.");
            System.out.println("Tietokannan koko on vaiheen 3 jälkeen " + file.length() + " tavua.");

            s.execute("DROP TABLE Elokuvat");
            s.executeUpdate("VACUUM");

            System.out.println("Testin "+ k +" kokonaiskesto on " + (timeAfterSelect-timeAtStart) + " millisekuntia.");
            System.out.println("");
        }
    }
}


