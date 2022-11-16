package com.example.m_expense.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.m_expense.Expense.Expense;
import com.example.m_expense.Trip.Trip;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "MExpense.db";
    private static final int DATABASE_VERSION = 1;

    // Trip table
    private static final String TABLE_TRIP = "trip";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "tripName";
    private static final String COLUMN_DESTINATION = "tripDestination";
    private static final String COLUMN_DATE_FROM = "tripDateFrom";
    private static final String COLUMN_DATE_TO = "tripDateTo";
    private static final String COLUMN_RISK = "risk";
    private static final String COLUMN_DESC = "tripDesc";

    // Expense table
    private static final String TABLE_EXPENSE = "Expense";
    private static final String COLUMN_TYPE = "expenseType";
    private static final String COLUMN_AMOUNT = "expenseAmount";
    private static final String COLUMN_DATE_EXPENSE = "expenseDate";
    private static final String COLUMN_NOTE = "expenseNote";
    private static final String COLUMN_LOCATION = "expenseLocation";
    public static final String COLUMN_TRIP_ID = "tripId";


    // User table
    public static final String TABLE_USER = "Login";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Create table query
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // This is called the first time a database is accessed. There should be code in here to create a new database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        dropAndRecreate(db);
    }

    private void dropAndRecreate(SQLiteDatabase db) {
        // delete table trip and expense
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        createTablesExpense(db);
        createTablesUser(db);
        insertDataUser(db);
    }

    private void insertDataUser(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        // add many users
        cv.put(COLUMN_USERNAME, "admin");
        cv.put(COLUMN_PASSWORD, "admin");
        db.insert(TABLE_USER, null, cv);
        cv.put(COLUMN_USERNAME, "user");
        cv.put(COLUMN_PASSWORD, "user");
        db.insert(TABLE_USER, null, cv);
        cv.put(COLUMN_USERNAME, "ll4825k@gre.ac.uk");
        cv.put(COLUMN_PASSWORD, "123456");
        db.insert(TABLE_USER, null, cv);
    }

    private void createTablesUser(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_USER +
                " (" + COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT not null);";
        db.execSQL(query);
    }

    private void createTables(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_TRIP +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESTINATION + " TEXT, " +
                COLUMN_DATE_FROM + " TEXT, " +
                COLUMN_DATE_TO + " TEXT, " +
                COLUMN_RISK + " INTEGER, " +
                COLUMN_DESC + " TEXT);";
        db.execSQL(query);
    }

    private void createTablesExpense(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_EXPENSE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT not null, " +
                COLUMN_AMOUNT + " FLOAT, " +
                COLUMN_DATE_EXPENSE + " DATE, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_TRIP_ID + " INTEGER references " + TABLE_TRIP + "(" + COLUMN_ID + "), " +
                COLUMN_USERNAME + " INTEGER references " + TABLE_USER + "(" + COLUMN_ID + "));";
        db.execSQL(query);
    }

    public long addTrip (Trip trip) {
        long insertId;
        SQLiteDatabase db = this.getWritableDatabase(); // get writable database
        ContentValues values = new ContentValues(); // create a new map of values, where column names are the keys
        values.put(COLUMN_NAME, trip.getName());
        values.put(COLUMN_DESTINATION, trip.getDes());
        values.put(COLUMN_DATE_FROM, trip.getDateFrom());
        values.put(COLUMN_DATE_TO, trip.getDateTo());
        values.put(COLUMN_RISK, trip.getRisk());
        values.put(COLUMN_DESC, trip.getDesc());

        // Inserting Row
        insertId = db.insert(TABLE_TRIP, null, values);
        db.close(); // Closing database connection
        return insertId;
    }

    public long addExpense(Expense expense) {
        long insertId;
        SQLiteDatabase db = this.getWritableDatabase(); // get writable database
        ContentValues values = new ContentValues(); // create a new map of values, where column names are the keys
        values.put(COLUMN_TYPE, expense.getTypeExpense());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_LOCATION, expense.getLocation());
        values.put(COLUMN_DATE_EXPENSE, expense.getDate());
        values.put(COLUMN_NOTE, expense.getNote());
        values.put(COLUMN_TRIP_ID, expense.getTripID());

        // Inserting Row
        insertId = db.insert(TABLE_EXPENSE, null, values);
        db.close(); // Closing database connection
        return insertId;
    }

    public List<Trip> getAllTrip() {
        final String query = "SELECT * FROM " + TABLE_TRIP +" ORDER BY " + COLUMN_ID + " DESC"; // select all query
        SQLiteDatabase db = this.getReadableDatabase(); // get readable database
        final List<Trip> list = new ArrayList<>();
        final Cursor cursor; // cursor to iterate through the result
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Trip trip = new Trip(); // create a new trip object
                    trip.setId(cursor.getInt(0));
                    trip.setName(cursor.getString(1));
                    trip.setDes(cursor.getString(2));
                    trip.setDateFrom(cursor.getString(3));
                    trip.setDateTo(cursor.getString(4));
                    trip.setRisk(cursor.getString(5));
                    trip.setDesc(cursor.getString(6));
                    // Adding object to list
                    list.add(trip);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return list;
    }

    public List<Expense> getAllExpense(Integer id) {
        final String query = String.format(
                "SELECT b.%s, %s, %s, %s, %s, %s, %s FROM " +
                        "%s a, %s b WHERE a.%s = b.%s AND b.%s = %s ORDER BY b.%s DESC",
                COLUMN_ID, COLUMN_TYPE, COLUMN_AMOUNT, COLUMN_DATE_EXPENSE, COLUMN_NOTE, COLUMN_TRIP_ID, COLUMN_LOCATION,TABLE_TRIP, TABLE_EXPENSE, COLUMN_ID, COLUMN_TRIP_ID, COLUMN_TRIP_ID, id, COLUMN_ID
        ); // select all query
        SQLiteDatabase db = this.getReadableDatabase(); // get readable database
        final List<Expense> list = new ArrayList<>(); // list to store all the trip objects
        final Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Expense expense = new Expense();
                    expense.setId(cursor.getInt(0));
                    expense.setTypeExpense(cursor.getString(1));
                    expense.setAmount(Float.valueOf(cursor.getString(2)));
                    expense.setLocation(cursor.getString(6));
                    expense.setDate(cursor.getString(3));
                    expense.setNote(cursor.getString(4));
                    // Adding object to list
                    list.add(expense);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return list;
    }

    public Float getTotalExpense(String id){
        float total = 0f; // total expense
        String query = "SELECT " + COLUMN_AMOUNT + " FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_TRIP_ID + " = " + id; // select all query

        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) { // if there is at least one row
                do {
                    total += cursor.getFloat(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return total;
    }

    public long updateTrip (Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase(); // get writable database
        ContentValues values = new ContentValues(); // create a new map of values, where column names are the keys
        values.put(COLUMN_NAME, trip.getName());
        values.put(COLUMN_DESTINATION, trip.getDes());
        values.put(COLUMN_DATE_FROM, trip.getDateFrom());
        values.put(COLUMN_DATE_TO, trip.getDateTo());
        values.put(COLUMN_RISK, trip.getRisk());
        values.put(COLUMN_DESC, trip.getDesc());

        return db.update(TABLE_TRIP, values, "id=?", new String[]{String.valueOf(trip.getId())}); // updating row
    }

    public long updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, expense.getTypeExpense());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_DATE_EXPENSE, expense.getDate());
        values.put(COLUMN_LOCATION, expense.getLocation());
        values.put(COLUMN_NOTE, expense.getNote());

        return db.update(TABLE_EXPENSE, values, "id=?", new String[]{String.valueOf(expense.getId())}); // updating row
    }

    public long deleteTrip (String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRIP, "id=?", new String[]{row_id});
    }

    public long deleteExpense(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSE, "id=?", new String[]{row_id});
    }

    public void deleteAllTrip() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TRIP);
        db.execSQL("DELETE FROM " + TABLE_EXPENSE);
    }

    public void deleteAllExpense() {
        // delete all data of Expense table
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EXPENSE);
    }

    public Boolean checkUserPass(String user, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{user, pass});
        return cursor.getCount() > 0;
    }

    public Context getContext() {
        return context;
    }
}

