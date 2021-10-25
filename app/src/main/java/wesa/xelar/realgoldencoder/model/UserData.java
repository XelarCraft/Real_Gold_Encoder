package wesa.xelar.realgoldencoder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("ID")
    @Expose
    private int id;
    @SerializedName("given_names")
    @Expose
    private String givenNames;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("passport")
    @Expose
    private String passport;
    @SerializedName("visa_number")
    @Expose
    private String visaNumber;
    @SerializedName("valid_from")
    @Expose
    private String validFrom;
    @SerializedName("valid_to")
    @Expose
    private String validTo;
    @SerializedName("foreigner_no")
    @Expose
    private String foreignerNo;
    @SerializedName("registration_date")
    @Expose
    private String registrationDate;
    @SerializedName("sponsor_name")
    @Expose
    private String sponsorName;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("photo")
    @Expose
    private String photo;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserData() {
    }

    /**
     *
     * @param givenNames
     * @param visaNumber
     * @param sex
     * @param sponsorName
     * @param photo
     * @param validFrom
     * @param passport
     * @param foreignerNo
     * @param nationality
     * @param surname
     * @param registrationDate
     * @param id
     * @param validTo
     */
    public UserData(int id, String givenNames, String surname, String passport, String visaNumber, String validFrom, String validTo, String foreignerNo, String registrationDate, String sponsorName, String nationality, String sex, String photo) {
        super();
        this.id = id;
        this.givenNames = givenNames;
        this.surname = surname;
        this.passport = passport;
        this.visaNumber = visaNumber;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.foreignerNo = foreignerNo;
        this.registrationDate = registrationDate;
        this.sponsorName = sponsorName;
        this.nationality = nationality;
        this.sex = sex;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getVisaNumber() {
        return visaNumber;
    }

    public void setVisaNumber(String visaNumber) {
        this.visaNumber = visaNumber;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getForeignerNo() {
        return foreignerNo;
    }

    public void setForeignerNo(String foreignerNo) {
        this.foreignerNo = foreignerNo;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}