package com.example.xyzt.customers;

import com.example.xyzt.accounts.Account;
import com.example.xyzt.transactions.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CustomerActions {

    @Autowired
    JdbcTemplate jdbcTemplate;

    Customer currentCustomer = null;
    Customer targetCustomer = null;
    Integer currentAccountId = null;

    int customerIdVal = 1;
    int accountId = 1;
    int transactionId = 1;

    String startDateRequired;
    String endDateRequired;

    public int listNumberOfAccountsForUser(Integer customerId) {
        int numberOfAccounts = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mydb.accounts where partnerId=" + customerId, Integer.class);

        return numberOfAccounts;
    }

    @RequestMapping(value = "/welcome")
    @ResponseBody
    public ModelAndView addNewCustomer(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName,
                                       @RequestParam(value = "address") String address, @RequestParam(value = "city") String city,
                                       @RequestParam(value = "nationality") String nationality, @RequestParam(value = "userName") String userName,
                                       @RequestParam(value = "password") String password) {
        ModelAndView modelAndView = new ModelAndView();

        Customer customer = new Customer(customerIdVal, firstName, lastName, address, city, nationality, userName, password);

        modelAndView.setViewName("/hello");
        jdbcTemplate.update(
                "INSERT INTO mydb.customers (customerId, firstName, lastName, address, city, nationality, userName, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                customer.getCustomerId(), customer.getFirstName(), customer.getLastName(), customer.getAddress(), customer.getCity(), customer.getNationality(), customer.getUserName(), customer.getPassword()
        );

        customerIdVal += 1;
        currentCustomer = customer;

        return modelAndView;
    }

    @RequestMapping("/login_user")
    public ModelAndView loginCustomer(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        int customerExistance = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mydb.customers where " + "userName=" + "'" + userName + "'and password='" + password + "'", Integer.class);

        ModelAndView modelAndView = new ModelAndView();

        if(customerExistance == 1) {
            modelAndView.setViewName("/hello");

            jdbcTemplate.query(
                "SELECT * FROM mydb.customers WHERE userName=" + "'" + userName + "'and password='" + password + "'",
                (rs, rowNum) -> currentCustomer = new Customer(rs.getInt("customerId"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("address"), rs.getString("city"),
                        rs.getString("nationality"), rs.getString("userName"), rs.getString("password"))
            );

            return modelAndView;
        }
        modelAndView.setViewName("/wrong_user");
        return modelAndView;
    }

    @RequestMapping("/delete_account")
    public ModelAndView deleteAccount() {

        String sqlQuery = "DELETE FROM mydb.customers WHERE customerId=?";
        jdbcTemplate.update(sqlQuery, currentCustomer.getCustomerId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/deleted_account");
        return modelAndView;
    }

    @RequestMapping("/display_bank_account_creation")
    public ModelAndView createAccountPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create_bank_account_fields");
        return modelAndView;
    }

    @RequestMapping("/create_bank_account")
    public ModelAndView createBankAccount(@RequestParam("currency") String currency) {
        int currentNumberOfAccounts = listNumberOfAccountsForUser(currentCustomer.getCustomerId());
        jdbcTemplate.update(
                "INSERT INTO mydb.accounts (accountId, partnerId, balance, currency) VALUES (?, ?, ?, ?)",
                accountId, currentCustomer.getCustomerId(), 0, currency
        );
        int updatedNumberOfAccounts = listNumberOfAccountsForUser(currentCustomer.getCustomerId());

        ModelAndView modelAndView = new ModelAndView();

        if(updatedNumberOfAccounts - currentNumberOfAccounts == 1) {
            accountId += 1;
            modelAndView.setViewName("bank_account_successful");
            return modelAndView;
        }

        modelAndView.setViewName("bank_account_failure");
        return modelAndView;
    }

    @RequestMapping("/list_accounts_for_user")
    public ModelAndView listAllAccountsForUser() {
        ModelAndView modelAndView = new ModelAndView();

        ArrayList<Account> accounts = new ArrayList<>();
        jdbcTemplate.query(
                "SELECT * FROM mydb.accounts WHERE partnerId=" + currentCustomer.getCustomerId(),
                (rs, rowNum) -> new Account(rs.getInt("accountId"), rs.getFloat("balance"),
                        rs.getString("currency"), rs.getInt("partnerId"))
        ).forEach(account -> accounts.add(account));

        if(accounts.size() == 0) {
            modelAndView.setViewName("no_accounts_to_display");
            return modelAndView;
        }
        modelAndView.addObject("accounts", accounts);
        modelAndView.setViewName("accounts_list");
        return modelAndView;
    }

    @RequestMapping("/delete_bank_account/{accountIdValue}")
    public ModelAndView deleteBankAccount(@PathVariable("accountIdValue") Integer accountIdValue) {
        ModelAndView modelAndView = new ModelAndView();

        int currentNumberOfAccounts = listNumberOfAccountsForUser(currentCustomer.getCustomerId());
        String sqlQuery = "DELETE FROM mydb.accounts WHERE partnerId=? AND accountId=?";
        jdbcTemplate.update(sqlQuery, currentCustomer.getCustomerId(), accountIdValue);
        int updatedNumberOfAccounts = listNumberOfAccountsForUser(currentCustomer.getCustomerId());

        if(currentNumberOfAccounts - updatedNumberOfAccounts == 1) {
            accountId += 1;
            modelAndView.setViewName("bank_account_deletion_successful");
            return modelAndView;
        }
        modelAndView.setViewName("bank_account_deletion_failure");
        return modelAndView;
    }

    @RequestMapping("/keep_data_of_bank_account/{accountIdValue}")
    public ModelAndView keepDataForBankAccount(@PathVariable("accountIdValue") Integer accountIdValue) {
        currentAccountId = accountIdValue;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create_new_transaction_fields");
        return modelAndView;
    }

    @RequestMapping("/create_new_transaction")
    public ModelAndView createNewTransaction(@RequestParam(value = "targetUserName") String targetUserName, @RequestParam(value = "amount") Integer amount,
                                             @RequestParam(value = "targetAccountId") String targetAccountId) {

        ModelAndView modelAndView = new ModelAndView();

        jdbcTemplate.query(
                "SELECT * FROM mydb.customers WHERE userName=" + "'" + targetUserName + "'",
                (rs, rowNum) -> targetCustomer = new Customer(rs.getInt("customerId"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("address"), rs.getString("city"),
                        rs.getString("nationality"), rs.getString("userName"), rs.getString("password"))
        );

        if(targetCustomer == null) {
            modelAndView.setViewName("no_such_user");
            return modelAndView;
        }

        jdbcTemplate.update(
                "INSERT INTO mydb.transactions (transactionId, targetAccount, targetCustomer, sendingAccount, sendingCustomer, amount, date) VALUES (?, ?, ?, ?, ?, ?, ?)",
                transactionId, targetAccountId, targetCustomer.getCustomerId(), currentAccountId, currentCustomer.getCustomerId(), amount, LocalDateTime.now()
        );

        int newTargetAmount = jdbcTemplate.queryForObject(
                "SELECT balance FROM mydb.accounts WHERE partnerId=" + targetCustomer.getCustomerId() + " and accountId=" + targetAccountId,
                Integer.class);

        newTargetAmount += amount;

        jdbcTemplate.update("UPDATE mydb.accounts SET balance=" + newTargetAmount + " where partnerId=" + targetCustomer.getCustomerId() + " and accountId=" + targetAccountId);


        int newSendingAmount = jdbcTemplate.queryForObject(
                "SELECT balance FROM mydb.accounts WHERE partnerId=" + currentCustomer.getCustomerId() + " and accountId=" + currentAccountId,
                Integer.class);

        newSendingAmount -= amount;

        jdbcTemplate.update("UPDATE mydb.accounts SET balance=" + newSendingAmount + " where partnerId=" + currentCustomer.getCustomerId() + " and accountId=" + currentAccountId);

        transactionId += 1;
        modelAndView.setViewName("transaction_complete");
        return modelAndView;
    }

    @RequestMapping("/add_money_to_my_account/{accountIdValue}")
    public ModelAndView addMoneyToSelfAccountFields(@PathVariable("accountIdValue") Integer accountIdValue) {
        currentAccountId = accountIdValue;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create_addition_fields");
        return modelAndView;
    }

    @RequestMapping("/create_addition_amount")
    public ModelAndView addMoneyToMyAccount(@RequestParam(value = "amount") Integer amount) {
        int newSendingAmount = jdbcTemplate.queryForObject(
                "SELECT balance FROM mydb.accounts WHERE partnerId=" + currentCustomer.getCustomerId() + " and accountId=" + currentAccountId,
                Integer.class);

        newSendingAmount += amount;

        jdbcTemplate.update("UPDATE mydb.accounts SET balance=" + newSendingAmount + " where partnerId=" + currentCustomer.getCustomerId() + " and accountId=" + currentAccountId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("addition_successful");
        return modelAndView;
    }

    @RequestMapping("/complete_date_transaction_backend/{accountIdValue}")
    public ModelAndView completeDateTransactionBackend(@PathVariable("accountIdValue") Integer accountIdValue) {
        currentAccountId = accountIdValue;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("complete_date_transaction");
        return modelAndView;
    }

    @RequestMapping("/date_input_fields")
    public ModelAndView dateInputFields(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        startDateRequired = startDate;
        endDateRequired = endDate;
        return listTransactions();
    }

    public ModelAndView listTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ArrayList<Transaction> transactionsInTimeframe = new ArrayList<>();
        ArrayList<Customer> customers = new ArrayList<>();
        ModelAndView modelAndView = new ModelAndView();

        jdbcTemplate.query(
                "SELECT * FROM mydb.transactions WHERE sendingAccount=" + currentAccountId + " and sendingCustomer=" + currentCustomer.getCustomerId(),
                (rs, rowNum) -> new Transaction(rs.getInt("targetAccount"), rs.getInt("sendingAccount"),
                        rs.getFloat("amount"), rs.getInt("targetCustomer"),
                        rs.getInt("sendingCustomer"), rs.getInt("transactionId"))
        ).forEach(transaction -> transactions.add(transaction));

        String[] splittedStartDate = startDateRequired.split("T");
        String[] splittedEndDate = endDateRequired.split("T");

        String newStartDate = splittedStartDate[0] + " " + splittedStartDate[1];
        String newEndDate = splittedEndDate[0] + " " + splittedEndDate[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTimeStart = LocalDateTime.parse(newStartDate, formatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse(newEndDate, formatter);

        for(int i = 0; i < transactions.size(); i ++) {
            if(transactions.get(i).getDate().isAfter(dateTimeStart) && transactions.get(i).getDate().isBefore(dateTimeEnd))
                transactionsInTimeframe.add(transactions.get(i));
        }

        if(transactionsInTimeframe.size() == 0) {
            modelAndView.setViewName("no_transactions_to_show");
            return modelAndView;
        }

        for(int i = 0; i < transactionsInTimeframe.size(); i ++) {
            jdbcTemplate.query(
                    "SELECT * FROM mydb.customers WHERE customerId=" + transactions.get(i).getTargetCustomer(),
                    (rs, rowNum) -> new Customer(rs.getInt("customerId"), rs.getString("firstName"), rs.getString("lastName"),
                            rs.getString("address"), rs.getString("city"),
                            rs.getString("nationality"), rs.getString("userName"), rs.getString("password"))
            ).forEach(customer -> customers.add(customer));
        }

        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("customers", customers);
        modelAndView.setViewName("transactions_list");
        return modelAndView;
    }
}
