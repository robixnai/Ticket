package br.com.cast.ticket.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.TicketApplication;

/**
 * Ticket tax types.
 *
 * @author maikotrindade
 * @author falvojr
 */
public enum TicketTaxType {

    UNIQUE(1, R.string.enum_tax_unique), HOURLY(2, R.string.enum_tax_hourly), DAILY(3, R.string.enum_tax_daily), WEEKEND(4, R.string.enum_tax_weekend), AREA(5, R.string.enum_tax_area);

    private static final Map<Integer, TicketTaxType> sLookup = new HashMap<Integer, TicketTaxType>();

    static {
        for (TicketTaxType menu : EnumSet.allOf(TicketTaxType.class)) {
            sLookup.put(menu.getId(), menu);
        }
    }

    private int mId;
    private int mDescriptionRes;

    TicketTaxType(final int id, final int descriptionRes) {
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

    public static TicketTaxType valueOf(int id) {
        return sLookup.get(id);
    }

    public static TicketTaxType getInstance(String name) {
        for (TicketTaxType type : values()) {
            if (TicketApplication.getContext().getString(type.mDescriptionRes).equals(name)) {
                return type;
            }
        }
        return null;
    }

}
