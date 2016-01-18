package br.com.cast.ticket.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.User;
import br.com.cast.ticket.http.HttpUserService;
import br.com.cast.ticket.util.AppUtil;
import br.com.cast.ticket.util.Mask;
import br.com.cast.ticket.util.StringUtils;

public class RegisterUserActivity extends AppCompatActivity {
    private EditText mEditTextFirstName;
    private EditText mEditTextLastName;
    private EditText mEditTextCellPhone;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextConfirmPassword;

    private CheckBox mCkTerms;

    private Button mButtonRegister;
    private Button mButtonTerms;
    private static final int WIDTH = 550;
    private static final int HEIGHT = 450;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        bindElements();

        mButtonTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogTermsAndConditions();
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }
    /**
     * Creates and sets up the dialog to terms and conditions. Setting the layout, height and width.
     */
    private void createDialogTermsAndConditions() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_terms, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);

        builder.setCustomTitle(view);
        builder.setMessage(getResources().getString(R.string.tab_terms));
        builder.setPositiveButton(R.string.tab_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setLayout(WIDTH, HEIGHT);
    }

    /**
     * Register a new user
     */
    private void register() {
        List<? extends View> fields = Arrays.asList(mEditTextFirstName, mEditTextLastName,
                mEditTextCellPhone, mEditTextEmail, mEditTextPassword, mEditTextConfirmPassword, mCkTerms);

        boolean formIsValid = AppUtil.validForm(fields, RegisterUserActivity.this);
        formIsValid &= checkPasswords();

        if (formIsValid) {
            User user = new User(null, mEditTextFirstName.getText().toString(),
                    mEditTextLastName.getText().toString(), mEditTextCellPhone.getText().toString(),
                    mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString(),null,null);

            //new SaveUserTask().execute(user);

            Toast.makeText(this, getResources().getString(R.string.message_register_user_success),
                    Toast.LENGTH_LONG).show();

            cleanForm();

            mEditTextFirstName.requestFocus();

            final Intent intentLogin = new Intent(RegisterUserActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    /**
     * Verifies that the provided passwords are equal
     *
     * @return true if passwords are equal
     */
    private boolean checkPasswords() {
        if (!StringUtils.equals(mEditTextPassword.getText().toString(),
                mEditTextConfirmPassword.getText().toString())) {
            mEditTextConfirmPassword.setError(getResources().getString(R.string.message_different_passwords));
            return false;
        }
        return true;
    }

    public class SaveUserTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            HttpUserService.post(users[0]);
            return null;
        }
    }

    /**
     * clean the form
     */
    private void cleanForm() {
        List<? extends View> fields = Arrays.asList(mEditTextFirstName, mEditTextLastName,
                mEditTextCellPhone, mEditTextEmail, mEditTextPassword, mEditTextConfirmPassword);

        for (View v : fields) {
            if (v instanceof EditText) {
                EditText txt = (EditText) v;
                txt.setText("");
            } else if (v instanceof CheckBox) {
                CheckBox ck = (CheckBox) v;
                ck.setChecked(false);
            }
        }
    }

    private void bindElements() {
        mEditTextFirstName = AppUtil.get(findViewById(R.id.txtFirstName));
        mEditTextLastName = AppUtil.get(findViewById(R.id.txtLastName));
        mEditTextCellPhone = AppUtil.get(findViewById(R.id.txtCellPhone));
        mEditTextCellPhone.addTextChangedListener(Mask.insert("(##)#####-####", mEditTextCellPhone));
        mEditTextEmail = AppUtil.get(findViewById(R.id.txtEmail));
        mEditTextPassword = AppUtil.get(findViewById(R.id.txtPassword));
        mEditTextConfirmPassword = AppUtil.get(findViewById(R.id.txtConfirmPassword));
        mCkTerms = AppUtil.get(findViewById(R.id.ckTermsConditient));
        mButtonTerms = AppUtil.get(findViewById(R.id.btnTerm));
        mButtonRegister = AppUtil.get(findViewById(R.id.btnRegister));
    }
}
