package mate.academy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.model.Status;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull
    private Status status;
}

