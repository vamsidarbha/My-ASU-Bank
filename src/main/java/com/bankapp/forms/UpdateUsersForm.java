package com.bankapp.forms;

import java.util.ArrayList;
import java.util.List;

import com.bankapp.models.User;

public class UpdateUsersForm {

    List<User> users;

    public UpdateUsersForm() {
        this.users = new ArrayList<User>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
