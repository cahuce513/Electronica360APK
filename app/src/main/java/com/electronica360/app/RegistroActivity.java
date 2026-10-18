package com.electronica360.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etCorreoRegistro;
    private EditText etContrasenaRegistro;
    private EditText etConfirmarContrasena;

    private Button btnRegistrar;
    private TextView tvMensajeRegistro;
    private TextView tvVolverLogin;

    private FirebaseAuth autenticacion;

    // Gris carbón para las barras superior e inferior.
    private static final int COLOR_BARRAS =
            Color.parseColor("#303030");

    // Colores de la identidad visual.
    private static final int COLOR_AZUL =
            Color.parseColor("#0085F7");

    private static final int COLOR_GRIS =
            Color.parseColor("#52627D");

    private static final int COLOR_ERROR =
            Color.parseColor("#B42318");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantiene el contenido dentro del espacio
        // disponible de las barras del teléfono.
        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                true
        );

        // Carga la pantalla de registro.
        setContentView(R.layout.activity_registro);

        // Configura las barras del sistema.
        configurarBarrasDelSistema();

        // Inicializa Firebase Authentication.
        autenticacion = FirebaseAuth.getInstance();

        // Vincula las variables con los controles del XML.
        etNombre = findViewById(R.id.etNombre);
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etContrasenaRegistro =
                findViewById(R.id.etContrasenaRegistro);
        etConfirmarContrasena =
                findViewById(R.id.etConfirmarContrasena);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvMensajeRegistro =
                findViewById(R.id.tvMensajeRegistro);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);

        // Configura el enlace profesional para volver al login.
        configurarEnlaceVolverLogin();

        // Acción del botón para crear una cuenta.
        btnRegistrar.setOnClickListener(
                view -> registrarUsuario()
        );

        // Regresa al inicio de sesión.
        tvVolverLogin.setOnClickListener(
                view -> finish()
        );
    }

    /**
     * Configura las barras superior e inferior
     * con fondo gris carbón e iconos blancos.
     */
    private void configurarBarrasDelSistema() {

        getWindow().setStatusBarColor(COLOR_BARRAS);
        getWindow().setNavigationBarColor(COLOR_BARRAS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        WindowInsetsControllerCompat controladorBarras =
                new WindowInsetsControllerCompat(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controladorBarras.setAppearanceLightStatusBars(false);
        controladorBarras.setAppearanceLightNavigationBars(false);
    }

    /**
     * Muestra la pregunta en gris y "Inicia sesión"
     * en azul, negrita y subrayado.
     */
    private void configurarEnlaceVolverLogin() {

        String texto =
                "¿Ya tienes una cuenta? Inicia sesión";

        SpannableString textoEstilizado =
                new SpannableString(texto);

        String textoEnlace = "Inicia sesión";

        int inicioEnlace = texto.indexOf(textoEnlace);
        int finEnlace = inicioEnlace + textoEnlace.length();

        // Pregunta en gris.
        textoEstilizado.setSpan(
                new ForegroundColorSpan(COLOR_GRIS),
                0,
                inicioEnlace,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Enlace en azul.
        textoEstilizado.setSpan(
                new ForegroundColorSpan(COLOR_AZUL),
                inicioEnlace,
                finEnlace,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Enlace en negrita.
        textoEstilizado.setSpan(
                new StyleSpan(Typeface.BOLD),
                inicioEnlace,
                finEnlace,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Subrayado únicamente en "Inicia sesión".
        textoEstilizado.setSpan(
                new UnderlineSpan(),
                inicioEnlace,
                finEnlace,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvVolverLogin.setText(textoEstilizado);
    }

    /**
     * Valida los datos y crea la cuenta con Firebase.
     * No muestra mensajes mientras Firebase procesa la solicitud.
     */
    private void registrarUsuario() {

        String nombre = etNombre.getText()
                .toString()
                .trim();

        String correo = etCorreoRegistro.getText()
                .toString()
                .trim();

        String contrasena = etContrasenaRegistro.getText()
                .toString();

        String confirmarContrasena =
                etConfirmarContrasena.getText()
                        .toString();

        // Oculta un posible mensaje de error anterior.
        tvMensajeRegistro.setVisibility(View.GONE);

        // Valida el nombre.
        if (nombre.isEmpty()) {
            mostrarError("Ingresa tu nombre completo.");
            etNombre.requestFocus();
            return;
        }

        // Valida el correo vacío.
        if (correo.isEmpty()) {
            mostrarError("Ingresa tu correo electrónico.");
            etCorreoRegistro.requestFocus();
            return;
        }

        // Valida el formato del correo.
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrarError(
                    "Ingresa un correo electrónico válido."
            );
            etCorreoRegistro.requestFocus();
            return;
        }

        // Valida que se haya escrito una contraseña.
        if (contrasena.isEmpty()) {
            mostrarError("Ingresa una contraseña.");
            etContrasenaRegistro.requestFocus();
            return;
        }

        // Comprueba la longitud mínima.
        if (contrasena.length() < 6) {
            mostrarError(
                    "La contraseña debe tener al menos 6 caracteres."
            );
            etContrasenaRegistro.requestFocus();
            return;
        }

        // Valida la confirmación.
        if (confirmarContrasena.isEmpty()) {
            mostrarError("Confirma tu contraseña.");
            etConfirmarContrasena.requestFocus();
            return;
        }

        // Comprueba que ambas contraseñas coincidan.
        if (!contrasena.equals(confirmarContrasena)) {
            mostrarError("Las contraseñas no coinciden.");
            etConfirmarContrasena.requestFocus();
            return;
        }

        // Desactiva el botón para impedir solicitudes duplicadas.
        // No se muestra ningún mensaje intermedio.
        btnRegistrar.setEnabled(false);

        // Crea el usuario en Firebase Authentication.
        autenticacion.createUserWithEmailAndPassword(
                correo,
                contrasena
        ).addOnCompleteListener(this, tarea -> {

            if (tarea.isSuccessful()) {

                FirebaseUser usuario =
                        autenticacion.getCurrentUser();

                if (usuario != null) {

                    // Guarda el nombre completo en el perfil.
                    UserProfileChangeRequest actualizacionPerfil =
                            new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre)
                                    .build();

                    usuario.updateProfile(actualizacionPerfil)
                            .addOnCompleteListener(
                                    this,
                                    tareaPerfil -> {

                                        // Notifica que la cuenta fue creada.
                                        // El perfil se actualiza aparte.
                                        Toast.makeText(
                                                RegistroActivity.this,
                                                "Cuenta creada correctamente. " +
                                                        "Ahora inicia sesión.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        // Regresa al login para que
                                        // el usuario inicie sesión.
                                        autenticacion.signOut();

                                        finish();
                                    }
                            );

                } else {

                    // La cuenta se creó, pero no se obtuvo
                    // el usuario actual de Firebase.
                    Toast.makeText(
                            RegistroActivity.this,
                            "Cuenta creada. Regresa e inicia sesión.",
                            Toast.LENGTH_LONG
                    ).show();

                    autenticacion.signOut();
                    finish();
                }

            } else {

                // Habilita de nuevo el botón si hay un error.
                btnRegistrar.setEnabled(true);

                // Solo muestra un mensaje cuando el registro falla.
                mostrarError(
                        "No se pudo crear la cuenta. " +
                                "Verifica los datos o intenta con otro correo."
                );
            }
        });
    }

    /**
     * Muestra únicamente los mensajes de error en rojo.
     */
    private void mostrarError(String mensaje) {

        tvMensajeRegistro.setText(mensaje);
        tvMensajeRegistro.setTextColor(COLOR_ERROR);
        tvMensajeRegistro.setVisibility(View.VISIBLE);
    }
}