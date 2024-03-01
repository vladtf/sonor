//package com.pweb.backend.dao.entities;
//
//import jakarta.persistence.*;
//
//import java.sql.Date;
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "transactions")
//public class Transaction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "transaction_id")
//    private Integer id;
//
//    private Double sum;
//
//    private Date createdAt;
//
//
//    @ManyToOne
//    @JoinColumn(name = "account_id_source")
//    private Post sourcePost;
//
//    @ManyToOne
//    @JoinColumn(name = "account_id_dest")
//    private Post destPost;
//
//
//    public Transaction() {
//        LocalDate currentDate = LocalDate.now();
//        this.createdAt = Date.valueOf(currentDate);
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Double getSum() {
//        return sum;
//    }
//
//    public void setSum(Double sum) {
//        this.sum = sum;
//    }
//
//    public Post getSourceAccount() {
//        return sourcePost;
//    }
//
//    public void setSourceAccount(Post sourcePost) {
//        this.sourcePost = sourcePost;
//    }
//
//    public Post getDestAccount() {
//        return destPost;
//    }
//
//    public void setDestAccount(Post destPost) {
//        this.destPost = destPost;
//    }
//
//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Date createdAt) {
//        this.createdAt = createdAt;
//    }
//}
