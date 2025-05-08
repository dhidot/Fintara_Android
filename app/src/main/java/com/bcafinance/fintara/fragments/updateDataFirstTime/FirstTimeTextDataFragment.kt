package com.bcafinance.fintara.fragments.updateDataFirstTime

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bcafinance.fintara.R
import com.bcafinance.fintara.data.model.dto.FirstTimeUpdateRequest
import com.bcafinance.fintara.data.viewModel.FirstLoginViewModel
import com.bcafinance.fintara.databinding.FragmentTextDataBinding
import com.bcafinance.fintara.ui.firstTimeUpdate.FirstTimeUpdateActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FirstTimeTextDataFragment : Fragment() {

    private val viewModel: FirstLoginViewModel by activityViewModels()
    private lateinit var binding: FragmentTextDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTextDataBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            val ttl = binding.etTtl.text.toString()
            val alamat = binding.etAlamat.text.toString()
            val noTelp = binding.etNoTelp.text.toString()
            val nik = binding.etNik.text.toString()
            val namaIbuKandung = binding.etIbu.text.toString()
            val pekerjaan = binding.etPekerjaan.text.toString()
            val gaji = binding.etGaji.text.toString().toDouble()
            val noRek = binding.etNoRek.text.toString()
            val statusRumah = binding.etStatusRumah.text.toString()

            // Simpan data teks ke ViewModel
            val textData = FirstTimeUpdateRequest(
                ttl, alamat, noTelp, nik, namaIbuKandung, pekerjaan, gaji, noRek, statusRumah, ktpPhotoUrl = "" ,selfiePhotoUrl = ""
            )
            viewModel.setTextData(textData)

            // Navigate ke fragment foto
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PhotoUploadFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.etTtl.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)

                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = formatter.format(selectedCalendar.time)

                    binding.etTtl.setText(formattedDate)
                    viewModel.setTtl(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }


        return binding.root
    }
}

