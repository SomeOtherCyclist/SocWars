package com.soc.database.stats;

import com.soc.SocWars;
import com.soc.database.SqlHelper;

import java.sql.Statement;
import java.util.Iterator;
import java.util.UUID;

import static com.soc.database.SqlHelper.*;

public abstract class BaseTable implements GetFields, TableName {
    protected final UUID player;

    protected BaseTable(UUID player) {
        this.player = player;
    }

    protected BaseTable() {
        this(null);
    }

    public final BaseTable createSqlTable(Statement statement) {
        try {
            statement.execute(this.createSqlTableRequest());
        } catch (Exception e) {
            SocWars.LOGGER.error("Failed to create blank table for {}", this.getTableName());
        }
        return this;
    }

    public final String createSqlTableRequest() {
        final Iterator<String> sqlFields = this.getValidFields(SqlHelper::getSqlFieldNameAndType); //Turn line below into

        final StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" + sqlFields.next() + " primary key");
        sqlFields.forEachRemaining(sqlField -> builder.append(", ").append(sqlField));
        builder.append(")");

        return builder.toString();
    }

    public final BaseTable blankInsert(Statement statement) {
        try {
            statement.execute(this.blankInsertRequest());
        } catch (Exception e) {
            SocWars.LOGGER.error("Failed to create {} insert statement for {}", this.getTableName(), this.player);
        }
        return this;
    }

    public final String blankInsertRequest() {
        Iterator<String> sqlValues = this.getValidFields(field -> getSqlFieldValue(field, this));

        final StringBuilder builder = new StringBuilder(String.format("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM %1$s WHERE player = %2$s
                    ) THEN
                        INSERT INTO %1$s
                        VALUES (\
                """, this.getTableName(), sqlUUID(this.player))
        );
        addAllTokens(builder, sqlValues, ", ");
        builder.append("""
                );
                    END IF;
                END $$
                """);

        return builder.toString();
    }

    public final BaseTable updateSql(Statement statement) {
        try {
            statement.execute(this.updateSqlRequest());
        } catch (Exception e) {
            SocWars.LOGGER.error("Failed to update {} for {}", this.getTableName(), this.player);
        }
        return this;
    }

    public final String updateSqlRequest() {
        final Iterator<String> sqlValues = this.getValidFields(field -> getSqlUpdateValue(field, this));
        sqlValues.next();

        StringBuilder builder = new StringBuilder("UPDATE ").append(this.getTableName()).append("\nSET ");
        addAllTokens(builder, sqlValues, ", ");
        builder.append("\nWHERE player = ").append(sqlUUID(this.player)).append(";");

        return builder.toString();

    }

    public final UUID getPlayer() {
        return this.player;
    }
}
