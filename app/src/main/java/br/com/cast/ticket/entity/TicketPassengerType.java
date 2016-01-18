package br.com.cast.ticket.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.TicketApplication;

/**
 * Ticket passenger types.
 *
 * @author maikotrindade
 * @author falvojr
 */
public enum TicketPassengerType {

    CHILD(1, R.string.enum_passager_child), ADULT(2, R.string.enum_passager_adult), ELDERLY(3, R.string.enum_passager_elderly);

    private static final Map<Integer,TicketPassengerType> sLookup = new HashMap<Integer,TicketPassengerType>();

    static {
        for(TicketPassengerType menu : EnumSet.allOf(TicketPassengerType.class)) {
            sLookup.put(menu.getId(), menu);
        }
    }

    private int mId;
    private int mDescriptionRes;

    TicketPassengerType(final int id, final int descriptionRes) {
        mId = id;
        mDescriptionRes = descriptionRes;
    }

    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return TicketApplication.getContext().getString(mDescriptionRes);
    }

    public static TicketPassengerType valueOf(int id) {
        return sLookup.get(id);
    }

    public static TicketPassengerType getInstance(String name) {
        for (TicketPassengerType type : values()) {
            if (TicketApplication.getContext().getString(type.mDescriptionRes).equals(name)) {
                return type;
            }
        }
        return null;
    }
}


