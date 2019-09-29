package me.raindance.champions.db;

//static imports are recommended to make the code look cleaner

import nu.studer.sample.Tables;
import nu.studer.sample.tables.Kits;
import org.bukkit.entity.Player;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.UUID;

public class ChampionsKitTable extends BaseTable implements IPlayerDB {
    private PlayerTable players;
    private final Kits KITS;
    public ChampionsKitTable(boolean test) {
        super("kits", test);
        this.KITS = Tables.KITS.rename(getName());
        this.players = TableOrganizer.getTable(DataTableType.PLAYERS, test);
    }

    @Override
    public PlayerTable getPlayerTable() {
        return players;
    }

    @Override
    public void createTable() {
        DSLContext create = getContext();
        CreateTableColumnStep step = create.createTableIfNotExists(getName())
                //playeruuids are exactly 36
                .column("player_id", SQLDataType.BIGINT)
                /**
                 * TODO
                 * We might to change to just use class IDs so that it's faster
                 * We should!
                 * {@link me.raindance.champions.kits.enums.SkillType}
                 */
                .column("class", SQLDataType.VARCHAR(16))
                .column("build_id", SQLDataType.INTEGER)
                .column("build_info", SQLDataType.VARCHAR(128));

        step.constraint(DSL.constraint(getConstraintPrefix() + "foreign_player_id")
            .foreignKey("player_id")
            .references(players.getName(), "_id"));
        step.constraint(DSL.constraint(getConstraintPrefix() + "primary_player_id").primaryKey("player_id"));
        step.constraint(DSL.constraint(getConstraintPrefix() + "player_unique").unique("player_id", "class", "build_id"));
        step.execute();
    }


    public String getJSONData(UUID uuid, String clasz, int build_id) {
        //TODO: Find out if this works
        return getContext().select(KITS.BUILD_INFO)
            .from(KITS)
            .where(
                KITS.PLAYER_ID.eq(getID(uuid)),
                KITS.CLASS.eq(clasz),
                KITS.BUILD_ID.eq(build_id))
            .fetchOneInto(String.class);
    }

    public void set(UUID uuid, String clasz, int build_id, String data) {
        // Find from (spotify recommended is fire Aujourd’hui à 21:45: inventory/InvFactory (look around for editClose))
        // int build_id = ?;

        DSLContext create = getContext();

        create.insertInto(KITS,
                KITS.CLASS, KITS.PLAYER_ID, KITS.BUILD_ID, KITS.BUILD_INFO)
                // Use the ON CONFLICT ON CONSTRAINT player_unique to update the values instead of inserting.
                .values(clasz, getID(uuid), build_id, data)
                .execute();

        // build_id domain size must be equal to 5
        // it should only be between 0-5 or 1-5
    }
    public void delete(UUID uuid, String clasz, int build_id) {
        DSLContext delete = getContext();
        delete.delete(KITS)
            .where(
                KITS.PLAYER_ID.eq(getID(uuid)),
                KITS.BUILD_ID.eq(build_id),
                KITS.CLASS.eq(clasz))
            .execute();
    }

    public int size() {
        return getContext().fetchCount(KITS);
    }
}
