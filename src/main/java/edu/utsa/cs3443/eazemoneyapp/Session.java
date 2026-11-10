package edu.utsa.cs3443.eazemoneyapp;

import edu.utsa.cs3443.eazemoneyapp.model.login.Account;
import edu.utsa.cs3443.eazemoneyapp.model.login.PasswordHasher;

import java.time.LocalDate;

/**
 * Holds the currently logged-in Account for the duration of the program run.
 * Every menu receives the Account from here so it knows which user's data to operate on.
 */
public final class Session {

    // Temporary development account; remove this initializer when login is implemented.
    private static Account currentAccount = createDevelopmentAccount();

    private Session() {}

    private static Account createDevelopmentAccount() {
        String salt = "development-only-salt";
        return new Account(
                "u1001",
                "jdoe",
                PasswordHasher.hashPassword("pass1234", salt),
                salt,
                "John",
                "USD",
                LocalDate.of(2026, 10, 1),
                true
        );
    }

    /**
     *
     * Starts a session for the given account.
     */
    public static void login(Account account) {
        currentAccount = account;
    }

    /**
     *
     * Ends the current session (sign out).
     */
    public static void logout() {
        currentAccount = null;
    }

    public static boolean isLoggedIn() {
        return currentAccount != null;
    }

    /**
     *
     * @return the logged-in Account, or null if nobody is logged in.
     */
    public static Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     *
     * @return the logged-in user's id, or null if nobody is logged in.
     */
    public static String getUserID() {
        return currentAccount == null ? null : currentAccount.getUserID();
    }
}
