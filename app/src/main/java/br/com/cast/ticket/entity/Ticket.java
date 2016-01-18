package br.com.cast.ticket.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Ticket entity.
 *
 * @author maikotrindade
 * @author falvojr
 */
public class Ticket implements Parcelable {

    private Integer mId;
    private String mAlias;
    private TicketTaxType mTaxType;
    private TicketData mData;
    private TicketType mType;

    /* MOCK */
    public List<Ticket> getAll(TicketType ticketType) {
        List<Ticket> tickets = new ArrayList<>();
        TicketData ticketData;

        if (ticketType.equals(TicketType.BUS)) {
            for (int i = 1; i < 4; i++) {
                tickets.add(createMock(ticketType, i));
            }
        } else {
            for (int i = 1; i < 3; i++) {
                tickets.add(createMock(ticketType, i));
            }
        }

        return tickets;
    }

    @NonNull
    private Ticket createMock(TicketType ticketType, int i) {
        TicketData ticketData;
        Ticket ticket = new Ticket();
        ticketData = new TicketData();

        try {
            ticketData.setExpiration(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(String.format("%1$02d/12/2015 %1$02d:55:44", i)));
        } catch (Exception e) {
            ticketData.setExpiration(new Date());
        }
        ticketData.setQuantity(i * i);
        if (i == 1) {
            ticket.setAlias("Bilhetes Infantis");
            ticketData.setPassengerType(TicketPassengerType.CHILD);
        } else if (i == 2) {
            ticket.setAlias("Bilhetes de Adulto");
            ticketData.setPassengerType(TicketPassengerType.ADULT);
        } else if (i == 3) {
            ticket.setAlias("Bilhetes de Idoso");
            ticketData.setPassengerType(TicketPassengerType.ELDERLY);
        }
        ticket.setData(ticketData);
        ticket.setType(ticketType);
        return ticket;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        this.mAlias = alias;
    }

    public TicketTaxType getTaxType() {
        return mTaxType;
    }

    public void setTaxType(TicketTaxType taxType) {
        this.mTaxType = taxType;
    }

    public TicketData getData() {
        return mData;
    }

    public void setData(TicketData data) {
        this.mData = data;
    }

    public TicketType getType() {
        return mType;
    }

    public void setType(TicketType type) {
        this.mType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;

        Ticket ticket = (Ticket) o;

        if (!mId.equals(ticket.mId)) return false;
        if (!mAlias.equals(ticket.mAlias)) return false;
        if (mTaxType != ticket.mTaxType) return false;
        if (!mData.equals(ticket.mData)) return false;
        return mType == ticket.mType;

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mAlias.hashCode();
        result = 31 * result + mTaxType.hashCode();
        result = 31 * result + mData.hashCode();
        result = 31 * result + mType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "mId=" + mId +
                ", mAlias='" + mAlias + '\'' +
                ", mTaxType=" + mTaxType +
                ", mData=" + mData +
                ", mType=" + mType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mId);
        dest.writeString(this.mAlias);
        dest.writeInt(this.mTaxType == null ? -1 : this.mTaxType.ordinal());
        dest.writeParcelable(this.mData, 0);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
    }

    public Ticket() {
    }

    protected Ticket(Parcel in) {
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mAlias = in.readString();
        int tmpMTaxType = in.readInt();
        this.mTaxType = tmpMTaxType == -1 ? null : TicketTaxType.values()[tmpMTaxType];
        this.mData = in.readParcelable(TicketData.class.getClassLoader());
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : TicketType.values()[tmpMType];
    }

    public static final Parcelable.Creator<Ticket> CREATOR = new Parcelable.Creator<Ticket>() {
        public Ticket createFromParcel(Parcel source) {
            return new Ticket(source);
        }

        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

}
