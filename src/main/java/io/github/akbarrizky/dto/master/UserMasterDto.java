package io.github.akbarrizky.dto.master;

public class UserMasterDto {

    public Long id;
    public String email;
    public String name;

    public UserMasterDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
