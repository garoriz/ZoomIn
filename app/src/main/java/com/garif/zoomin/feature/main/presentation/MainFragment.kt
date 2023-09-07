package com.garif.zoomin.feature.main.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.garif.zoomin.R
import com.garif.zoomin.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        with(binding) {
            btnStart.setOnClickListener {
                if (hasPermissions(activity as Context)) {
                    view.findNavController().navigate(R.id.action_mainFragment_to_magnifyingGlassFragment)
                } else {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }
        }
    }

    private fun hasPermissions(context: Context): Boolean = Manifest.permission.CAMERA.all {
        ActivityCompat.checkSelfPermission(context, it.toString()) == PackageManager.PERMISSION_GRANTED
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_magnifyingGlassFragment)
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.no_permissions),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}