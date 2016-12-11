package com.rambabusaravanan.simplenotes.model;

import com.backendless.Backendless;

import java.io.Serializable;

/**
 * Created by androbabu on 10/12/16.
 */

public class Notes implements Serializable {

    public String objectId;
    public String userId;
    public String title, message;

    public Notes() {
        userId = Backendless.UserService.loggedInUser();
    }

    public Notes(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
