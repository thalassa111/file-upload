package com.me.chengspringboot.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDto {
    public String name;
    public String email;
    public String address;
    public String password;

    public UserDto(String name, String email, String address, String password) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password = password;
    }
}
