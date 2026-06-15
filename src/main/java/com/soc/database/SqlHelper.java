package com.soc.database;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class SqlHelper {
    private SqlHelper() {}

    public static Optional<String> getSqlFieldNameAndType(Field field) {
        return getSqlFieldType(field).map(string -> field.getName() + string);
    }

    public static Optional<String> getSqlFieldType(Field field) {
        return switch (field.getType().getCanonicalName()) {
            case "boolean" -> Optional.of(" boolean");
            case "boolean[]" -> Optional.of(" varbit");
            case "int" -> Optional.of(" int");
            case "long" -> Optional.of(" bigint");
            case "short" -> Optional.of(" smallint");
            case "java.lang.String" -> Optional.of(" varchar");
            case "java.util.UUID", "net.minecraft.server.network.ServerPlayerEntity", "net.minecraft.class_3222" -> Optional.of(" uuid");
            default -> Optional.empty();
        };
    }

    public static Optional<String> getSqlFieldValue(Field field, Object obj) {
        field.setAccessible(true);
        try {
            return switch (field.getType().getCanonicalName()) {
                case "boolean" -> Optional.of(String.valueOf(field.getBoolean(obj)));
                case "boolean[]" -> Optional.of(SqlHelper.varbitFromBooleanArr((boolean[])field.get(obj)));
                case "int" -> Optional.of(String.valueOf(field.getInt(obj)));
                case "long" -> Optional.of(String.valueOf(field.getLong(obj)));
                case "short" -> Optional.of(String.valueOf(field.getShort(obj)));
                case "java.lang.String" -> Optional.of((String)field.get(obj));
                case "java.util.UUID" -> Optional.of(sqlUUID((UUID)field.get(obj)));
                default -> Optional.empty();
            };
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> getSqlUpdateValue(Field field, Object obj) {
        return getSqlFieldValue(field, obj).map(value -> String.format("%1$s = %1$s + %2$s", field.getName(), value));
    }

    public static String varbitFromBooleanArr(boolean[] bits) {
        final StringBuilder builder = new StringBuilder("B'");

        for (boolean bool : bits) builder.append(bool ? 1 : 0);

        builder.append('\'');

        return builder.toString();
    }

    public static String sqlUUID(UUID uuid) {
        return String.format("'%s'", uuid.toString().replace("-", ""));
    }

    public static void addAllTokens(StringBuilder builder, Iterator<String> tokens, String separator) {
        builder.append(tokens.next());
        tokens.forEachRemaining(sqlValue -> builder.append(separator).append(sqlValue));
    }
}
