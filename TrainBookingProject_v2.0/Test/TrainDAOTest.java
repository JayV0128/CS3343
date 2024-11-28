package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import DataModel.*;
import DB_init.Database;
import DAO.*;
import java.util.ArrayList;

class TrainDAOTest {

    private TrainDAO trainDAO;
    private Train testTrain;
    private Database dbInstance;

    @BeforeEach
    void setUp() {
        dbInstance = Database.getInstance();
        trainDAO = new TrainDAO();
        // Clear and initialize the train table
        dbInstance.getTable_train().clear();
        testTrain = new Train("T123", "CityA", "CityB", "2023-12-25", "10:00", 100, 50.0);
        trainDAO.addTrain_fromTrainTable(testTrain);
    }

    @Test
    void testGetTrainByNumber() {
        Train train = trainDAO.getTrain_fromTrainTable("T123");
        assertNotNull(train, "Train should exist");
        assertEquals("T123", train.getTrainNumber(), "Train numbers should match");
    }

    @Test
    void testAddTrain() {
        Train newTrain = new Train("T456", "CityX", "CityY", "2023-12-26", "12:00", 80, 60.0);
        boolean isAdded = trainDAO.addTrain_fromTrainTable(newTrain);
        assertTrue(isAdded, "Train should be successfully added");
        assertEquals(2, trainDAO.getTable_train().size(), "Train table size should be 2");
    }

    @Test
    void testUpdateTrain() {
        testTrain.setPrice(55.0);
        boolean isUpdated = trainDAO.updateTrain_fromTrainTable(testTrain);
        assertTrue(isUpdated, "Train should be successfully updated");
        Train updatedTrain = trainDAO.getTrain_fromTrainTable("T123");
        assertEquals(55.0, updatedTrain.getPrice(), "Train price should be updated");
    }

    @Test
    void testDeleteTrain() {
        boolean isDeleted = trainDAO.deleteTrain_fromTrainTable("T123");
        assertTrue(isDeleted, "Train should be successfully deleted");
        Train deletedTrain = trainDAO.getTrain_fromTrainTable("T123");
        assertNull(deletedTrain, "Deleted train should not exist");
    }

    @AfterEach
    void tearDown() {
        // Reset the database
        dbInstance.getTable_train().clear();
        dbInstance.resetDB();
    }
}