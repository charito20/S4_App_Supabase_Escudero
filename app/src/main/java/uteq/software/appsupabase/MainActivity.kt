package uteq.software.appsupabase

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch
import uteq.software.appsupabase.data.SupabaseManager
import uteq.software.appsupabase.models.Alumno
import uteq.software.appsupabase.utils.SupabaseErrorHandler

class MainActivity : AppCompatActivity() {

    private lateinit var txtAlumnos: TextView
    private lateinit var progressMaterias: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtAlumnos = findViewById(R.id.txtAlumnos)
        progressMaterias = findViewById(R.id.progressMaterias)
        consultarAlumnos()
    }

    private fun consultarAlumnos() {
        lifecycleScope.launch {
            progressMaterias.visibility = View.VISIBLE
            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()
                txtAlumnos.text = if (alumnos.isEmpty()) {
                    "No se encontraron alumnos."
                } else {
                    alumnos.joinToString("\n\n") { a ->
                        "Nombre: ${a.nombres}\n" +
                                "Paralelo: ${a.paralelo}\n" +
                                "Correo: ${a.correo ?: "Sin registrar"}\n" +
                                "Teléfono: ${a.telefono ?: "Sin registrar"}"
                    }
                }
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            } finally {
                progressMaterias.visibility = View.GONE
            }
        }
    }
}
