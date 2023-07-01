package ru.practicum.shareitserver.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class UserDTO {

    private Long id;

    private String name;

    private String email;
}
