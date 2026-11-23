package edu.utsa.cs3443.eazemoneyapp.model.wallet;

import edu.utsa.cs3443.eazemoneyapp.model.transactions.Transaction;
import edu.utsa.cs3443.eazemoneyapp.model.transactions.TransactionList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * All wallets of one user, backed by data/{userId}/wallets.csv.
 * Every change (add, update, delete) is written to the file immediately.
 */
public class WalletList {
    private static final String HEADER = "walletId,walletName,startingBalance";

    private String userId;
    private final List<Wallet> wallets = new ArrayList<>();

    /** Reads data/{userId}/wallets.csv. A missing file simply gives an empty list. */
    public void loadWallets(String userId) {
        try {
            reloadWallets(userId);
        } catch (IOException e) {
            System.out.println("Could not read wallets file: " + e.getMessage());
        }
    }

    /** Reloads the user's wallets and reports file or malformed-row errors to the caller. */
    public void reloadWallets(String userId) throws IOException {
        setUserId(userId);
        wallets.clear();

        Path file = getFilePath();
        if (!Files.exists(file)) {
            return;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                wallets.add(convertLineToWallet(line));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid wallet row: " + line, e);
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId must not be blank.");
        }
        this.userId = userId;
    }

    /** Read-only view of the wallets. Use add/update/delete to change them. */
    public List<Wallet> getWallets() {
        return Collections.unmodifiableList(wallets);
    }

    /** Read-only view retained for callers using the previous accessor name. */
    public List<Wallet> getAll() {
        return getWallets();
    }

    /** Loads a user's wallets and returns their balances after applying transactions. */
    public Map<String, BigDecimal> loadWalletBalances(String userId) throws IOException {
        reloadWallets(userId);

        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        for (Wallet wallet : wallets) {
            balances.put(wallet.getWalletId(), BigDecimal.valueOf(wallet.getStartingBalance()));
        }

        TransactionList transactionList = new TransactionList(userId);
        transactionList.loadTransactions();
        for (Transaction transaction : transactionList.getTransactions()) {
            BigDecimal balance = balances.get(transaction.getWalletId());
            if (balance != null) {
                balances.put(transaction.getWalletId(), balance.add(transaction.getAmount()));
            }
        }
        return balances;
    }

    /** Checks whether a wallet still has transactions before it can be deleted. */
    public boolean hasTransactionsForWallet(String walletId) throws IOException {
        TransactionList transactionList = new TransactionList(userId);
        transactionList.loadTransactions();
        return !transactionList.findByWallet(walletId).isEmpty();
    }

    /** @return the wallet with this id, or null if there is none */
    public Wallet findWalletById(String walletId) {
        for (Wallet w : wallets) {
            if (w.getWalletId().equals(walletId)) {
                return w;
            }
        }
        return null;
    }

    /** True if a wallet with this name already exists (ignores upper/lower case). */
    public boolean isWalletExists(String walletId) {
        return this.findWalletById(walletId) != null;
    }

    public void addWallet(Wallet wallet) throws IOException {
        if (wallet == null || findWalletById(wallet.getWalletId()) != null) {
            throw new IllegalArgumentException("Wallet must have a unique ID.");
        }
        wallets.add(wallet);
        try {
            saveWalletToFile();
        } catch (IOException e) {
            wallets.remove(wallet);
            throw e;
        }
    }

    public String nextWalletId() {
        int max = 1000;
        for (Wallet w : wallets) {
            String id = w.getWalletId();
            if (id != null && id.length() > 1 && (id.charAt(0) == 'w' || id.charAt(0) == 'W')) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {
                    // non-numeric id, skip it
                }
            }
        }
        return "w" + (max + 1);
    }

    /**
     * Saves changes made to a wallet that is already in the list
     * (call it after using the wallet's setters).
     * @return false if no wallet with that id exists
     */
    public boolean updateWallet(Wallet wallet) throws IOException {
        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getWalletId().equals(wallet.getWalletId())) {
                Wallet previous = wallets.get(i);
                wallets.set(i, wallet);
                try {
                    saveWalletToFile();
                } catch (IOException e) {
                    wallets.set(i, previous);
                    throw e;
                }
                return true;
            }
        }
        return false;
    }

    /** @return false if no wallet with that id exists */
    public boolean deleteWallet(String walletId) throws IOException {
        int index = -1;
        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getWalletId().equals(walletId)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return false;
        }

        Wallet removed = wallets.remove(index);
        try {
            saveWalletToFile();
        } catch (IOException e) {
            wallets.add(index, removed);
            throw e;
        }
        return true;
    }

    private Path getFilePath() {
        if (userId == null) {
            throw new IllegalStateException("WalletList userId must be set before accessing its file.");
        }
        return Paths.get("data", userId, "wallets.csv");
    }

    private void saveWalletToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Wallet w : wallets) {
            lines.add(convertWalletToLine(w));
        }
        Path file = getFilePath();
        Files.createDirectories(file.getParent());
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /** Builds a Wallet from a CSV line. Throws IllegalArgumentException if malformed. */
    private Wallet convertLineToWallet(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 fields but found " + parts.length + ": " + line);
        }
        try {
            return new Wallet(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad startingBalance in row: " + line, e);
        }
    }

    /** Converts a wallet to a CSV line (no trailing newline). */
    private String convertWalletToLine(Wallet wallet) {
        return wallet.getWalletId() + "," + wallet.getWalletName() + ","
                + String.format(Locale.US, "%.2f", wallet.getStartingBalance());
    }
}