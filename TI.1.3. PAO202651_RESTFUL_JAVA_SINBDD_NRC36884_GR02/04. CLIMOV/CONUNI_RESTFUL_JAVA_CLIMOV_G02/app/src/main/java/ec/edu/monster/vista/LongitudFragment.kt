package ec.edu.monster.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ec.edu.monster.R
import ec.edu.monster.api.RetrofitClient
import ec.edu.monster.databinding.FragmentLongitudBinding
import ec.edu.monster.model.ConversionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento para conversiones de longitud usando API REST
 */
class LongitudFragment : Fragment() {

    private var _binding: FragmentLongitudBinding? = null
    private val binding get() = _binding!!
    
    private val operaciones = listOf(
        Pair("metros", "pies") to "m → ft",
        Pair("pies", "metros") to "ft → m",
        Pair("metros", "pulgadas") to "m → in",
        Pair("pulgadas", "metros") to "in → m",
        Pair("kilometros", "millas") to "km → mi",
        Pair("millas", "kilometros") to "mi → km"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLongitudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            operaciones.map { it.second }
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.spinnerOperacion.adapter = adapter
    }
    
    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        // Invierte el sentido (las conversiones van en pares: ida/vuelta)
        binding.btnInvertir.setOnClickListener {
            val pos = binding.spinnerOperacion.selectedItemPosition
            binding.spinnerOperacion.setSelection(pos xor 1)
        }

        binding.btnConvertir.setOnClickListener { convertirLongitud() }
        binding.btnLimpiar.setOnClickListener { limpiarCampos() }
    }
    
    private fun convertirLongitud() {
        val valorTexto = binding.editValor.text?.toString()?.trim() ?: ""
        if (valorTexto.isEmpty()) {
            binding.editValor.error = "Ingrese un valor"
            return
        }
        val valor = valorTexto.toDoubleOrNull() ?: run {
            binding.editValor.error = "Valor inválido"
            return
        }
        
        val posicion = binding.spinnerOperacion.selectedItemPosition
        val (unidades, _) = operaciones[posicion]
        val (unidadOrigen, unidadDestino) = unidades
        
        binding.btnConvertir.isEnabled = false
        binding.btnConvertir.text = "Convirtiendo..."
        
        lifecycleScope.launch {
            try {
                val request = ConversionRequest(
                    valor = valor,
                    unidadOrigen = unidadOrigen,
                    unidadDestino = unidadDestino,
                    categoria = "longitud"
                )
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.convertir(request)
                }
                requireActivity().runOnUiThread {
                    binding.btnConvertir.isEnabled = true
                    binding.btnConvertir.text = "Convertir"
                    if (response.isSuccessful) {
                        val body = response.body()
                        val resultado = body?.valorConvertido ?: body?.resultado
                        if (body?.exito == true && resultado != null) {
                            mostrarResultado(valor, unidadOrigen, unidadDestino, resultado)
                        } else {
                            mostrarError(body?.mensaje ?: "Error en la conversión")
                        }
                    } else {
                        mostrarError("Error del servidor: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    binding.btnConvertir.isEnabled = true
                    binding.btnConvertir.text = "Convertir"
                    mostrarError("Error de conexión: ${e.message}")
                }
            }
        }
    }
    
    private fun mostrarResultado(valorOriginal: Double, origen: String, destino: String, resultado: Double) {
        val simbolos = mapOf("metros" to "m", "pies" to "ft", "pulgadas" to "in", "kilometros" to "km", "millas" to "mi")
        binding.textResultado.text = "$valorOriginal ${simbolos[origen] ?: origen} = ${String.format("%.4f", resultado)} ${simbolos[destino] ?: destino}"
        binding.textResultado.setTextColor(resources.getColor(R.color.monster_pink, null))
        binding.layoutResultado.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Conversión exitosa", Toast.LENGTH_SHORT).show()
    }
    
    private fun mostrarError(mensaje: String) {
        binding.textResultado.text = mensaje
        binding.textResultado.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        binding.layoutResultado.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Error en la conversión", Toast.LENGTH_SHORT).show()
    }
    
    private fun limpiarCampos() {
        binding.editValor.text?.clear()
        binding.spinnerOperacion.setSelection(0)
        binding.layoutResultado.visibility = View.GONE
        binding.editValor.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


