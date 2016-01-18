package br.com.cast.ticket.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ezequielmessore
 */
public class User implements Parcelable {

    private Integer mId;
    private String mFirstName;
    private String mLastName;
    private String mCellPhone;
    private String mEmail;
    private String mPassword;
    private String mAccessToken;
    private String mPhotoUrl;


    public User(Integer id,String firstName, String lastName, String cellPhone, String email, String password, String accessToken, String photoUrl) {
        this.mId = id;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mCellPhone = cellPhone;
        this.mEmail = email;
        this.mPassword = password;
        this.mAccessToken = accessToken;
        this.mPhotoUrl = photoUrl;
    }

    public User(){}

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getCellPhone() {
        return mCellPhone;
    }

    public void setCellPhone(String mCellPhone) {
        this.mCellPhone = mCellPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getAccessToken() { return this.mAccessToken; }

    public void setAccessToken(String mAccessToken) { this.mAccessToken = mAccessToken; }

    public String getmPhotoUrl() { return this.mPhotoUrl; }

    public void setPhotoUrl(String mPhotoUrl) { this.mPhotoUrl = mPhotoUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mFirstName);
        dest.writeString(this.mLastName);
        dest.writeString(this.mCellPhone);
        dest.writeString(this.mEmail);
        dest.writeString(this.mPassword);
        dest.writeString(this.mAccessToken);
        dest.writeString(this.mPhotoUrl);
    }

    protected User(Parcel in) {
        this.mFirstName = in.readString();
        this.mLastName = in.readString();
        this.mCellPhone = in.readString();
        this.mEmail = in.readString();
        this.mPassword = in.readString();
        this.mAccessToken = in.readString();
        this.mPhotoUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
