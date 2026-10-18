package com.electronica360.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    // Gris carbón para las barras superior e inferior.
    private static final int COLOR_BARRAS =
            Color.parseColor("#303030");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantiene el contenido dentro del espacio disponible
        // de las barras del teléfono.
        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                true
        );

        // Carga la pantalla principal.
        setContentView(R.layout.activity_main);

        // Configura las barras del sistema.
        configurarBarrasDelSistema();

        // Categoría: Repuestos.
        View tarjetaRepuestos =
                findViewById(R.id.tarjetaRepuestos);

        if (tarjetaRepuestos != null) {
            tarjetaRepuestos.setOnClickListener(view ->
                    mostrarMensaje(
                            "Categoría seleccionada: Repuestos"
                    )
            );
        }

        // Categoría: Componentes.
        View tarjetaComponentes =
                findViewById(R.id.tarjetaComponentes);

        if (tarjetaComponentes != null) {
            tarjetaComponentes.setOnClickListener(view ->
                    mostrarMensaje(
                            "Categoría seleccionada: Componentes"
                    )
            );
        }

        // Categoría: Herramientas.
        View tarjetaHerramientas =
                findViewById(R.id.tarjetaHerramientas);

        if (tarjetaHerramientas != null) {
            tarjetaHerramientas.setOnClickListener(view ->
                    mostrarMensaje(
                            "Categoría seleccionada: Herramientas"
                    )
            );
        }

        // Botón: Explorar catálogo.
        View btnContinuar =
                findViewById(R.id.btnContinuar);

        if (btnContinuar != null) {
            btnContinuar.setOnClickListener(view ->
                    mostrarMensaje(
                            "Catálogo general de Electrónica 360"
                    )
            );
        }
    }

    /**
     * Configura ambas barras del sistema con fondo gris
     * carbón e iconos blancos.
     */
    private void configurarBarrasDelSistema() {

        // Aplica el mismo color a las barras superior e inferior.
        getWindow().setStatusBarColor(COLOR_BARRAS);
        getWindow().setNavigationBarColor(COLOR_BARRAS);

        // Desactiva el contraste automático de navegación
        // en las versiones compatibles de Android.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        // Configura los iconos del sistema en blanco.
        WindowInsetsControllerCompat controladorBarras =
                new WindowInsetsControllerCompat(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controladorBarras.setAppearanceLightStatusBars(false);
        controladorBarras.setAppearanceLightNavigationBars(false);
    }

    /**
     * Muestra los mensajes de las categorías
     * y del botón Explorar catálogo.
     */
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(
                MainActivity.this,
                mensaje,
                Toast.LENGTH_SHORT
        ).show();
    }
}