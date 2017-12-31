package com.michalis.ekpaideytikologismiko;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class WallPost {

    public String postTitle, postBody, picUrl,userID;

    public WallPost() {
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostBody() {
        return postBody;
    }

    public String getPicUrl() { return picUrl;}

    public String getUserID() {
        return userID;
    }

    public WallPost(String postTitle, String postBody, String picUrl, String userID) {
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.picUrl = picUrl;
        this.userID = userID;
    }
}

