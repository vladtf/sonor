package com.pweb.backend.services;

import com.pweb.backend.controllers.PostController;
import com.pweb.backend.dao.entities.Post;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.PostRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getAllPosts(org.springframework.security.core.userdetails.User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        return postRepository.findAllByUser(userOptional.get());
    }

    public List<Post> createPost(org.springframework.security.core.userdetails.User user, PostController.NewPostRequest newPostRequest) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        User user1 = userOptional.get();
        Post post = new Post();
        post.setTitle(newPostRequest.title);
        post.setContent(newPostRequest.content);
        post.setCategory(newPostRequest.category);
        post.setUser(user1);
        postRepository.save(post);
        return getAllPosts(user);
    }

//    public List<Post> getUserAccountsByToken(String token) {
//        User user = userService.getUserByToken(token);
//        //return user.getAccounts();
//
//        List<Post> posts = user.getAccounts();
//        for (Post post1 : posts) {
//            post1.setUser(null);
//        }
//        return posts;
//    }
//
//    public List<Post> createAccountForUserByToken(String token, Post post) {
//        User user = userService.getUserByToken(token);
//        // user.getAccounts().add(account);
//        post.setUser(user);
//        accountRepository.save(post);
//
//        List<Post> posts = accountRepository.findAccountsByUser(user);
//        posts.forEach(account1 -> account1.setUser(null));
//
//        return posts;
//    }
//
//    public List<UserController.TransactionResponse> findTransactionsByIban(String iban, User user) {
//
//        if (iban == null) {
//            List<UserController.TransactionResponse> allTransactions = new ArrayList<>();
//
//            List<Post> posts = accountRepository.findAccountsByUser(user);
//
//            for (Post post : posts) {
//                allTransactions.addAll(findTransactionsByIban(post.getIban(), user));
//            }
//
//            return allTransactions;
//        }
//
//        Optional<Post> accountOptional = accountRepository.findAccountByIban(iban);
//        if (accountOptional.isEmpty()) {
//            throw new RuntimeException("Account not found.");
//        }
//
//        Post post = accountOptional.get();
//        List<Transaction> allByDestAccount = transactionRepository.findAllByDestAccount(post);
//        List<Transaction> allBySourceAccount = transactionRepository.findAllBySourceAccount(post);
//
//        List<Transaction> allTransactions = new ArrayList<>();
//        allTransactions.addAll(allByDestAccount);
//        allTransactions.addAll(allBySourceAccount);
//
//        List<UserController.TransactionResponse> results = new ArrayList<>();
//        for (Transaction transaction : allTransactions) {
//            TransactionType transactionType = (transaction.getSourceAccount().getIban().equals(iban))
//                    ? TransactionType.OUTCOME : TransactionType.INCOME;
//
//            String sourceAccount = (transaction.getSourceAccount().getIban().equals(iban))
//                    ? transaction.getSourceAccount().getIban() : transaction.getDestAccount().getIban();
//
//            results.add(new UserController.TransactionResponse(
//                    transaction.getSum(),
//                    sourceAccount, transaction.getDestAccount().getIban(),
//                    transactionType,
//                    transaction.getCreatedAt()
//            ));
//        }
//
//        return results;
//    }
//
//
//    public List<Post> deleteAccountForUserByToken(String token, Post post) {
//        User user = userService.getUserByToken(token);
//
//        if (post.getIban() == null || post.getIban().equals("")) {
//            throw new RuntimeException("iban not provided");
//        }
//
//        Optional<Post> accountOptional = accountRepository.findAccountByIban(post.getIban());
//        if (accountOptional.isEmpty())
//            throw new RuntimeException("account not found.");
//
//        Post postToDelete = accountOptional.get();
//        if (!postToDelete.getUser().getId().equals(user.getId()))
//            throw new RuntimeException("account not found.");
//
//        postToDelete.setUser(null);
//        accountRepository.delete(postToDelete);
//
//        List<Post> posts = accountRepository.findAccountsByUser(user);
//        posts.forEach(account1 -> account1.setUser(null));
//
//        return posts;
//    }
//
//    public List<Post> getAllAccounts() {
//        Iterable<Post> all = accountRepository.findAll();
//        List<Post> allPosts = new ArrayList<>();
//        all.forEach(allPosts::add);
//        allPosts.forEach(account -> account.setUser(null));
//        return allPosts;
//    }
//
//    public boolean updateAccount(String token, Post post) {
//        Post postToUpdate = accountRepository.findAccountByIban(post.getIban()).orElseThrow(() -> new RuntimeException("Account not found."));
//
//        if (postToUpdate.getUser() == null) {
//            throw new RuntimeException("Account not found.");
//        }
//
//        postToUpdate.setBalance(post.getBalance());
//
//        accountRepository.save(postToUpdate);
//        return true;
//    }


}
