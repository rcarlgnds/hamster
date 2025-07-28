package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Pagination;
import com.example.hamster.data.model.User;

import java.io.Serializable;
import java.util.List;

public class UserResponse implements Serializable {
    private DataWrapper data;

    public DataWrapper getData() {
        return data;
    }

    public static class DataWrapper implements Serializable {
        private List<User> data;
        private Pagination pagination;

        public List<User> getData() {
            return data;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }
}