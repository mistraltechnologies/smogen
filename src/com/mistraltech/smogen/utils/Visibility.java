package com.mistraltech.smogen.utils;

public enum Visibility {
    PUBLIC(3), PROTECTED(2), PACKAGE_PRIVATE(1), PRIVATE(0);

    int rank;

    Visibility(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
