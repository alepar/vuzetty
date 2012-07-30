package ru.alepar.vuzetty.common.api;

import java.io.Serializable;

public class Category implements Serializable {

    public final String name;

    public Category(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category that = (Category) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Category{'" +
                 name + '\'' +
                '}';
    }
}
