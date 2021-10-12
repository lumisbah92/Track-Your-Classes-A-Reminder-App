package com.example.firebaselearning;

public class PostingModel {
    private String post;

    public PostingModel(){

    }

    public PostingModel(String post) {
        this.post = post;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
