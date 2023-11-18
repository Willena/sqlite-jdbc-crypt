package org.sqlite.mc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.sqlite.SQLiteException;

@DisabledIfSystemProperty(
        disabledReason = "SQLite3 binary not compatible with that test",
        named = "disableCipherTests",
        matches = "true")
public class SQLiteMCURIInterfaceTest {

    private static final String SQL_TABLE =
            "CREATE TABLE IF NOT EXISTS warehouses ("
                    + "	id integer PRIMARY KEY,"
                    + "	name text NOT NULL,"
                    + "	capacity real"
                    + ");";

    public String createFile() throws IOException {
        File tmpFile = File.createTempFile("tmp-sqlite", ".db");
        tmpFile.deleteOnExit();
        return tmpFile.getAbsolutePath();
    }

    public boolean databaseIsReadable(Connection connection) {
        if (connection == null) return false;
        try {
            Statement st = connection.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT count(*) as nb FROM sqlite_master");
            resultSet.next();
            // System.out.println("The out is : " + resultSet.getString("nb"));
            // "When reading the database, the result should contain the number 1");
            assertThat(resultSet.getString("nb")).isEqualTo("1");

            return true;
        } catch (SQLException e) {
            // System.out.println(e.getMessage());
            return false;
        }
    }

    public void applySchema(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(SQL_TABLE);
    }

    public void plainDatabaseCreate(String dbPath) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:" + dbPath);
        applySchema(conn);
        conn.close();
    }

    public void cipherDatabaseCreate(String dbPath, String key) throws SQLException {
        Connection connection =
                DriverManager.getConnection(
                        "jdbc:sqlite:file:"
                                + dbPath
                                + "?cipher=sqlcipher&legacy=1&kdf_iter=4000&key="
                                + key);
        applySchema(connection);
        connection.close();
    }

    public Connection plainDatabaseOpen(String dbPath) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:file:" + dbPath);
    }

    @Test
    public void plainDatabaseTest() throws IOException, SQLException {
        String path = createFile();
        // 1.  Open + Write
        plainDatabaseCreate(path);

        // 2. Ensure another Connection can read the databse written
        Connection c = plainDatabaseOpen(path);
        // "The plain database should be always readable");
        assertThat(databaseIsReadable(c)).isTrue();
        c.close();
    }

    public Connection cipherDatabaseOpen(String dbPath, String key) throws SQLException {
        try {
            return DriverManager.getConnection(
                    "jdbc:sqlite:file:"
                            + dbPath
                            + "?cipher=sqlcipher&legacy=1&kdf_iter=4000&key="
                            + key);
        } catch (SQLiteException e) {
            return null;
        }
    }

    public void genericDatabaseTest(SQLiteMCConfig.Builder config)
            throws IOException, SQLException {
        String path = createFile();
        // 1. Open + Write + cipher with "Key1" key
        String Key1 = "Key1";
        String Key2 = "Key2";

        cipherDatabaseCreate(path, Key1);

        // 2. Ensure db is readable with good Password
        Connection c = cipherDatabaseOpen(path, Key1);
        // String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        config.getClass().getSimpleName(), Key1)
        assertThat(databaseIsReadable(c)).isTrue();
        c.close();

        // 3. Ensure db is not readable without the good password (Using Key2 as password)
        c = cipherDatabaseOpen(path, Key2);
        assertThat(c).isNull();
        //        assertNull(
        //                c,
        //                String.format(
        //                        "2 Be sure the database with config %s cannot be read with the key
        // '%s' (good key is %s)",
        //                        config.getClass().getSimpleName(), Key2, Key1));

        // 4. Rekey the database
        c = cipherDatabaseOpen(path, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //        assertTrue(
        //                databaseIsReadable(c),
        //                String.format(
        //                        "3. Be sure the database with config %s can be read before
        // rekeying with the key '%s' (replacing %s with %s)",
        //                        config.getClass().getSimpleName(), Key2, Key1, Key2));
        c.createStatement().execute(String.format("PRAGMA rekey=%s", Key2));
        assertThat(databaseIsReadable(c)).isTrue();
        //        assertTrue(
        //                databaseIsReadable(c), "4. Be sure the database is still readable after
        // rekeying");
        c.close();

        // 5. Should now be readable with Key2
        c = cipherDatabaseOpen(path, Key2);
        assertThat(databaseIsReadable(c)).isTrue();
        //        assertTrue(
        //                databaseIsReadable(c),
        //                String.format(
        //                        "5. Should now be able to open the database with config %s and the
        // new key '%s'",
        //                        config.getClass().getSimpleName(), Key2));
        c.close();
    }

    @Test
    public void sqlCipherDatabaseHexKeyTest() throws SQLException, IOException {

        String dbfile = createFile();
        String Key1 = "x'54686973206973206D792076657279207365637265742070617373776F72642E'";
        String Key2 = "x'66086973206973206D792076657279207365637265742070617373776F72642E'";
        cipherDatabaseCreate(dbfile, Key1);

        // 2. Ensure db is readable with good Password
        Connection c = cipherDatabaseOpen(dbfile, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //        assertTrue(
        //                databaseIsReadable(c),
        //                String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        "SQLCipher", Key1));
        c.close();

        // 3. Ensure not readable with wrong key
        Connection c2 = cipherDatabaseOpen(dbfile, Key2);
        assertThat(databaseIsReadable(c2)).isFalse();
        //        assertFalse(
        //                databaseIsReadable(c2),
        //                String.format(
        //                        "2. Be sure the database with config %s cannot be read with the
        // key '%s'",
        //                        "SQLCipher", Key2));
        c.close();
    }

    @Test
    public void sqlCipherDatabaseSpecialKeyTest() throws SQLException, IOException {
        String dbfile = createFile();
        String Key1 = URLEncoder.encode("Key2&2ax", "utf8");
        String Key2 = "Key2&2ax";
        cipherDatabaseCreate(dbfile, Key1);

        // 2. Ensure db is readable with Key1 Password URL access
        Connection c = cipherDatabaseOpen(dbfile, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //                ,
        //                String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        "SQLCipher", Key1));
        c.close();

        // 3. Make sure we can read the database using the SQL interface
        c =
                new SQLiteMCSqlCipherConfig()
                        .setLegacy(1)
                        .setKdfIter(4000)
                        .withKey(Key2)
                        .build()
                        .createConnection("jdbc:sqlite:file:" + dbfile);
        assertThat(databaseIsReadable(c)).isTrue();
        //                ,
        //                "2. Be sure the database is readable using PRAGMA method and key
        // containing special characters");
        c.close();

        c =
                new SQLiteMCSqlCipherConfig()
                        .setLegacy(1)
                        .setKdfIter(4000)
                        .withKey(Key2)
                        .useSQLInterface(true)
                        .build()
                        .createConnection("jdbc:sqlite:file:" + dbfile);
        assertThat(databaseIsReadable(c)).isTrue();
        //        ,
        //                "3. Be sure the database is readable using SQL method and key containing
        // special characters");
        c.close();
    }

    @Test
    public void sqlCipherDatabaseTest() throws IOException, SQLException {
        genericDatabaseTest(SQLiteMCSqlCipherConfig.getDefault());
    }
}
