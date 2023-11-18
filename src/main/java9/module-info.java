module io.github.willena.sqlitejdbc {

    requires org.slf4j;
    requires transitive java.sql;
    requires transitive java.sql.rowset;
    requires static org.graalvm.nativeimage;

    exports org.sqlite;
    exports org.sqlite.core;
    exports org.sqlite.date;
    exports org.sqlite.javax;
    exports org.sqlite.jdbc3;
    exports org.sqlite.jdbc4;
    exports org.sqlite.util;
    exports org.sqlite.mc;

    provides java.sql.Driver with org.sqlite.JDBC;

}
