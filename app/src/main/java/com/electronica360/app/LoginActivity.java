package com.electronica360.app;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo;
    private EditText etContrasena;
    private Button btnIniciarSesion;
    private TextView tvMensaje;
    private TextView tvIrRegistro;

    private FirebaseAuth autenticacion;

    // Gris carbón para las barras superior e inferior.
    private static final int COLOR_BARRAS =
            Color.parseColor("#303030");

    // Colores del texto.
    private static final int COLOR_GRIS =
            Color.parseColor("#52627D");

    private static final int COLOR_ERROR =
            Color.parseColor("#B42318");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantiene el contenido dentro del área de las barras.
        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                true
        );

        // Carga la pantalla de inicio de sesión.
        setContentView(R.layout.activity_login);

        // Configura las barras del sistema.
        configurarBarrasDelSistema();

        // Inicializa Firebase Authentication.
        autenticacion = FirebaseAuth.getInstance();

        // Vincula las variables con los controles del XML.
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        tvMensaje = findViewById(R.id.tvMensaje);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        // Aplica el estilo profesional al enlace de registro.
        configurarEnlaceRegistro();

        // Acción del botón Iniciar sesión.
        btnIniciarSesion.setOnClickListener(
                view -> iniciarSesion()
        );

        // Abre la pantalla de registro.
        tvIrRegistro.setOnClickListener(view -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);
        });
    }

    /**
     * Configura las barras superior e inferior.
     */
    private void configurarBarrasDelSistema() {

        getWindow().setStatusBarColor(COLOR_BARRAS);
        getWindow().setNavigationBarColor(COLOR_BARRAS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        // Iconos blancos sobre las barras oscuras.
        WindowInsetsControllerCompat controladorBarras =
                new WindowInsetsControllerCompat(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controladorBarras.setAppearanceLightStatusBars(false);
        controladorBarras.setAppearanceLightNavigationBars(false);
    }

    /**
     * Pregunta en gris y "Regístrate" en azul,
     * negrita y subrayado.
     */
    private void configurarEnlaceRegistro() {

        String texto = "¿No tienes una cuenta? Regístrate";

        SpannableString textoEstilizado =
                new SpannableString(texto);

        int inicioEnlace = texto.indexOf("Regístrate");
        int finEnlace = inicioEnlace + "Regístrate".length();

        // Pregunta en gris.
        textoEstilizado.setSpan(
                new ForegroundColorSpan(COLOR_GRIS),
                0,
                inicioEnlace,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // "Regístrate" en azul.
        textoEstilizado.setSpan(
                new ForegroundColorSpan(
                        Color.parseColor("#0085F7")
                ),
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

        // Subrayado únicamente en "Regístrate".
        textoEstilizado.setSpan(
                new UnderlineSpan(),
                inicioEnlace,
                finEnlace,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvIrRegistro.setText(textoEstilizado);
    }

    /**
     * Valida los campos e inicia sesión con Firebase.
     * Solo muestra mensajes cuando hay errores.
     */
    private void iniciarSesion() {

        String correo = etCorreo.getText()
                .toString()
                .trim();

        String contrasena = etContrasena.getText()
                .toString();

        // Oculta cualquier mensaje anterior.
        tvMensaje.setVisibility(View.GONE);

        // Valida que se haya escrito el correo.
        if (correo.isEmpty()) {
            mostrarError("Ingresa tu correo electrónico.");
            etCorreo.requestFocus();
            return;
        }

        // Valida el formato del correo.
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrarError(
                    "Ingresa un correo electrónico válido."
            );
            etCorreo.requestFocus();
            return;
        }

        // Valida que se haya escrito la contraseña.
        if (contrasena.isEmpty()) {
            mostrarError("Ingresa tu contraseña.");
            etContrasena.requestFocus();
            return;
        }

        // Evita múltiples solicitudes simultáneas.
        btnIniciarSesion.setEnabled(false);

        // No se muestra el mensaje "Verificando tus datos...".
        // El formulario permanece sin mensajes durante la espera.

        autenticacion.signInWithEmailAndPassword(
                correo,
                contrasena
        ).addOnCompleteListener(this, tarea -> {

            // Habilita nuevamente el botón al terminar.
            btnIniciarSesion.setEnabled(true);

            if (tarea.isSuccessful()) {

                // Oculta los mensajes anteriores.
                tvMensaje.setVisibility(View.GONE);

                // Abre la pantalla principal.
                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

                startActivity(intent);

                // Evita regresar al login con el botón Atrás.
                finish();

            } else {

                // Solo muestra un mensaje si falla el acceso.
                mostrarError(
                        "No se pudo iniciar sesión. " +
                                "Verifica el correo y la contraseña."
                );
            }
        });
    }

    /**
     * Muestra los errores en rojo.
     */
    private void mostrarError(String mensaje) {

        tvMensaje.setText(mensaje);
        tvMensaje.setTextColor(COLOR_ERROR);
        tvMensaje.setVisibility(View.VISIBLE);
    }
}