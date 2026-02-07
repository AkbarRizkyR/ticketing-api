package io.github.akbarrizky.dto.master;

public class UserMasterDto {

    public java.util.UUID id;
    public String email;
    public String name;

    public UserMasterDto(java.util.UUID id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
