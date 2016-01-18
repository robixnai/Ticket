package br.com.cast.ticket.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.BusTicket;
import br.com.cast.ticket.entity.SubwayTicket;
import br.com.cast.ticket.entity.Ticket;
import br.com.cast.ticket.entity.TicketData;
import br.com.cast.ticket.entity.TicketPassengerType;
import br.com.cast.ticket.entity.TicketTaxType;
import br.com.cast.ticket.entity.TicketType;
import br.com.cast.ticket.http.HttpTicketService;
import br.com.cast.ticket.util.AppUtil;
import br.com.cast.ticket.util.MyLocation;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Activity that creates a new {@link Ticket}.
 *
 * @author ezequielmessore
 * @author maikotrindade
 * @since 25/07/2015
 */
public class TicketActivity extends AppCompatActivity {

    // Constants Prices Bus
    private final double BUS_PRICE_CHILDREN = 2.0;
    private final double BUS_PRICE_ADULT = 5.0;
    private final double BUS_PRICE_ELDERLY = 3.0;

    // Constants Prices Bus
    private final double SUBWAY_PRICE_CHILD = 3.0;
    private final double SUBWAY_PRICE_ADULT = 6.0;
    private final double SUBWAY_PRICE_ELDERLY = 4.0;

    //Component's
    private MaterialSpinner mStates;
    private MaterialSpinner mTypePassenger;
    private MaterialSpinner mTypeTransport;
    private MaterialSpinner mLine;
    private EditText mQuantity;
    private MaterialSpinner mTaxType;
    private Button mBuyAction;

    // Price
    private int mCountTicket = 1;

    // List's
    List<String> mListStates;
    List<String> mListLines;

    String mCurrentLocation;
    private double mTotalTicket;
    private TicketData mTicketData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        bindElements();
        mock();
        //updateCurrentLocation();
        createSpinners();
        controlSpinnersPassengerTransport();
    }

    //TODO remover mock
    private void mock() {
        mListStates = new ArrayList<>();
        mListStates.add("Acre");
        mListStates.add("Distrito Federal");
        mListStates.add("Rio de Janeiro");
        mListStates.add("Santa Catarina");
        mListStates.add("São Paulo");

        mListLines = new ArrayList<>();
        mListLines.add("Linha Azul");
        mListLines.add("Linha Vermelha");
    }

    public void updateCurrentLocation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    MyLocation myLocation = new MyLocation();
                    mCurrentLocation = myLocation.returnLocation(TicketActivity.this);
                    mStates.setSelection(mListStates.indexOf(mCurrentLocation) + 1);
                } catch (Exception e) {
                    Log.e(TicketActivity.class.getSimpleName(), e.getMessage());
                }
            }
        }, 0);
    }

    private void createSpinners() {
        createSpinner(mStates, mListStates);
        createSpinnerPassenger(mTypePassenger);
        createSpinnerTransport(mTypeTransport);
        createSpinner(mLine, mListLines);
        createSpinnerTaxType(mTaxType);
    }

    private void createSpinnerTaxType(MaterialSpinner taxType) {
        ArrayAdapter<TicketTaxType> adapter = new ArrayAdapter<>(TicketActivity.this, android.R.layout.simple_spinner_item, TicketTaxType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taxType.setAdapter(adapter);
    }

    private void createSpinnerTransport(MaterialSpinner mTypeTransport) {
        ArrayAdapter<TicketType> adapter = new ArrayAdapter<>(TicketActivity.this, android.R.layout.simple_spinner_item, TicketType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeTransport.setAdapter(adapter);
    }

    private void createSpinnerPassenger(MaterialSpinner mTypePassenger) {
        ArrayAdapter<TicketPassengerType> adapter = new ArrayAdapter<>(TicketActivity.this, android.R.layout.simple_spinner_item, TicketPassengerType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypePassenger.setAdapter(adapter);
    }

    private void createSpinner(MaterialSpinner spinner, List<String> listItens) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TicketActivity.this, android.R.layout.simple_spinner_item, listItens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void bindElements() {
        mStates = AppUtil.get(findViewById(R.id.states));
        mTypePassenger = AppUtil.get(findViewById(R.id.select_type_passenger));
        mTypeTransport = AppUtil.get(findViewById(R.id.select_type_transport));
        mLine = AppUtil.get(findViewById(R.id.select_line));
        mTaxType = AppUtil.get(findViewById(R.id.select_tax_type));

        mBuyAction = AppUtil.get(findViewById(R.id.btnPaid));
        mBuyAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO realizar validações
                isValidTicket();
                buyTicket();
            }
        });

        mQuantity = AppUtil.get(findViewById(R.id.edtQuantity));
        mQuantity.setText(String.valueOf(mCountTicket));
        mQuantity.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_del_ticket, 0, R.mipmap.ic_action_add_ticket, 0);
        mQuantity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_RIGHT = 2;
                final int PADDING = 40;
                int right = mQuantity.getRight();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (right - mQuantity.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mCountTicket += 1;
                        mQuantity.setText(String.valueOf(mCountTicket));
                        setTotal();

                    } else if (event.getRawX() <= (PADDING + mQuantity.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        mCountTicket -= (mCountTicket == 1) ? 0 : 1;
                        mQuantity.setText(String.valueOf(mCountTicket));
                        setTotal();
                    }
                }
                return false;
            }
        });
    }

    private void controlSpinnersPassengerTransport() {
        mTypeTransport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    setTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTypePassenger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    setTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setTotal() {
        Object typePassenger = mTypePassenger.getSelectedItem();
        Object typeTransport = mTypeTransport.getSelectedItem();
        double price = calculatePriceTicket(typePassenger.toString(), typeTransport.toString());
        mTotalTicket = price * mCountTicket;
        mBuyAction.setText("PAGAR " + NumberFormat.getCurrencyInstance().format(mTotalTicket));
    }

    private double calculatePriceTicket(String typePassenger, String typeTransport) {
        double price = 0;

        final TicketType type = TicketType.getInstance(typeTransport);
        final TicketPassengerType passenger = TicketPassengerType.getInstance(typePassenger);
        if (type != null && passenger != null) {
            switch (type) {
                case BUS:
                    mTicketData = new BusTicket();
                    switch (passenger) {
                        case CHILD:
                            price = BUS_PRICE_CHILDREN;
                            break;
                        case ADULT:
                            price = BUS_PRICE_ADULT;
                            break;
                        case ELDERLY:
                            price = BUS_PRICE_ELDERLY;
                            break;
                    }
                    break;
                case SUBWAY:
                    mTicketData = new SubwayTicket();
                    switch (passenger) {
                        case CHILD:
                            price = SUBWAY_PRICE_CHILD;
                            break;
                        case ADULT:
                            price = SUBWAY_PRICE_ADULT;
                            break;
                        case ELDERLY:
                            price = SUBWAY_PRICE_ELDERLY;
                            break;
                    }
                    break;
            }
        }
        return price;
    }

    private boolean isValidTicket() {
        return true;
    }

    private void buyTicket() {
        //TODO criar e popular um novo ticket com as informações que já existe.
        Ticket ticket = new Ticket();

        mTicketData.setLine(mLine.getSelectedItem().toString());
        mTicketData.setQuantity(Integer.parseInt(mQuantity.getText().toString()));
        mTicketData.setUnitTax(BigDecimal.valueOf(mTotalTicket));
        mTicketData.setPassengerType(TicketPassengerType.getInstance(mTypePassenger.getSelectedItem().toString()));
        //TODO remover mock QR e Expiration
        mTicketData.setQrCode("97826457892634589726384");
        mTicketData.setExpiration(new Date());
        mTicketData.setLastUsage(new Date());

        ticket.setAlias("ALIAS");
        ticket.setData(mTicketData);
        TicketTaxType taxType = TicketTaxType.getInstance(mTaxType.getSelectedItem().toString());
        ticket.setTaxType(taxType);
        //new SaveTicketTask(this).execute(ticket);

        //TODO Adicionar objeto Ticket na Intent e Iniciar Activity
        Intent intent = new Intent();
        intent.putExtra("Ticket", ticket);
        setResult(RESULT_OK,intent);
        finish();
    }

    public class SaveTicketTask extends AsyncTask<Ticket, Void, Void> {
        Context mContext;
        ProgressDialog mProgress;

        public SaveTicketTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mProgress = ProgressDialog.show(mContext, "Waiting...", "Process Data", true, true);
        }

        @Override
        protected Void doInBackground(Ticket... tickets) {
            HttpTicketService.postTicket(tickets[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgress.dismiss();
        }

    }

}