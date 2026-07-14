package dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import util.KycStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CutsomerCreatedResponse {
    private String externalId;
    private KycStatus KycStatus;
    private Integer version;
    private LocalDateTime createdAt;
}
