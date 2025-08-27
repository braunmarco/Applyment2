import my.cvmanager.domain.Position;
import my.cvmanager.domain.Technology;
import my.cvmanager.domain.UserProfile;
import org.junit.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

public class DaoTest {
    Logger logger = Logger.getLogger(DaoTest.class.getName());

    @Test
    public void testCreateUser() {

        // create Position
        Position position = new Position();
        // title
        position.setTitle("Software Engineer");
        // company
        position.setCompany("DAT");
        // location
        position.setLocation("Stuttgart");
        // start date
        position.setStartDate(LocalDate.now());
        // end date
        position.setEndDate(LocalDate.now().plusDays(1));
        // description
        position.setDescription("Ganz gut, Leute waren ganz okay");

        Position position2 = new Position();
        position2.setTitle("Software Engineer");
        position2.setCompany("Diehl Metering");
        position2.setLocation("Nürnberg");
        position2.setStartDate(LocalDate.now().minusYears(1));
        position2.setEndDate(LocalDate.now().minusYears(1).plusDays(1));
        position2.setDescription("Ganz gut, Leute waren ganz okay");

        // create Technology
        Technology technology = new Technology();
        // name
        technology.setName("Java");
        // level
        technology.setLevel(Technology.Level.AAA.code());
        Technology technology2 = new Technology();
        // name
        technology2.setName("Java");
        // level
        technology2.setLevel(Technology.Level.AAA.code());

        // create User
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("Marco");
        userProfile.setLastName("Braun");
        userProfile.setEmail("braun_marco@gmx.de");
        userProfile.setPhone("+49 1755944513");
        userProfile.setLocation("Turmstrassed 63, 36124 Eichenzell");
        userProfile.setSummary("Test abcd");

        // add technology to position
        position.addTechnology(technology);
        position.addTechnology(technology2);
        // add position to user
        userProfile.addPosition(position);
        userProfile.addPosition(position2);

        //UserServ service = new BaseDao<>();
        //UserService userService = new BaseDao<>();
        //service.persist(userProfile);

        logger.info(userProfile.toString());
    }
}
