package com.softserve.edu.service.utils;


import org.apache.commons.lang3.builder.ToStringBuilder;

public class BBIOutcomeDTO {

    public enum ReasonOfRejection{
        NO_CORRESPONDING_VERIFICATION,
        BBI_IS_NOT_VALID,
        INVALID_VERIFICATION_CODE,
        INVALID_COUNTER_SIZE_AND_SYMBOL,
        BBI_FILE_IS_ALREADY_IN_DATABASE,
        WRONG_IMAGE_IN_BBI,
        NAME_OF_BBI_FILE_DOES_NOT_MATCH,
        WRONG_MODULE_ID
    }

    private String verificationID;
    private String bbiFileName;
    private boolean success;

    public String getReasonOfRejection() {
        return reasonOfRejection;
    }

    public void setReasonOfRejection(String reasonOfRejection) {
        this.reasonOfRejection = reasonOfRejection;
    }

    private String reasonOfRejection;

    public String getVerificationID() {
        return verificationID;
    }

    public void setVerificationID(String verificationID) {
        this.verificationID = verificationID;
    }

    public String getBbiFileName() {
        return bbiFileName;
    }

    public void setBbiFileName(String bbiFileName) {
        this.bbiFileName = bbiFileName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private BBIOutcomeDTO(){

    }

    private BBIOutcomeDTO(String bbiFileName, String verificationID, boolean success) {

        this.verificationID = verificationID;
        this.bbiFileName = bbiFileName;
        this.success = success;
    }

    private BBIOutcomeDTO(String bbiFileName, String verificationID, boolean success, ReasonOfRejection reasonOfRejection) {

        this.verificationID = verificationID;
        this.bbiFileName = bbiFileName;
        this.success = success;
        this.reasonOfRejection = reasonOfRejection.toString();
    }

    public static BBIOutcomeDTO reject(String bbiFileName, String verificationID, ReasonOfRejection reasonOfRejection){
        return new BBIOutcomeDTO(bbiFileName, verificationID, false, reasonOfRejection);
    }

    public static BBIOutcomeDTO accept(String bbiFileName, String verificationID){
        return new BBIOutcomeDTO(bbiFileName, verificationID, true);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("verificationID", verificationID)
                .append("bbiFileName", bbiFileName)
                .append("success", success)
                .append("reasonOfRejection", reasonOfRejection)
                .toString();
    }
}
