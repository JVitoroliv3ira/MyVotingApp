package api.dtos.responses;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO<C> {
    private final C content;
    private final List<String> errors;
}
