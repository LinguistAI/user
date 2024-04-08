package app.linguistai.bmvp.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QUserSearch {
    @NotBlank
    private String username;

    @Min(value = 0, message = "Min page number can be 0")
    private Integer page = 0; // default value for page if page is not given

    @Min(value = 1, message = "Min page size can be 1")
    private Integer size = 10; // default value for size if size is not given
}