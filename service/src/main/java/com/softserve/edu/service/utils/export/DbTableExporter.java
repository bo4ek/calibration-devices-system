package com.softserve.edu.service.utils.export;

import com.softserve.edu.common.Constants;
import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbTableExporter extends TableExporter {
    private String tableName;

    public DbTableExporter() {
        this("DefaultTable");
    }

    public DbTableExporter(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void exportToFile(List<TableExportColumn> data, File output) throws Exception {
        output.delete();

        SqlJetDb db = SqlJetDb.open(output, true);
        db.getOptions().setAutovacuum(true);
        db.runTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                db.getOptions().setUserVersion(1);
                return true;
            }
        }, SqlJetTransactionMode.WRITE);


        db.beginTransaction(SqlJetTransactionMode.WRITE);
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
        for (int i = 0; i < data.size() - 1; ++i) {
            createTableQuery += data.get(i).getName() + " " + data.get(i).getType() + ", ";
        }
        createTableQuery += data.get(data.size() - 1).getName() + " " + data.get(data.size() - 1).getType() + ") ";
        createTableQuery += "PRAGMA encoding = \"UTF-8\"";
        db.createTable(createTableQuery);
        db.beginTransaction(SqlJetTransactionMode.WRITE);
        try {
            ISqlJetTable table = db.getTable(tableName);
            for (int i = 0; i < data.get(0).getData().size(); ++i) {
                String[] row = new String[data.size()];
                for (int j = 0; j < data.size(); ++j) {
                    row[j] = data.get(j).getData().get(i);
                }
                table.insert(row);
            }
        } finally {
            db.commit();
        }
        db.close();
    }
}