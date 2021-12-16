package com.galenit.sampleusagesdk.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.galenit.component.binah.BinahView
import com.galenit.device.api.base.WavePartListener
import com.galenit.device.api.parameter.Parameter
import com.galenit.sampleusagesdk.R
import com.galenit.sampleusagesdk.databinding.FragmentFaceMeasurementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragmentFaceMeasurement : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentFaceMeasurementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFaceMeasurementBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.radioGroupe.setOnCheckedChangeListener { group, checkedId ->
            if (group == binding.radioGroupe) {
                when(checkedId) {
                    binding.radioButtonFace.id -> binding.cameraView.mode = BinahView.Mode.FACE
                    binding.radioButtonFinger.id -> binding.cameraView.mode = BinahView.Mode.FINGER
                }
            }
        }

        binding.cameraView.onFace = { bitmap, _ ->
            Toast.makeText(context, getString(R.string.message_find_face), Toast.LENGTH_LONG).show()
        }
        binding.cameraView.onFaceHide = { ->
            Toast.makeText(context, getString(R.string.message_no_face), Toast.LENGTH_LONG).show()
            binding.buttonStart.text = getString(R.string.button_start_measurement)
            CoroutineScope(Dispatchers.Main).launch {
                binding.cameraView.stopMeasurement()
                binding.cameraView.startWithoutMeasurements()
            }
        }

        binding.cameraView.startWithoutMeasurements()
        binding.buttonStart.setOnClickListener {
            if (binding.buttonStart.text == getString(R.string.button_stop_measurement)) {
                binding.cameraView.stopMeasurement()
                binding.cameraView.startWithoutMeasurements()
                binding.buttonStart.text = getString(R.string.button_start_measurement)
                return@setOnClickListener
            }
            binding.buttonStart.text = getString(R.string.button_stop_measurement)
            binding.textViewSaturation.text = "-"
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val parameter = binding.cameraView.readLongParameter(Parameter.LongBodyOxygen::class)
                    parameter.setPartListener(object : WavePartListener {
                        override fun listen(param: Parameter<Any>) {
                            binding.textViewSaturation.text = param.value.toString()
                        }
                    })
                }
                catch (e: Exception) {
                    e.message?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                }
            }

            binding.textViewPulseRate.text = "-"
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val parameter = binding.cameraView.readLongParameter(Parameter.LongPulseRate::class)
                    parameter.setPartListener(object : WavePartListener {
                        override fun listen(param: Parameter<Any>) {
                            binding.textViewPulseRate.text = param.value.toString()
                        }
                    })
                }
                catch (e: Exception) {
                    e.message?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                }
            }

            binding.textViewBreathingRate.text = "-"
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val parameter = binding.cameraView.readLongParameter(Parameter.LongBreathingRate::class)
                    parameter.setPartListener(object : WavePartListener {
                        override fun listen(param: Parameter<Any>) {
                            binding.textViewBreathingRate.text = param.value.toString()
                        }
                    })
                }
                catch (e: java.lang.Exception) {
                    e.message?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                }
            }

            binding.textViewSDNNHeartRateVariability.text = "-"
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val parameter = binding.cameraView.readParameter(Parameter.SDNNHeartRateVariability::class)
                    binding.textViewSDNNHeartRateVariability.text = String.format("%.0f", parameter.value)

                }
                catch (e: java.lang.Exception) {
                    e.message?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                }
            }

            binding.textViewStressLevel.text = "-"
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val parameter = binding.cameraView.readParameter(Parameter.StressLevel::class)
                    binding.textViewStressLevel.text = parameter.value.toString()
                }
                catch (e: java.lang.Exception) {
                    e.message?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                }
            }

            binding.textViewBloodPressure.text = "-"
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val parameter = binding.cameraView.readParameter(Parameter.Pressure::class)
                    binding.textViewBloodPressure.text = String.format("%s / %s", parameter.value.systolic, parameter.value.diastolic)
                }
                catch (e: java.lang.Exception) {
                    e.message?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                }
            }
            binding.cameraView.startMeasurements()
        }

        return root
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
        fun newInstance(sectionNumber: Int): PlaceholderFragmentFaceMeasurement {
            return PlaceholderFragmentFaceMeasurement().apply {
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