package com.example.pankkiappi.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.example.pankkiappi.model.Account;
import com.example.pankkiappi.model.Card;
import com.example.pankkiappi.model.Payee;
import com.example.pankkiappi.model.Transaction;
import com.example.pankkiappi.model.User;


public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private SQLiteOpenHelper openHelper;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UserManager.db";

    // User table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_ACCOUNT = "account";
    private static final String TABLE_CARD = "card";
    private static final String PAYEES_TABLE = "Payees";
    private static final String TRANSACTIONS_TABLE = "Transactions";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_CITY = "user_city";
    private static final String COLUMN_USER_POSTAL_CODE = "user_postal_code";
    private static final String COLUMN_USER_ADDRESS = "user_address";
    private static final String COLUMN_USER_SALT = "user_salt";
    private static final String COLUMN_USER_TYPE = "user_type";



    private static final int USER_ID = 0;
    // Account Table Columns names
    private static final String COLUMN_ACCOUNT_ID = "account_id";
    private static final String COLUMN_ACCOUNT_NAME = "account_name";
    private static final String COLUMN_ACCOUNT_BALANCE = "account_balance";
   // private static final String COLUMN_ACCOUNT_PAYMENTS_ALLOWED = "payments_allowed";

    private static final int ACCOUNT_ID = 1;
    private static final int ACCOUNT_NAME = 2;
    private static final int ACCOUNT_BALANCE = 3;
    //private static final int ACCOUNT_PAYMENTS_ALLOWED = 4;

    // Card Table Columns names
    private static final String COLUMN_CARD_NUMBER = "card_number";
    private static final String COLUMN_CVC = "cvc";
    private static final String COLUMN_LINKED_ACCOUNT = "linked_account";

    private static final int CARD_NUMBER = 1;
    private static final int CVC = 2;
    private static final int LINKED_ACCOUNT = 3;
    // Payee Table Columns names
    private static final String PAYEE_ID = "_PayeeID";
    private static final String PAYEE_NAME = "PayeeName";

    private static final int PAYEE_ID_COLUMN = 1;
    private static final int PAYEE_NAME_COLUMN = 2;

    // Transaction Table Columns names
    private static final String TRANSACTION_ID = "_TransactionID";
    private static final String TIMESTAMP = "Timestamp";
    private static final String SENDING_ACCOUNT = "SendingAccount";
    private static final String DESTINATION_ACCOUNT = "DestinationAccount";
    private static final String TRANSACTION_PAYEE = "Payee";
    private static final String TRANSACTION_AMOUNT = "Amount";
    private static final String TRANS_TYPE = "Type";

    private static final int TRANSACTION_ID_COLUMN = 2;
    private static final int TIMESTAMP_COLUMN = 3;
    private static final int SENDING_ACCOUNT_COLUMN = 4;
    private static final int DESTINATION_ACCOUNT_COLUMN = 5;
    private static final int TRANSACTION_PAYEE_COLUMN = 6;
    private static final int TRANSACTION_AMOUNT_COLUMN = 7;
    private static final int TRANSACTION_TYPE_COLUMN = 8;

    private static final String CREATE_PAYEES_TABLE =
            "CREATE TABLE " + PAYEES_TABLE + " (" +
                    COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    PAYEE_ID + " TEXT NOT NULL, " +
                    PAYEE_NAME + " TEXT, " +
                    "PRIMARY KEY(" + COLUMN_USER_ID + "," + PAYEE_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";

    // create user table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT NOT NULL,"
            + COLUMN_USER_EMAIL + " TEXT NOT NULL," + COLUMN_USER_PASSWORD + " TEXT NOT NULL," + COLUMN_USER_CITY + " TEXT NOT NULL," + COLUMN_USER_POSTAL_CODE + " TEXT NOT NULL," + COLUMN_USER_ADDRESS + " TEXT NOT NULL," + COLUMN_USER_SALT + " TEXT NOT NULL," + COLUMN_USER_TYPE + " TEXT NOT NULL"  + ")";

    // create account table sql query
    //private String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "(" +
            //COLUMN_USER_ID + " INTEGER NOT NULL, " +
            // COLUMN_ACCOUNT_ID + " TEXT," + COLUMN_ACCOUNT_BALANCE + " REAL,"
          //  + COLUMN_ACCOUNT_NAME + " TEXT" + ")";

    private static final String CREATE_ACCOUNTS_TABLE =
            "CREATE TABLE " + TABLE_ACCOUNT + " (" +
                    COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_ACCOUNT_ID + " TEXT NOT NULL, " +
                    COLUMN_ACCOUNT_NAME + " TEXT, " +
                    COLUMN_ACCOUNT_BALANCE + " REAL, " +
                    "PRIMARY KEY(" + COLUMN_USER_ID + "," + COLUMN_ACCOUNT_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";

    private static final String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARD + "(" +
            COLUMN_USER_ID + " INTEGER NOT NULL , " +
            COLUMN_ACCOUNT_ID + " TEXT NOT NULL," +
            COLUMN_CARD_NUMBER + " INTEGER NOT NULL, " +
            COLUMN_CVC + " INTEGER NOT NULL)";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS " + TABLE_ACCOUNT;
    private String DROP_CARD_TABLE = "DROP TABLE IF EXISTS " + TABLE_CARD;
    private String DROP_PAYEES_TABLE = "DROP TABLE IF EXISTS " + PAYEES_TABLE;
    private String DROP_TRANSACTIONS_TABLE = "DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE;

    private static final String CREATE_TRANSACTIONS_TABLE =
            "CREATE TABLE " + TRANSACTIONS_TABLE + " (" +
                    COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_ACCOUNT_ID + " TEXT NOT NULL, " +
                    TRANSACTION_ID + " TEXT NOT NULL, " +
                    TIMESTAMP + " TEXT, " +
                    SENDING_ACCOUNT + " TEXT, " +
                    DESTINATION_ACCOUNT + " TEXT, " +
                    TRANSACTION_PAYEE + " TEXT, " +
                    TRANSACTION_AMOUNT + " REAL, " +
                    TRANS_TYPE + " TEXT, " +
                    "PRIMARY KEY(" + COLUMN_USER_ID + "," + COLUMN_ACCOUNT_ID + "," + TRANSACTION_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + "," + COLUMN_ACCOUNT_ID + ") REFERENCES " +
                    TABLE_ACCOUNT + "(" + COLUMN_USER_ID + "," + COLUMN_ACCOUNT_ID + ")," +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";
    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PAYEES_TABLE);
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        db.execSQL(CREATE_CARD_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(PAYEES_TABLE);
        db.execSQL(DROP_ACCOUNT_TABLE);
        db.execSQL(TRANSACTIONS_TABLE);
        db.execSQL(TABLE_CARD);
        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_CITY, user.getCity());
        values.put(COLUMN_USER_POSTAL_CODE, user.getPostalCode());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_SALT, user.getSalt());
        values.put(COLUMN_USER_TYPE, user.getType());


        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void saveNewAccount(User user, Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getId());
        values.put(COLUMN_ACCOUNT_ID, account.getAccountNo());
        values.put(COLUMN_ACCOUNT_NAME, account.getAccountName());
        values.put(COLUMN_ACCOUNT_BALANCE, account.getAccountBalance());

       // if (account.getPayments() == 1) {
       //     values.put(COLUMN_ACCOUNT_PAYMENTS_ALLOWED, 1);
       // } else {
        //    values.put(COLUMN_ACCOUNT_PAYMENTS_ALLOWED, 0);
       // }

        db.insert(TABLE_ACCOUNT, null, values);
        db.close();
    }

    public void saveNewCard(User user, Account account, String cardNumber,  String cvc) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getId());
        values.put(COLUMN_ACCOUNT_ID, account.getAccountNo());
        values.put(COLUMN_CARD_NUMBER, cardNumber);
        values.put(COLUMN_CVC, cvc);

        db.insert(TABLE_CARD, null, values);
        db.close();
    }
    public void overwriteAccount(User user, Account account) {

        database = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ID,user.getId());
        cv.put(COLUMN_ACCOUNT_ID,account.getAccountNo());
        cv.put(COLUMN_ACCOUNT_NAME,account.getAccountName());
        cv.put(COLUMN_ACCOUNT_BALANCE, account.getAccountBalance());

        database.update(TABLE_ACCOUNT, cv, COLUMN_USER_ID + "=? AND " + COLUMN_ACCOUNT_ID +"=?",
                new String[] {String.valueOf(user.getId()), account.getAccountNo()});
        database.close();
    }

    public void saveNewTransaction(User user, String accountNo, Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getId());
        values.put(COLUMN_ACCOUNT_ID, accountNo);
       // values.put(TRANSA);
    }
    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public ArrayList<User> getAllUser() {


        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_CITY,
                COLUMN_USER_POSTAL_CODE,
                COLUMN_USER_ADDRESS,
                COLUMN_USER_SALT,
                COLUMN_USER_TYPE

        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        ArrayList<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                user.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_USER_CITY)));
                user.setPostalCode(cursor.getString(cursor.getColumnIndex(COLUMN_USER_POSTAL_CODE)));
                user.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ADDRESS)));
                user.setSalt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_SALT)));
                user.setType(cursor.getString(cursor.getColumnIndex(COLUMN_USER_TYPE)));

                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    /**
     * This method to update user record
     *
     * @param user
     */
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_CITY, user.getCity());
        values.put(COLUMN_USER_POSTAL_CODE, user.getPostalCode());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_SALT, user.getSalt());
        values.put(COLUMN_USER_TYPE, user.getType());


        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param
     * @return true/false
     */
    public ArrayList<Payee> getPayeesFromCurrentProfile(long profileID) {
        ArrayList<Payee> payees = new ArrayList<>();
        database = this.getReadableDatabase();

        Cursor cursor = database.query(PAYEES_TABLE, null, null, null, null,
                null ,null);
        getPayeesFromCursor(profileID, payees, cursor);

        cursor.close();
        database.close();

        return payees;
    }

    private void getPayeesFromCursor(long profileID, ArrayList<Payee> payees, Cursor cursor) {

        while (cursor.moveToNext()) {

            if (profileID == cursor.getLong(USER_ID)) {
                long id = cursor.getLong(USER_ID);
                String payeeID = cursor.getString(PAYEE_ID_COLUMN);
                String payeeName = cursor.getString(PAYEE_NAME_COLUMN);

                payees.add(new Payee(payeeID, payeeName, id));
            }
        }
    }

    public ArrayList<Transaction> getTransactionsFromCurrentAccount(long profileID, String accountNo) {

        ArrayList<Transaction> transactions = new ArrayList<>();
        database = this.getReadableDatabase();

        Cursor cursor = database.query(TRANSACTIONS_TABLE, null, null, null, null,
                null ,null);

        getTransactionsFromCursor(profileID, accountNo, transactions, cursor);

        cursor.close();
        database.close();

        return transactions;
    }
    private void getTransactionsFromCursor(long profileID, String accountNo, ArrayList<Transaction> transactions, Cursor cursor) {

        while (cursor.moveToNext()) {

            if (profileID == cursor.getLong(USER_ID)) {
                long id = cursor.getLong(USER_ID);
                if (accountNo.equals(cursor.getString(ACCOUNT_ID))) {
                    String transactionID = cursor.getString(TRANSACTION_ID_COLUMN);
                    String timestamp = cursor.getString(TIMESTAMP_COLUMN);
                    String sendingAccount = cursor.getString(SENDING_ACCOUNT_COLUMN);
                    String destinationAccount = cursor.getString(DESTINATION_ACCOUNT_COLUMN);
                    String payee = cursor.getString(TRANSACTION_PAYEE_COLUMN);
                    double amount = cursor.getDouble(TRANSACTION_AMOUNT_COLUMN);
                    Transaction.TRANSACTION_TYPE transactionType = Transaction.TRANSACTION_TYPE.valueOf(cursor.getString(TRANSACTION_TYPE_COLUMN));

                    if (transactionType == Transaction.TRANSACTION_TYPE.PAYMENT) {
                        transactions.add(new Transaction(transactionID, timestamp, payee, amount, id));
                    } else if (transactionType == Transaction.TRANSACTION_TYPE.TRANSFER) {
                        transactions.add(new Transaction(transactionID, timestamp, sendingAccount, destinationAccount, amount, id));
                    } else if (transactionType == Transaction.TRANSACTION_TYPE.DEPOSIT) {
                        transactions.add(new Transaction(transactionID, timestamp, amount, id));
                    }
                }

            }
        }
    }
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean checkAdmin(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println(email);
        Cursor cursor = db.rawQuery("SELECT user_type FROM user WHERE user_email = ?", new String[] {email});
        if( cursor != null && cursor.moveToFirst() ){
            if (cursor.getString(0).equals("admin")) {
                System.out.println("on admin");
                return true;
            }
        }
        System.out.println("ei oo admin");
        cursor.close();
        db.close();
        return false;

    }
    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String generatedPassword = null;

        //String selectQuery = "SELECT " + COLUMN_USER_SALT + " FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = "+email;
        Cursor cursor1 = db.rawQuery("SELECT user_salt FROM user WHERE user_email = ?", new String[] {email});
        //Liikutetaan cursoria ettei kaadu jos yrittää olemattomalla tunnuksella kirjautua
        if( cursor1 != null && cursor1.moveToFirst() ){
            String salt = cursor1.getString(0);
            byte[] bytes = salt.getBytes(StandardCharsets.ISO_8859_1);
            String saltString = new String(bytes, StandardCharsets.ISO_8859_1);
            String passwordToHash = password + saltString;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(saltString.getBytes(StandardCharsets.ISO_8859_1));
                byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.ISO_8859_1));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hashedPassword.length; i++) {
                    sb.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = sb.toString();
                password = generatedPassword;
            } catch (NoSuchAlgorithmException x) {
                // do proper exception handling
            }
        }

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();
        cursor1.close();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public ArrayList<Account> getAccountsFromCurrentProfile(long user_id) {

        ArrayList<Account> accounts = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_ACCOUNT, null, null, null, null,
                null ,null);
        getAccountsFromCursor(user_id, accounts, cursor);

        cursor.close();
        database.close();

        return accounts;
    }



    private void getAccountsFromCursor(long userID, ArrayList<Account> accounts, Cursor cursor) {

        while (cursor.moveToNext()) {

            if (userID == cursor.getLong(USER_ID)) {
                long id = cursor.getLong(USER_ID);
                String accountNo = cursor.getString(ACCOUNT_ID);
                String accountName = cursor.getString(ACCOUNT_NAME);
                double accountBalance = cursor.getDouble(ACCOUNT_BALANCE);
                //int payments = cursor.getInt(ACCOUNT_PAYMENTS_ALLOWED);

                accounts.add(new Account(accountName, accountNo, accountBalance, id));
            }
        }
    }

    public ArrayList<Card> getCardsFromCurrentProfile(long user_id) {

        ArrayList<Card> cards = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_CARD, null, null, null, null, null, null);


        getCardsFromCursor(user_id, cards, cursor);

        cursor.close();
        database.close();

        return cards;
    }

    private void getCardsFromCursor(long user_id, ArrayList<Card> cards, Cursor cursor) {

        while (cursor.moveToNext()) {
            //System.out.println(user_id);
           // System.out.println(cursor.getInt(USER_ID));
            if (user_id == cursor.getInt(0)) {
                long id = cursor.getLong(0);
                System.out.println(user_id);
                String account_id = cursor.getString(ACCOUNT_ID);
                System.out.println(account_id);
                String card_number = cursor.getString(CARD_NUMBER);
                System.out.println(card_number);
                String cvc = cursor.getString(CVC);
                System.out.println(cvc);


                cards.add(new Card(card_number, cvc, account_id, id));

            }
        }
    }

}
