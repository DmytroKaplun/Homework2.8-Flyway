package org.example.entity;

import lombok.*;
import org.example.annotation.Id;
import org.example.annotation.Table;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(value = "client")
public class Client {
    @Id
    private Long id;
    @Setter
    private String name;

    private Client(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Long id;

        public Builder setName(String name) {
            if (name.length() < 2 || name.length() > 1000) {
                throw new IllegalArgumentException("Name length must be between 2 and 1000 characters.");
            }
            this.name = name;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }
}
