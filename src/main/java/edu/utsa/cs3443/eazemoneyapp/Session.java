package edu.utsa.cs3443.eazemoneyapp;

import edu.utsa.cs3443.eazemoneyapp.model.login.Account;

/**
 * Holds the currently logged-in Account for the duration of the program run.
 * Every menu receives the Account from here so it knows which user's data to operate on.
 */
public final class Session {

    private static Account currentAccount;

    private Session() {}
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
