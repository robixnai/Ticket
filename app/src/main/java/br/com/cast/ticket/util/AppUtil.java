package br.com.cast.ticket.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.cast.ticket.R;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Useful class for generic methods.
 *
 * @author falvojr
 */
public final class AppUtil {

    public static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private static final String PATTERN_DATE = "dd/MM/yyyy";
    private static final String PATTERN_TIME = "HH:mm:ss";
    private static final String PATTERN_DATETIME = "dd/MM/yyyy HH:mm:ss";
    private static final String PATTERN_NUMBER = "#.00";
    public static Context CONTEXT;

    private AppUtil() {
        super();
    }

    public static <T> T get(Object element) {
        return (T) element;
    }


    /**
     * Verifies that the network is available.
     * @author Ezequiel
     */

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
    /**
     * Checks that the form filing is complete
     * @author
     *
     * @return true if the filing is ok otherwise returns false
     */
    public static boolean validForm(List<? extends View> fields,Context context) {
        boolean isValid = true;

        for (View v : fields) {
            if (v instanceof EditText) {
                EditText txt = (EditText) v;
                if (StringUtils.isEmpty(txt.getText().toString())) {
                    txt.setError(context.getResources().getString(R.string.message_error_empty_string));
                    isValid = false;
                }
            }else if(v instanceof MaterialSpinner){
                MaterialSpinner ms = (MaterialSpinner) v;
                if(ms.getSelectedItemPosition() <= 0){
                    //ms.setError(context.getResources().getString(R.string.msgErrorEmptyString));
                    isValid= false;
                }
            } else if (v instanceof CheckBox) {
                CheckBox ck = (CheckBox) v;
                if (!ck.isChecked()) {
                    Toast.makeText(context, context.getResources()
                            .getString(R.string.message_terms_conditions_required), Toast.LENGTH_LONG).show();
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    public static String formatDate(Date date) {
        return format(date, AppUtil.PATTERN_DATE);
    }

    public static String formatDateTime(Date date) {
        return format(date, AppUtil.PATTERN_DATETIME);
    }

    public static String formatTime(Date date) {
        return format(date, AppUtil.PATTERN_TIME);
    }

    public static String formatDecimal(Number number) {
        final DecimalFormat decimalFormat = AppUtil.get(NumberFormat.getNumberInstance(Locale.US));
        decimalFormat.applyPattern(AppUtil.PATTERN_NUMBER);
        return decimalFormat.format(number);
    }

    private static String format(Date date, String pattern) {
        final DateFormat dateTimeFormat = new SimpleDateFormat(pattern, AppUtil.LOCALE_PT_BR);
        return dateTimeFormat.format(date);
    }

    public static boolean isEmailValid(String email) {
        if ((email == null) || (email.trim().length() == 0))
            return false;

        String emailPattern = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isTokenAvaible(String token){
        final String UriGoogleApi = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + token;
        return false;
    }

}
