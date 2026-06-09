package com.soc.database;

import com.soc.SocWars;
import com.soc.database.stats.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

public final class Database {
    private Database() {}

    private static final Connection CONNECTION;
    private static final Statement STATEMENT;

    public static Optional<Statement> getStatement() {
        return Optional.ofNullable(STATEMENT);
    }

    public static boolean isConnected() {
        return STATEMENT != null;
    }

    static {
        Connection connection;
        Statement statement;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgrespassword");
            statement = connection.createStatement();

            SocWars.LOGGER.info("Database successfully connected!");
        } catch (Exception e) {
            connection = null;
            statement = null;

            SocWars.LOGGER.error("Failed to connect to database\n{}", e.getMessage());
        }
        CONNECTION = connection;
        STATEMENT = statement;
    }

    public static void initialise() {
        new LobbyTable().createSqlTable(STATEMENT);
        new SkywarsTable().createSqlTable(STATEMENT);
        new BedwarsTable().createSqlTable(STATEMENT);
        new HideAndSeekTable().createSqlTable(STATEMENT);
        new PropHuntTable().createSqlTable(STATEMENT);
        new DuelsTable().createSqlTable(STATEMENT);

        ServerPlayerEvents.JOIN.register(player -> {
            if (player.getPermissionLevel() >= 2) {
                final Text status = isConnected() ? Text.translatable("database.status.connected").formatted(Formatting.DARK_GREEN) : Text.translatable("database.status.disconnected").formatted(Formatting.RED);
                player.sendMessage(Text.translatable("database.status.op_message", status).formatted(Formatting.GOLD), false);
            }

            new LobbyTable(player.getUuid()).blankInsert(STATEMENT);
            new SkywarsTable(player.getUuid()).blankInsert(STATEMENT);
            new BedwarsTable(player.getUuid()).blankInsert(STATEMENT);
            new HideAndSeekTable(player.getUuid()).blankInsert(STATEMENT);
            new PropHuntTable(player.getUuid()).blankInsert(STATEMENT);
            new DuelsTable(player.getUuid()).blankInsert(STATEMENT);
        });
    }
}
