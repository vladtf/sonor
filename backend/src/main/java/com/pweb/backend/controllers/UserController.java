
package com.pweb.backend.controllers;

import com.pweb.backend.requests.TransactionRequest;
import com.pweb.backend.services.PostService;
import com.pweb.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


//    @PostMapping("/accounts")
//    public List<Post> createAccountForUserByToken(@RequestHeader("Authorization") String token, @RequestBody Post post) {
//        if (post.getIban() == null || post.getIban().equals("")) {
//            throw new RuntimeException("iban not provided");
//        }
//        return postService.createAccountForUserByToken(token, post);
//    }

//    @DeleteMapping("/accounts")
//    public List<Post> deleteAccountForUserByToken(@RequestHeader("Authorization") String token, @RequestBody Post post) {
//        if (post.getIban() == null || post.getIban().equals("")) {
//            throw new RuntimeException("iban not provided");
//        }
//        return postService.deleteAccountForUserByToken(token, post);
//    }

//    @GetMapping("/accounts")
//    public List<Post> getUserAccountsByToken(@RequestHeader("Authorization") String token) {
//        if (token == null || token.equals(""))
//            throw new RuntimeException("token doesn t exist");
//        return postService.getUserAccountsByToken(token);
//    }

//    @GetMapping("/accounts/all")
//    public List<Post> getAllAccounts() {
//        return postService.getAllAccounts();
//    }

//    @PutMapping("/accounts")
//    public Boolean updateAccount(@RequestHeader("Authorization") String token, @RequestBody Post post) {
//        if (post.getIban() == null || post.getIban().equals("")) {
//            throw new RuntimeException("iban not provided");
//        }
//
//        return postService.updateAccount(token, post);
//    }

    @PostMapping("/transaction")
    public String makeTransaction(@RequestBody TransactionRequest transactionRequest, @RequestHeader("Authorization") String token) {

        if (token == null || token.equals(""))
            throw new RuntimeException("token doesn t exist");

//        transactionService.makeTransaction(transactionRequest);
        return "succes";
    }

    @GetMapping("/emails")
    public List<String> getAllEmails(@RequestHeader("Authorization") String token) {
        return userService.getAllEmails(token);
    }

//    @GetMapping("/transactions")
//    public List<TransactionResponse> getAllTransactions(
//            @RequestHeader("Authorization") String token,
//            @RequestParam(value = "iban", required = false) String iban
//    ) {
//        if (token == null || token.equals("")) {
//            throw new RuntimeException("User not logged in");
//        }
//
//        User user = userService.getUserByToken(token);
//
//        return postService.findTransactionsByIban(iban, user);
//    }

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

