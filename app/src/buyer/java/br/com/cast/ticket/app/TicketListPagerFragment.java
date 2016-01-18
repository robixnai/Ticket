package br.com.cast.ticket.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.Ticket;
import br.com.cast.ticket.entity.TicketType;
import br.com.cast.ticket.util.MyLocation;
import br.com.cast.ticket.widget.TicketListAdapter;

/**
 * Created by robson on 31/07/15.
 */
public class TicketListPagerFragment extends Fragment {

    private static final String TAG = TicketListPagerFragment.class.getSimpleName();

    public final static String ITEMS_COUNT_KEY = "TicketListPagerFragment$ItemsCount";
    public static final String ANDROID_APPS_MAPS = "com.google.android.apps.maps";

    private RecyclerView mRecyclerView;
    private Handler mHandler = new Handler();

    public static TicketListPagerFragment createInstance(List<Ticket> ticketList) {
        TicketListPagerFragment partThreeFragment = new TicketListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ITEMS_COUNT_KEY, (ArrayList) ticketList);
        partThreeFragment.setArguments(bundle);
        return partThreeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_ticket_list_pager, container, false);
        return mRecyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        final Bundle bundle = getArguments();
        final List<Ticket> ticketList = bundle.getParcelableArrayList(ITEMS_COUNT_KEY);
        final MyLocation location = new MyLocation();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TicketListAdapter ticketListAdapter = new TicketListAdapter(ticketList, new TicketListAdapter.OnTicketClickListener() {
            @Override
            public void onLocationClick(Ticket ticket) {
                Uri gmmIntentUri;
                if (TicketType.BUS.equals(ticket.getType())) {
                    final String uriCordinatesBus = "geo:" + location.getLatitude(getActivity()) + "," + location.getLongitude(getActivity()) + "?q=" + getString(R.string.tab_bus);
                    gmmIntentUri = Uri.parse(uriCordinatesBus);
                } else {
                    final String uriCordinatesSubway = "geo:" + location.getLatitude(getActivity()) + "," + location.getLongitude(getActivity()) + "?q=" + getString(R.string.tab_subway);
                    gmmIntentUri = Uri.parse(uriCordinatesSubway);
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(ANDROID_APPS_MAPS);
                startActivity(mapIntent);
            }

            @Override
            public void onCardClick(Ticket ticket) {
                try {
                    // Create ObjectMapper instance
                    final ObjectMapper objectMapper = new ObjectMapper();
                    // Configure Object mapper for pretty print
                    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                    // Writing to console, can write to any output stream such as file
                    final StringWriter ticketJsonWritter = new StringWriter();
                    objectMapper.writeValue(ticketJsonWritter, ticket);
                    final String json = ticketJsonWritter.toString();
                    showDialogQrCode(json, ticket.getAlias());
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        });
        recyclerView.setAdapter(ticketListAdapter);
        ticketListAdapter.notifyDataSetChanged();
    }

    //FIXME corrigir bug na mudaçao de orientaçao e utilizar interface para geração de QRCode
    public void showDialogQrCode(final String json, final String alias) {
        final Dialog dialog = new Dialog(mRecyclerView.getContext());
        dialog.setContentView(R.layout.dialog_qrcode);
        dialog.setTitle(alias);
        final ImageView imgQRCode = (ImageView) dialog.findViewById(R.id.qr_code_image);

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                //TODO medir o tamanho da tela e gerar QRCODE com largura e altura dinamicamente
                int qrCodeWidth = 450;
                int qrCodeHeight = 450;

                try {
                    BitMatrix bitMatrix = new QRCodeWriter().encode(json, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    imgQRCode.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //TODO adicionar mensagem de erro
                    Log.e(TAG, "Erro ao gerar o QR Code." + e.getMessage());
                }
            }
        });

        dialog.show();
    }
}
