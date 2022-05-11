package com.mf.starter.domain.dto;

import com.mf.starter.domain.MfaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendTotpDto implements Serializable {
    private MfaType mfaType = MfaType.SMS;

    private String mfaId;
}
