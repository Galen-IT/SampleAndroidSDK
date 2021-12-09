package com.galenit.sampleusagesdk.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.galenit.device.api.base.WavePartListener
import com.galenit.device.api.lib.GalenIT
import com.galenit.device.api.parameter.Parameter
import com.galenit.sampleusagesdk.databinding.FragmentDevicesMeasurementBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragmentDevicesMeasurement : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentDevicesMeasurementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDevicesMeasurementBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.buttonStart.setOnClickListener {
            context?.let {
                binding.textViewLog.text = ""
                CoroutineScope(Dispatchers.Main).launch {
                    val parameterMeasurement = Parameter.LongPulseRate::class

                    val manager = GalenIT.newBleManager(it)
                    addLineTextView(binding.textViewLog, "Поиск устройства")
                    val scan = manager.scanForParameter(parameterMeasurement)
                        .onEach { addLineTextView(binding.textViewLog, "Найдено " + it.deviceName) }
                        .first()
                    try {
                        addLineTextView(binding.textViewLog, "Попытка подключения к " + scan.deviceName)
                        val dev = scan.connect()
                        addLineTextView(binding.textViewLog, "Подключение установлено с " + scan.deviceName)

                        // разовое измерение
//                        val parameter = dev.readParameter(parameterMeasurement)
//                        addLineTextView(binding.textViewLog, "Получено измерение " + parameter.value.toString())

                        // продолжительное измерение
                        val parameter = dev.readLongParameter(parameterMeasurement)
                        parameter.setPartListener(object : WavePartListener {
                            override fun listen(param: Parameter<Any>) {
                                addLineTextView(binding.textViewLog, "Получено измерение " + param.value.toString())
                            }
                        })
//                        CoroutineScope(Dispatchers.Main).launch {
//                            delay(30000) // Через 30 секунд
//                            parameter.stop() // Останавливаем измерение
//                        }
                        val param = parameter.waitForParameter()
                        addLineTextView(binding.textViewLog, "Получено измерение " + param.value.toString())

                        dev.disconnect()
                        addLineTextView(binding.textViewLog, "Подключение c " + scan.deviceName + " закрыто")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return root
    }

    private fun addLineTextView(textView: TextView, textLine: String) {
        activity?.runOnUiThread {
            val text = textView.text.toString() + textLine + "\n"
            textView.text = text
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragmentDevicesMeasurement {
            return PlaceholderFragmentDevicesMeasurement().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}