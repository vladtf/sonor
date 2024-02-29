
package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Account;
import com.pweb.backend.dao.entities.Token;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.requests.LoginRequest;
import com.pweb.backend.requests.RegisterRequest;
import com.pweb.backend.requests.TransactionRequest;
import com.pweb.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final TransactionService transactionService;

    private final AccountService accountService;

    private final ExchangeService exchangeService;

    private final NewsService newsService;

    @Autowired
    public UserController(UserService userService, TransactionService transactionService, AccountService accountService, ExchangeService exchangeService, NewsService newsService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.exchangeService = exchangeService;
        this.newsService = newsService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null || registerRequest.getEmail().equals("")) {
            throw new RuntimeException("email not provided");
        }

        return userService.registerUser(registerRequest);
    }


    @PostMapping("/accounts")
    public List<Account> createAccountForUserByToken(@RequestHeader("Authorization") String token, @RequestBody Account account) {
        if (account.getIban() == null || account.getIban().equals("")) {
            throw new RuntimeException("iban not provided");
        }
        return accountService.createAccountForUserByToken(token, account);
    }

    @DeleteMapping("/accounts")
    public List<Account> deleteAccountForUserByToken(@RequestHeader("Authorization") String token, @RequestBody Account account) {
        if (account.getIban() == null || account.getIban().equals("")) {
            throw new RuntimeException("iban not provided");
        }
        return accountService.deleteAccountForUserByToken(token, account);
    }

    @GetMapping("/accounts")
    public List<Account> getUserAccountsByToken(@RequestHeader("Authorization") String token) {
        if (token == null || token.equals(""))
            throw new RuntimeException("token doesn t exist");
        return accountService.getUserAccountsByToken(token);
    }

    @GetMapping("/accounts/all")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PutMapping("/accounts")
    public Boolean updateAccount(@RequestHeader("Authorization") String token, @RequestBody Account account) {
        if (account.getIban() == null || account.getIban().equals("")) {
            throw new RuntimeException("iban not provided");
        }

        return accountService.updateAccount(token, account);
    }

    @PostMapping("/transaction")
    public String makeTransaction(@RequestBody TransactionRequest transactionRequest, @RequestHeader("Authorization") String token) {

        if (token == null || token.equals(""))
            throw new RuntimeException("token doesn t exist");

        transactionService.makeTransaction(transactionRequest);
        return "succes";
    }

    @GetMapping("/emails")
    public List<String> getAllEmails(@RequestHeader("Authorization") String token) {
        return userService.getAllEmails(token);
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getAllTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "iban", required = false) String iban
    ) {
        if (token == null || token.equals("")) {
            throw new RuntimeException("User not logged in");
        }

        User user = userService.getUserByToken(token);

        return accountService.findTransactionsByIban(iban, user);
    }

    @GetMapping("/exchange")
    public List<ExchangeService.ExchangeResponse> getExchangeRates() {
        return exchangeService.getExchangeRates();
    }

    @GetMapping("/news")
    @Secured("ADMIN")
    public List<NewsService.NewsResponse> getNews() {
        return newsService.getNews();
    }


    public static class TransactionResponse {
        private final Double sum;
        private final String sourceAccount;
        private final String destAccount;
        private final TransactionType transactionType;

        private final Date createdAt;

        public Double getSum() {
            return sum;
        }

        public String getSourceAccount() {
            return sourceAccount;
        }

        public String getDestAccount() {
            return destAccount;
        }

        public TransactionType getTransactionType() {
            return transactionType;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public TransactionResponse(Double sum, String sourceAccount, String destAccount, TransactionType transactionType, Date createdAt) {
            this.sum = sum;
            this.sourceAccount = sourceAccount;
            this.destAccount = destAccount;
            this.transactionType = transactionType;
            this.createdAt = createdAt;
        }

    }

}

