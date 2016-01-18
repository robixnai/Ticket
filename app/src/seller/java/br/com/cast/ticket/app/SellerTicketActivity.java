package br.com.cast.ticket.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.Ticket;
import br.com.cast.ticket.app.util.qrcode.Intents;
import br.com.cast.ticket.util.AppUtil;

public class SellerTicketActivity extends AppCompatActivity {

    private TextView txtAlias, txtDate, txtTime, txtType, txtPassangerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_ticket);

        txtAlias = (TextView) findViewById(R.id.edt_alias);
        txtDate = (TextView) findViewById(R.id.edt_date);
        txtTime = (TextView) findViewById(R.id.edt_time);
        txtType = (TextView) findViewById(R.id.edt_type);
        txtPassangerType = (TextView) findViewById(R.id.edt_passenger_type);

        Intent intent = getIntent();
        String json = intent.getStringExtra(Intents.Scan.RESULT);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Ticket ticket = mapper.readValue(json, Ticket.class);
            txtAlias.setText(ticket.getAlias());
            txtDate.setText(AppUtil.formatDate(ticket.getData().getExpiration()));
            txtTime.setText(AppUtil.formatTime(ticket.getData().getExpiration()));
            txtType.setText(ticket.getType().toString());
            txtPassangerType.setText(ticket.getData().getPassengerType().toString());

        } catch (IOException e) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setMessage(getString(R.string.msg_error_qrcode))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    })
                    .show();
        }
    }
}
