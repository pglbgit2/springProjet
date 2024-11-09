package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long toUser;
    private Long fromUser;
    private String requestStr;

}
