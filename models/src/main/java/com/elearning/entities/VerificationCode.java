package com.elearning.entities;

import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "verification_code")

public class VerificationCode extends IBaseEntity{

    private String code;
    private EnumVerificationCode type;
    private Boolean isConfirmed;
    private String parentId;
    private Date expiredAt;
    private Date confirmedAt;
    private String sendTo;
}
