package org.sqlite.mc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.sqlite.SQLiteException;

@DisabledIfSystemProperty(
        disabledReason = "SQLite3 binary not compatible with that test",
        named = "disableCipherTests",
        matches = "true")
public class SQLiteMCSQLInterfaceTest {

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
            assertThat(resultSet.getString("nb")).isEqualTo("1");
            //                    "1",
            //                    ,
            //                    "When reading the database, the result should contain the number
            // 1");
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

    public void cipherDatabaseCreate(SQLiteMCConfig.Builder config, String dbPath, String key)
            throws SQLException {
        Connection connection =
                config.withKey(key)
                        .useSQLInterface(true)
                        .build()
                        .createConnection("jdbc:sqlite:file:" + dbPath);
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
        assertThat(databaseIsReadable(c))
                .isTrue(); // , "The plain database should be always readable");
        c.close();
    }

    public Connection cipherDatabaseOpen(SQLiteMCConfig.Builder config, String dbPath, String key)
            throws SQLException {
        try {
            return config.withKey(key)
                    .useSQLInterface(true)
                    .build()
                    .createConnection("jdbc:sqlite:file:" + dbPath);
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

        cipherDatabaseCreate(config, path, Key1);

        // 2. Ensure db is readable with good Password
        Connection c = cipherDatabaseOpen(config, path, Key1);

        assertThat(databaseIsReadable(c)).isTrue(); // ,
        //                String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        config.getClass().getSimpleName(), Key1));
        c.close();

        // 3. Ensure db is not readable without the good password (Using Key2 as password)
        c = cipherDatabaseOpen(config, path, Key2);
        assertThat(c).isNull();
        //        ,
        //                String.format(
        //                        "2 Be sure the database with config %s cannot be read with the key
        // '%s' (good key is %s)",
        //                        config.getClass().getSimpleName(), Key2, Key1));

        // 4. Rekey the database
        c = cipherDatabaseOpen(config, path, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //        ,
        //                String.format(
        //                        "3. Be sure the database with config %s can be read before
        // rekeying with the key '%s' (replacing %s with %s)",
        //                        config.getClass().getSimpleName(), Key2, Key1, Key2));
        c.createStatement().execute(String.format("PRAGMA rekey='%s'", Key2));
        assertThat(databaseIsReadable(c)).isTrue();
        // , "4. Be sure the database is still readable after rekeying");
        c.close();

        // 5. Should now be readable with Key2
        c = cipherDatabaseOpen(config, path, Key2);
        assertThat(databaseIsReadable(c)).isTrue();
        //        ,
        //                String.format(
        //                        "5. Should now be able to open the database with config %s and the
        // new key '%s'",
        //                        config.getClass().getSimpleName(), Key2));
        c.close();
    }

    @Test
    public void chacha20DatabaseTest() throws SQLException, IOException {
        genericDatabaseTest(SQLiteMCChacha20Config.getDefault());
    }

    @Test
    public void chacha20DatabaseHexKeyTest() throws SQLException, IOException {
        SQLiteMCConfig.Builder config = SQLiteMCChacha20Config.getDefault();

        String dbfile = createFile();
        String Key1 = "raw:54686973206973206D792076657279207365637265742070617373776F72642E";

        cipherDatabaseCreate(config, dbfile, Key1);

        // 2. Ensure db is readable with good Password
        Connection c = cipherDatabaseOpen(config, dbfile, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //                ,
        //                String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        config.getClass().getSimpleName(), Key1));
        c.close();
    }

    @Test
    public void sqlCipherDatabaseHexKeyTest() throws SQLException, IOException {
        SQLiteMCConfig.Builder config = SQLiteMCSqlCipherConfig.getDefault();

        String dbfile = createFile();
        String Key1 = "x'54686973206973206D792076657279207365637265742070617373776F72642E'";
        cipherDatabaseCreate(config, dbfile, Key1);

        // 2. Ensure db is readable with good Password
        Connection c = cipherDatabaseOpen(config, dbfile, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //        ,
        //                String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        config.getClass().getSimpleName(), Key1));
        c.close();
    }

    @Test
    public void aes128cbcDatabaseTest() throws IOException, SQLException {
        genericDatabaseTest(SQLiteMCWxAES128Config.getDefault());
    }

    @Test
    public void aes256cbcDatabaseTest() throws IOException, SQLException {
        genericDatabaseTest(SQLiteMCWxAES256Config.getDefault());
    }

    @Test
    public void sqlCipherDatabaseTest() throws IOException, SQLException {
        genericDatabaseTest(SQLiteMCSqlCipherConfig.getDefault());
    }

    @Test
    public void RC4DatabaseTest() throws IOException, SQLException {
        genericDatabaseTest(SQLiteMCRC4Config.getDefault());
    }

    @Test
    public void ascon128Test() throws IOException, SQLException {
        genericDatabaseTest(SQLiteMCAscon128Config.getDefault());
    }

    @Test
    public void defaultCihperDatabaseTest() throws IOException, SQLException {
        genericDatabaseTest(new SQLiteMCConfig.Builder());
    }

    @Test
    public void defaultCihperDatabaseWithSpecialKeyTest() throws IOException, SQLException {
        SQLiteMCConfig.Builder config = new SQLiteMCConfig.Builder();

        String path = createFile();
        // 1. Open + Write + cipher with "Key1" key
        String Key1 = "Key1&a%32er";
        String Key2 = "Key1";

        cipherDatabaseCreate(config, path, Key1);

        // 2. Ensure db is readable with good Password
        Connection c = cipherDatabaseOpen(config, path, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //                ,
        //                String.format(
        //                        "1. Be sure the database with config %s can be read with the key
        // '%s'",
        //                        config.getClass().getSimpleName(), Key1));
        c.close();

        // 3. Ensure db is not readable without the good password (Using Key2 as password)
        c = cipherDatabaseOpen(config, path, Key2);
        assertThat(c).isNull();
        //        ,
        //                String.format(
        //                        "2 Be sure the database with config %s cannot be read with the key
        // '%s' (good key is %s)",
        //                        config.getClass().getSimpleName(), Key2, Key1));

        // 4. Rekey the database
        c = cipherDatabaseOpen(config, path, Key1);
        assertThat(databaseIsReadable(c)).isTrue();
        //                ,
        //                String.format(
        //                        "3. Be sure the database with config %s can be read before
        // rekeying with the key '%s' (replacing %s with %s)",
        //                        config.getClass().getSimpleName(), Key2, Key1, Key2));
        c.createStatement().execute(String.format("PRAGMA rekey=%s", Key2));
        assertThat(databaseIsReadable(c)).isTrue();
        //                , "4. Be sure the database is still readable after rekeying");
        c.close();

        // 5. Should now be readable with Key2
        c = cipherDatabaseOpen(config, path, Key2);
        assertThat(databaseIsReadable(c)).isTrue();
        //        ,
        //                String.format(
        //                        "5. Should now be able to open the database with config %s and the
        // new key '%s'",
        //                        config.getClass().getSimpleName(), Key2));
        c.close();
    }

    @Test
    public void crossCipherAlgorithmTest() throws IOException, SQLException {
        String dbfile = createFile();
        String key = "key";
        cipherDatabaseCreate(new SQLiteMCConfig.Builder(), dbfile, key);

        Connection c = cipherDatabaseOpen(new SQLiteMCConfig.Builder(), dbfile, key);
        assertThat(databaseIsReadable(c))
                .isTrue(); // , "Crosstest : Should be able to read the base db");
        c.close();

        c = cipherDatabaseOpen(SQLiteMCRC4Config.getDefault(), dbfile, key);
        assertThat(c).isNull(); // , "Should not be readable with RC4");
        //        c.close();

        c = cipherDatabaseOpen(SQLiteMCSqlCipherConfig.getDefault(), dbfile, key);
        assertThat(c).isNull(); // , "Should not be readable with SQLCipher");
        //        c.close();

        c = cipherDatabaseOpen(SQLiteMCWxAES128Config.getDefault(), dbfile, key);
        assertThat(c).isNull(); // , "Should not be readable with Wx128bit");
        //        c.close();

        c = cipherDatabaseOpen(SQLiteMCWxAES256Config.getDefault(), dbfile, key);
        assertThat(c).isNull(); // , "Should not be readable with Wx256");
        //        c.close();

        c = cipherDatabaseOpen(SQLiteMCChacha20Config.getDefault(), dbfile, key);
        assertThat(databaseIsReadable(c))
                .isTrue(); // , "Should be readable with Chacha20 as it is default");
        //        c.close();
    }
}
