package ru.practicum.shareitgateway.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class UserDTO {

    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
