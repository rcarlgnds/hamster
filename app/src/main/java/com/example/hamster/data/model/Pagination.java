package com.example.hamster.data.model;

import java.io.Serializable;

public class Pagination implements Serializable {
    private int currentPage;
    private int perPage;
    private int total;
    private int totalPages;

    // Getters
    public int getCurrentPage() { return currentPage; }
    public int getPerPage() { return perPage; }
    public int getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
}