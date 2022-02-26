package it.raccoon.library.domain;

public enum Filter {
    NAME("name"),
    AUTHOR("author"),
    GENRE("genre");

    private final String name;

    Filter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
