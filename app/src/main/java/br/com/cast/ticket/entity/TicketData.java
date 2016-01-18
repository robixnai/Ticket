package br.com.cast.ticket.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Ticket data for composition on {@link Ticket} entity.
 *
 * @author maikotrindade
 * @author falvojr
 * @author ezequielmessore
 */
public class TicketData implements Parcelable {

    private BigDecimal mUnitTax;
    private Integer mQuantity;
    private String mQrCode;
    private Date mExpiration;
    private TicketPassengerType mPassengerType;
    private Date mLastUsage;
    private String mLine;

    public BigDecimal getUnitTax() {
        return mUnitTax;
    }

    public void setUnitTax(BigDecimal mUnitTax) {
        this.mUnitTax = mUnitTax;
    }

    public Integer getQuantity() {
        return mQuantity;
    }

    public void setQuantity(Integer mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getQrCode() {
        return mQrCode;
    }

    public void setQrCode(String mQrCode) {
        this.mQrCode = mQrCode;
    }

    public Date getExpiration() {
        return mExpiration;
    }

    public void setExpiration(Date mExpiration) {
        this.mExpiration = mExpiration;
    }

    public TicketPassengerType getPassengerType() {
        return mPassengerType;
    }

    public void setPassengerType(TicketPassengerType mPassengerType) {
        this.mPassengerType = mPassengerType;
    }

    public Date getLastUsage() {
        return mLastUsage;
    }

    public void setLastUsage(Date mLastUsage) {
        this.mLastUsage = mLastUsage;
    }

    public String getLine() {
        return mLine;
    }

    public void setLine(String mLine) {
        this.mLine = mLine;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mUnitTax);
        dest.writeValue(this.mQuantity);
        dest.writeString(this.mQrCode);
        dest.writeLong(mExpiration != null ? mExpiration.getTime() : -1);
        dest.writeInt(this.mPassengerType == null ? -1 : this.mPassengerType.ordinal());
        dest.writeLong(mLastUsage != null ? mLastUsage.getTime() : -1);
        dest.writeString(this.mLine);
    }

    public TicketData() {
    }

    protected TicketData(Parcel in) {
        this.mUnitTax = (BigDecimal) in.readSerializable();
        this.mQuantity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mQrCode = in.readString();
        long tmpMExpiration = in.readLong();
        this.mExpiration = tmpMExpiration == -1 ? null : new Date(tmpMExpiration);
        int tmpMPassengerType = in.readInt();
        this.mPassengerType = tmpMPassengerType == -1 ? null : TicketPassengerType.values()[tmpMPassengerType];
        long tmpMLastUsage = in.readLong();
        this.mLastUsage = tmpMLastUsage == -1 ? null : new Date(tmpMLastUsage);
        this.mLine = in.readString();
    }

    public static final Creator<TicketData> CREATOR = new Creator<TicketData>() {
        public TicketData createFromParcel(Parcel source) {
            return new TicketData(source);
        }

        public TicketData[] newArray(int size) {
            return new TicketData[size];
        }
    };
}
