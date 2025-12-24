## How to Specify Database Files

Here is an example to establishing a connection to a database file `C:\work\mydatabase.db` (in Windows)

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/work/mydatabase.db")) { /*...*/ }
```

Opening a UNIX (Linux, maxOS, etc.) file `/home/leo/work/mydatabase.db`
```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/home/leo/work/mydatabase.db")) { /*...*/ }
```

## How to Use Memory or Temporary Databases
SQLite supports in-memory databases, which do not create any database files. To use a memory database in your Java code, get the database connection as follows:

```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) { /*...*/ }
```

You can create temporary database as follows:
```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite:")) { /*...*/ }
```

## How to use Online Backup and Restore Feature
Take a backup of the whole database to `backup.db` file:

```java
try (
    // Create a memory database
    Connection conn = DriverManager.getConnection("jdbc:sqlite:");
    Statement stmt = conn.createStatement();
) {
    // Do some updates
    stmt.executeUpdate("create table sample(id, name)");
    stmt.executeUpdate("insert into sample values(1, \"leo\")");
    stmt.executeUpdate("insert into sample values(2, \"yui\")");
    // Dump the database contents to a file
    stmt.executeUpdate("backup to backup.db");
}
```

Restore the database from a backup file:
```java
try (
    // Create a memory database
    Connection conn = DriverManager.getConnection("jdbc:sqlite:");
    // Restore the database from a backup file
    Statement stat = conn.createStatement();
) {
    stat.executeUpdate("restore from backup.db");
}
```

## Creating BLOB data
1. Create a table with a column of blob type: `create table T (id integer, data blob)`
1. Create a prepared statement with `?` symbol: `insert into T values(1, ?)`
1. Prepare a blob data in byte array (e.g., `byte[] data = ...`)
1. `preparedStatement.setBytes(1, data)`
1. `preparedStatement.execute()...`

## Reading Database Files in classpaths or network (read-only)
To load db files that can be found from the class loader (e.g., db 
files inside a jar file in the classpath), 
use `jdbc:sqlite::resource:` prefix. 

For example, here is an example to access an SQLite DB file, `sample.db` 
in a Java package `org.yourdomain`:
```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:org/yourdomain/sample.db")) { /*...*/ }
```

In addition, external DB resources can be used as follows:
```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:http://www.xerial.org/svn/project/XerialJ/trunk/sqlite-jdbc/src/test/java/org/sqlite/sample.db")) { /*...*/ }
```

To access db files inside some specific jar file (in local or remote), 
use the [JAR URL](http://java.sun.com/j2se/1.5.0/docs/api/java/net/JarURLConnection.html):
```java
try (Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:jar:http://www.xerial.org/svn/project/XerialJ/trunk/sqlite-jdbc/src/test/resources/testdb.jar!/sample.db")) { /*...*/ }
```

DB files will be extracted to a temporary folder specified in `System.getProperty("java.io.tmpdir")`.

## Configure directory to extract native library
sqlite-jdbc extracts a native library for your OS to the directory specified by `java.io.tmpdir` JVM property. To use another directory, set `org.sqlite.tmpdir` JVM property to your favorite path.

## How to use a specific native library
You can use a specific version of the native library by setting the following JVM properties:
```
-Dorg.sqlite.lib.path=/path/to/folder
-Dorg.sqlite.lib.name=your-custom.dll
```

## Override detected architecture

If the detected architecture is incorrect for your system, thus loading the wrong native library, you can override the value setting the following JVM property:
```
-Dorg.sqlite.osinfo.architecture=arm
```

## Configure Connections
```java
SQLiteConfig config = new SQLiteConfig();
// config.setReadOnly(true);   
config.setSharedCache(true);
config.recursiveTriggers(true);
// ... other configuration can be set via SQLiteConfig object
try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sample.db", config.toProperties())) { /*...*/ }
```

## How to Use Encrypted Databases

### Using custom binary library

You need to specify the driver via directly referencing the .dll/.so through:
```
-Dorg.sqlite.lib.path=.
-Dorg.sqlite.lib.name=sqlite_cryption_support.dll
```

### Get started with encryption

The main goal of this library is to allow users to encrypt databases they are producing.
In this section we will walk through the main aspect to understand to make this library
work correctly with your requirements.

The content of this section is mainly extracted from the WxSQLite3 repository.

#### Introduction

This library is compiled with a modified SQLite native library that support multiple
cipher schemes. In order to be used the user must choose a cipher scheme manually. If
not using the default one (at the moment the default cipher is CHACHA20) is applied.

Before applying a configuration, choose the encryption scheme you would like to use in the
supported cipher list.

The full documentation of the native library used can be found here: https://utelle.github.io/SQLite3MultipleCiphers/

#### Configuration methods

##### Configure using SQLiteMCConfig objects

Starting with version 3.32.0 the java implementation has a new configuration object called SQLiteMCConfig that can hold
the cipher configuration.
The interface allows for very simple and quick configuration of the choosen cipher algorithm.

For each cipher supported a ready to use and a customizable object is present. If you want to go completely custom it
is also possible. All parameters are available through setters.

Each conbinaison of paramters presented in previous section are implemented as getDefault (SQLiteMC default) or
get<name>Defautls (ex: getV2Defaults() for SQLCipher))

The object names are :

````
SQLiteMCConfig.Builder; Use this to build a configuration object from scratch
    
SQLiteMCSqlCipherConfig; // Generate a configuration for SQLCipher
SQLiteMCChacha20Config; // Generate a configuration for Chacha20
SQLiteMCWxAES256Config; // Generate a configuration for legacy AES 256 WxSQLite3
SQLiteMCWxAES128Config; // Generate a configuration for legacy AES 128 WxSQLite3
SQLiteMCRC4Config; // Generate a configuration for System.Data.SQLite
````

To specify the key you just need to use the `withKey(String Key)` or any of the configuration object.
To create the connection it is now very simple:

```java
//Using the SQLiteMC default parameters
Connection connection=DriverManager.getConnection("jdbc:sqlite:file:file.db",new SQLiteMCConfig.Builder().withKey("Key").build().toProperties());
Connection connection=new SQLiteMCConfig.Builder().withKey("Key").build().createConnection("jdbc:sqlite:file:file.db");
//Using Chacha20
Connection connection=DriverManager.getConnection("jdbc:sqlite:file:file.db",SQLiteMCChacha20Config.getDefault().withKey("Key").build().toProperties());
Connection connection=SQLiteMCChacha20Config.getDefault().withKey("Key").build().createConnection("jdbc:sqlite:file:file.db");
```

##### Configure using SQL specific SQL functions

**Note:** See
the [SQLite3 Multiple Ciphers SQL Documentation](https://utelle.github.io/SQLite3MultipleCiphers/docs/configuration/config_sql/)
for more details.

##### Configure using URI

**Note:** See
the [SQLite3 Multiple Ciphers URI Documentation](https://utelle.github.io/SQLite3MultipleCiphers/docs/configuration/config_uri/)
for more details.

Example: URI query string to select the legacy SQLCipher Version 2 encryption scheme:

```
file:databasefile?cipher=sqlcipher&legacy=1&kdf_iter=4000
```

#### Encryption key manipulations

Several manipulation can be very usefull when encrypting a database.
For example you may want to change the password used, remove it, or encrypt a plain database.

Here is what you need to know.

##### Encrypt a plain database

1. Open the database file
2. Set cipher configuration
3. Apply the key for the first time using the `PRAGMA` syntax
4. Use as usual

##### Open an encrypted DB

1. Open the database file
2. set cipher configuration
3. Apply the corresponding key using the `PRAGMA` syntax
4. Use normally

##### Change the key used for a database

1. Open the database file
2. Set cipher configuration
3. Apply the current key using the `PRAGMA key='mykey'` syntax (It needs to be adapted if using an hexadecimal key)
4. Change the key using the `PRAGMA rekey='my_new_key'` syntax (It needs to be adapted if using an hexadecimal key)
5. Use normally

##### Remove the key and go back to plain

1. Open the database file
2. Set cipher configuration
3. Apply the current key using the `PRAGMA key='mykey'` syntax (It needs to be adapted if using an hexadecimal key)
4. Change the key to `null` using`PRAGMA rekey=''`
5. Use normally

## Generated keys

SQLite has limited support to retrieve generated keys, using [last_insert_rowid](https://www.sqlite.org/c3ref/last_insert_rowid.html), with the following limitations:
- a single ID can be retrieved, even if multiple rows were added or updated
- it needs to be called right after the statement

By default the driver will eagerly retrieve the generated keys after each statement, which may impact performances.

You can disable the retrieval of generated keys in 3 ways:
- via `SQLiteDataSource#setGetGeneratedKeys(false)`
- via `SQLiteConnectionConfig#setGetGeneratedKeys(false)`:
- using the pragma `jdbc.get_generated_keys`:
```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:?jdbc.get_generated_keys=false")) { /*...*/ }
```

## Explicit read only transactions (use with Hibernate)

In order for the driver to be compliant with Hibernate, it needs to allow setting the read only flag after a connection has been created.

SQLite has a notion of "auto-upgrading" read-only transactions to read-write transactions. This can cause `SQLITE_BUSY` exceptions which are difficult to deal with in a JPA/Hibernate/Spring scenario.

For example:

- open connection
- query data <--- this uses a read-only transaction in SQLite by default
- write data <--- this is risky as it promotes the transaction to read-write
- commit

The approach taken is:

- open transactions on demand
- allow setting `readOnly` only if no statement has been executed yet
- if `readOnly(false)` is received, then we _quit_ out of our transaction, and open a new transaction with `BEGIN IMMEDIATE`. This forces a global lock on the database, preventing `SQLITE_BUSY`.

You can activate explicit read only support in 2 ways:
- via `SQLiteConfig#setExplicitReadOnly(true)`: 
```java
SQLiteConfig config = new SQLiteConfig();
config.setExplicitReadOnly(true);
```
- using the pragma `jdbc.explicit_readonly`:
```java
try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:?jdbc.explicit_readonly=true")) { /*...*/ }
```

## How to use with Android

Android expects JNI native libraries to be bundled differently than a normal Java application.

You will need to extract the native libraries from our jar (from `org/sqlite/native/Linux-Android`), and place them in the `jniLibs` directory:

![android-studio-screenshot](./.github/README_IMAGES/android_jnilibs.png)

The name of directories in our jar and in Android Studio differ, here is a mapping table:

| Jar directory | Android Studio directory |
|---------------|--------------------------|
| aarch64       | arm64-v8a                |
| arm           | armeabi                  |
| x86           | x86                      |
| x86_64        | x86_64                   |

## How to load Run-Time Loadable Extensions

### Enable loadable extensions

- If you use `DriverManager`, configure the `Properties`:

```java
prop.setProperty("enable_load_extension", "true");
```

- If you use `SQLiteConfig`:

```java
SQLiteConfig config = new SQLiteConfig();
config.enableLoadExtension(true);
```

- You can also specify the pragma in the connection string: `"jdbc:sqlite::memory:?enable_load_extension=true"`

### Load an extension

Use the `load_extension` [SQL function](https://sqlite.org/lang_corefunc.html#load_extension).