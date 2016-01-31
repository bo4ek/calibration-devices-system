package com.softserve.edu.controller;

import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/certificate")
public class CertificateController {

    @Autowired
    VerificationService verificationService;

    /**
     * Checks whether verification with {@code verificationId} id is signed
     * @param verificationId id of verification
     * @return  {@literal true} if verification certificate is signed, or otherwise {@literal false}
     */
    @RequestMapping(value = "isSigned/{verificationId}", method = RequestMethod.GET)
    public Boolean isCertificateSigned(@PathVariable
                                       String verificationId) {
        Verification verification = verificationService.findById(verificationId);
        if(verification != null) {
            return verification.isSigned();
        }
        return false;
    }
}
