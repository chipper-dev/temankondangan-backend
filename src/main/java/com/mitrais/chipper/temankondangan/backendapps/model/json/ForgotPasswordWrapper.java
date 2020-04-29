package com.mitrais.chipper.temankondangan.backendapps.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ForgotPasswordWrapper {
    String email;
}
