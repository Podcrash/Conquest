package me.raindance.champions;

import me.raindance.champions.db.DatabaseConnection;
import me.raindance.champions.db.TableOrganizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class CreateDatabaseTest {

    @Test
    @DisplayName("Create Databases")
    @Order(1)
    public void create() {
        TableOrganizer.createTables(false);
    }
}
