package org.sqlite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.sql.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

public class ParametersTest {

    @Test
    @DisabledIfSystemProperty(
            disabledReason = "SQLite3 binary not compatible with that test",
            named = "disableCipherTests",
            matches = "true")
    public void testSqliteConfigViaStatements() throws Throwable {
        File testDB = File.createTempFile("test.db", "", new File("target"));
        testDB.deleteOnExit();

        String uri = "jdbc:sqlite:file:" + testDB + "?cache=shared";
        try (Connection connection = DriverManager.getConnection(uri)) {
            try (Statement stat = connection.createStatement()) {
                stat.execute("SELECT sqlite3mc_config('cipher', 'sqlcipher');");
                stat.execute("SELECT sqlite3mc_config('sqlcipher', 'legacy', 4);");
                stat.execute("PRAGMA key='a';");
                stat.execute("select 1 from sqlite_master");

                stat.execute("PRAGMA busy_timeout = 1800000;");
                stat.execute("PRAGMA auto_vacuum = incremental;");
                stat.execute("PRAGMA journal_mode = truncate;");
                stat.execute("PRAGMA synchronous = full;");
                stat.execute("PRAGMA cache_size = -65536;");

                checkPragma(stat, "busy_timeout", "1800000");
                checkPragma(stat, "auto_vacuum", "2");
                checkPragma(stat, "journal_mode", "truncate");
                checkPragma(stat, "synchronous", "2");
                checkPragma(stat, "cache_size", "-65536");
                assertThat(
                                ((SQLiteConnection) stat.getConnection())
                                        .getDatabase()
                                        .getConfig()
                                        .isEnabledSharedCache())
                        .isFalse();
                assertThat(
                                ((SQLiteConnection) stat.getConnection())
                                        .getDatabase()
                                        .getConfig()
                                        .isEnabledSharedCacheConnection())
                        .isTrue();
            }
        }
    }

    @Test
    public void testSqliteConfigViaURI() throws Throwable {
        File testDB = File.createTempFile("test.db", "", new File("target"));
        testDB.deleteOnExit();

        String uri =
                "jdbc:sqlite:file:"
                        + testDB
                        + "?cache=private&busy_timeout=1800000&auto_vacuum=2&journal_mode=truncate&synchronous=full&cache_size=-65536";
        try (Connection connection = DriverManager.getConnection(uri)) {
            try (Statement stat = connection.createStatement()) {
                stat.execute("select 1 from sqlite_master");

                checkPragma(stat, "busy_timeout", "1800000");
                checkPragma(stat, "auto_vacuum", "2");
                checkPragma(stat, "journal_mode", "truncate");
                checkPragma(stat, "synchronous", "2");
                checkPragma(stat, "cache_size", "-65536");
                assertThat(
                                ((SQLiteConnection) stat.getConnection())
                                        .getDatabase()
                                        .getConfig()
                                        .isEnabledSharedCache())
                        .isFalse();
                assertThat(
                                ((SQLiteConnection) stat.getConnection())
                                        .getDatabase()
                                        .getConfig()
                                        .isEnabledSharedCacheConnection())
                        .isFalse();
            }
        }
    }

    private void checkPragma(Statement stat, String key, String expectedValue) throws SQLException {
        try (ResultSet resultSet = stat.executeQuery("pragma " + key + ";")) {
            resultSet.next();
            String value = resultSet.getString(1);
            assertThat(value).isEqualTo(expectedValue);
        }
    }
}
