package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.annotation.Column;
import org.example.annotation.Id;
import org.example.annotation.Table;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(value = "worker")
public class Worker {
    @Id
    private Long id;
    private String name;
    private LocalDate birthday;
    private Level level;
    private int salary;

    public enum Level {
        TRAINEE,
        JUNIOR,
        MIDDLE,
        SENIOR;
        public String getDbValue() {
            return this.name().charAt(0) + this.name().substring(1).toLowerCase();
        }

    }

    private Worker(Builder builder) {
        this.name = builder.name;
        this.birthday = builder.birthday;
        this.level = builder.level;
        this.salary = builder.salary;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long id;
        private String name;
        private LocalDate birthday;
        private Level level;
        private int salary;

        public Builder setName(String name) {
            if (name.length() < 2 || name.length() > 1000) {
                throw new IllegalArgumentException("Name length must be between 2 and 1000 characters.");
            }
            this.name = name;
            return this;
        }

        public Builder setBirthday(LocalDate birthday) {
            if (birthday.getYear() <= 1900) {
                throw new IllegalArgumentException("Year of birth must be greater than 1900.");
            }
            this.birthday = birthday;
            return this;
        }

        public Builder setLevel(Level level) {
            this.level = level;
            return this;
        }

        public Builder setSalary(int salary) {
            if (salary < 100 || salary > 100000) {
                throw new IllegalArgumentException("Salary must be between 100 and 100000.");
            }
            this.salary = salary;
            return this;
        }

        public Worker build() {
            return new Worker(this);
        }
    }
}
