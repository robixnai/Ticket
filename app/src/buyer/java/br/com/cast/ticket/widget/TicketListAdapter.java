package br.com.cast.ticket.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.Ticket;
import br.com.cast.ticket.entity.TicketType;
import br.com.cast.ticket.util.AppUtil;

/**
 * Ticket list adapter.
 *
 * @author falvojr
 * @author Robson Moreira
 *
 */
public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.ViewHolder> {

    private List<Ticket> mItens;
    private OnTicketClickListener mOnTicketClickListener;

    public TicketListAdapter(List<Ticket> itens) {
        mItens = itens;
    }

    public TicketListAdapter(List<Ticket> mItens, OnTicketClickListener mOnTicketClickListener) {
        this.mItens = mItens;
        this.mOnTicketClickListener = mOnTicketClickListener;
    }

    public void setItens(List<Ticket> itens) {
        this.mItens = itens;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.ticket_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Ticket ticket = mItens.get(position);

        try {
            //holder.itemView.setBackgroundResource(R.drawable.cardview_ticket_background);
        } catch (ClassCastException e) {
            //FIXME http://stackoverflow.com/q/26399670/3072570
        }

        if(TicketType.BUS.equals(ticket.getType())) {
            holder.mTextViewAlias.setText(ticket.getAlias());
            holder.mTextViewAlias.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_bus, 0, 0, 0);
        } else {
            holder.mTextViewAlias.setText(ticket.getAlias());
            holder.mTextViewAlias.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_subway, 0, 0, 0);
        }

        holder.mTextViewDate.setText(AppUtil.formatDate(ticket.getData().getExpiration()));
        holder.mTextViewDate.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_calendar, 0, 0, 0);
        holder.mTextViewHour.setText(AppUtil.formatTime(ticket.getData().getExpiration()));
        holder.mTextViewHour.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_calendar_clock, 0, 0, 0);
        holder.mTextViewQuantity.setText(ticket.getData().getQuantity().toString());
        holder.mTextViewQuantity.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ticket_quantity, 0, 0, 0);

        holder.mImageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTicketClickListener != null) {
                    mOnTicketClickListener.onLocationClick(ticket);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTicketClickListener != null) {
                    mOnTicketClickListener.onCardClick(ticket);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItens == null ? 0 : mItens.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.mImageViewLocation.setOnClickListener(null);
        super.onViewRecycled(holder);
    }

    public interface OnTicketClickListener {
        public void onLocationClick(Ticket ticket);
        public void onCardClick(Ticket ticket);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextViewAlias;
        private final TextView mTextViewDate;
        private final TextView mTextViewHour;
        private final TextView mTextViewQuantity;
        private final ImageView mImageViewLocation;

        public ViewHolder(final View view) {
            super(view);
            mTextViewAlias = AppUtil.get(view.findViewById(R.id.textViewAlias));
            mTextViewDate = AppUtil.get(view.findViewById(R.id.textViewDate));
            mTextViewHour = AppUtil.get(view.findViewById(R.id.textViewHour));
            mTextViewQuantity = AppUtil.get(view.findViewById(R.id.textViewQuantity));
            mImageViewLocation = AppUtil.get(view.findViewById(R.id.imageViewLocation));
        }
    }
}
