package com.hyperether.getgoing.ui.fragment.profile

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hyperether.getgoing.App
import com.hyperether.getgoing.R
import com.hyperether.getgoing.databinding.FragmentUserProfileBinding
import com.hyperether.getgoing.model.CBDataFrame
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.fragment_profile.ib_fp_age
import kotlinx.android.synthetic.main.fragment_profile.ib_fp_height
import kotlinx.android.synthetic.main.fragment_profile.ib_fp_weight

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    val viewModel: UserProfileViewModel by viewModels()
    private var model: CBDataFrame? = null
    private var settings: SharedPreferences? = null
    private lateinit var genderImg: ImageView
    private var rootViewGroup: ViewGroup? = null
    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var genderBtn: ImageButton
    lateinit var routeViewModel: RouteViewModel
    lateinit var totalMileage: TextView
    lateinit var totalCalories: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_user_profile,
            null,
            false
        )
        settings = activity?.getSharedPreferences(Constants.PREF_FILE, 0)
        model = CBDataFrame.getInstance()
        rootViewGroup = container
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        genderImg = binding.ibFpGender
        routeViewModel = ViewModelProvider(this)[RouteViewModel::class.java]
        totalMileage = binding.tvFpMileage
        totalCalories = binding.tvFpCalories
        when (settings!!.getInt("gender", 0)) {
            0 -> genderImg.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_gendersign_male
                )
            )

            1 -> genderImg.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_light_gender_female_icon
                )
            )

            2 -> genderImg.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_light_gender_icon_trans
                )
            )
        }
        genderBtn = binding.ibFpGender
        tvGender = binding.tvFpGender
        tvAge = binding.tvFpAge
        tvHeight = binding.tvFpHeight
        tvWeight = binding.tvFpWeight
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        initLabels()
        initDialogs()
        routeViewModel.getAllRoutes().observe(
            requireActivity()
        ) { route -> initTotals(route) }
        binding.ibFpBackbutton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initTotals(route: List<Route>?) {
        val totalRoute = DoubleArray(1)
        val totalKcal = DoubleArray(1)
        App.getHandler().post(Runnable {
            totalRoute[0] = 0.0
            totalKcal[0] = 0.0
            if (route != null) {
                for (item in route) {
                    totalRoute[0] += item.length / 1000
                    totalKcal[0] += item.energy
                }
                activity?.runOnUiThread(Runnable {
                    totalMileage.text = String.format("%.02f km", totalRoute[0])
                    val s: Double = Math.round(totalKcal[0] * 100.0) / 100.0.toDouble()
                    totalCalories.text = "$s Kcal"
                })
            }
        })
    }

    private fun initDialogs() {
        genderBtn.setOnClickListener { view ->
            val id = "gender"
            createAlertDialog(id, view).also { it?.show() }
        }
        ib_fp_age.setOnClickListener { view ->
            val id = "age"
            createAlertDialog(id, view).also { it?.show() }
        }
        ib_fp_height.setOnClickListener { view ->
            val id = "height"
            createAlertDialog(id, view).also { it?.show() }
        }
        ib_fp_weight.setOnClickListener { view ->
            val id = "weight"
            createAlertDialog(id, view).also { it?.show() }
        }
    }

    private fun createAlertDialog(pId: String, view: View): AlertDialog.Builder? {
        val genderBuilder: AlertDialog.Builder
        val ageBuilder: AlertDialog.Builder
        val heightBuilder: AlertDialog.Builder
        val weightBuilder: AlertDialog.Builder
        when (pId) {
            "gender" -> {
                genderBuilder = AlertDialog.Builder(view.context)
                var newText = ""
                genderBuilder.setSingleChoiceItems(
                    R.array.genders,
                    settings!!.getInt("gender", 0)
                ) { _, which ->
                    {
                        val editor = settings!!.edit()
                        when (which) {
                            0 -> {
                                newText = "Male"
                                editor.putInt("gender", 0)
                                model!!.gender = Constants.gender.Male
                            }

                            1 -> {
                                newText = "Female"
                                editor.putInt("gender", 1)
                                model!!.gender = Constants.gender.Female
                            }

                            else -> {
                                newText = "Other"
                                editor.putInt("gender", 2)
                                model!!.gender = Constants.gender.Other
                            }
                        }
                        editor.apply()
                    }.invoke()
                }
                    .setPositiveButton("Confirm") { _, _ ->
                        {
                            tvGender.text = newText

                            when (newText) {
                                "Male" -> genderImg.setImageDrawable(
                                    ContextCompat.getDrawable
                                        (context!!, R.drawable.ic_gendersign_male)
                                )

                                "Female" -> genderImg.setImageDrawable(
                                    ContextCompat.getDrawable
                                        (context!!, R.drawable.ic_light_gender_female_icon)
                                )

                                "Other" -> genderImg.setImageDrawable(
                                    ContextCompat.getDrawable
                                        (context!!, R.drawable.ic_light_gender_icon_trans)
                                )
                            }
                        }()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
                    .setTitle("Please select your gender:")
                return genderBuilder
            }
            "age"->{
                val ageList = arrayListOf<String>()
                for (i in 1..120)
                    ageList.add(i.toString())
                ageBuilder = AlertDialog.Builder(view.context)
                val inflater = LayoutInflater.from(requireActivity())
                val toInflate = inflater.inflate(R.layout.alertdialog_age,null)
                ageBuilder.setView(toInflate)
                val ageSpinner: Spinner = toInflate.findViewById(R.id.dialog_spinner_age)
                val arAdapter = ArrayAdapter<String>(
                    view.context,
                    android.R.layout.simple_list_item_1, ageList
                )
                ageSpinner.adapter = arAdapter
                ageSpinner.setSelection(settings!!.getInt("age", 0) - 1)
                ageBuilder.setPositiveButton("Confirm"){_,_->
                    {
                        tvAge.text = ageSpinner.selectedItem.toString() +
                                resources.getString(R.string.textview_age_end)
                        val editor = settings!!.edit()
                        editor.putInt(
                            "age",
                            Integer.valueOf((ageSpinner.selectedItem as String))
                        )
                        editor.apply()
                        model!!.age = Integer.valueOf((ageSpinner.selectedItem as String))
                    }()
                }
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .setTitle("How old are you?")
                return ageBuilder
            }
            "height"->{
                val heightList= arrayListOf<String>()
                for (i in 100..250){
                  heightList.add(i.toString())
                }
                heightBuilder = AlertDialog.Builder(view.context)
                val inflater = LayoutInflater.from(requireActivity())
                val toInflate = inflater.inflate(R.layout.alertdialog_height,null)
                heightBuilder.setView(toInflate)
                val heightSpinner: Spinner = toInflate.findViewById(R.id.dialog_spinner_height)
                val arAdapter = ArrayAdapter<String>(
                    view.context,
                    android.R.layout.simple_list_item_1,heightList
                )
                heightSpinner.adapter = arAdapter
                heightSpinner.setSelection(settings!!.getInt("height", 0) - 110)
                heightBuilder.setPositiveButton("Confirm") { _, _ ->
                    {
                        tvHeight.text = heightSpinner.selectedItem.toString() + " cm"
                        val editor = settings!!.edit()
                        editor.putInt(
                            "height",
                            Integer.valueOf(heightSpinner.selectedItem as String)
                        )
                        editor.apply()
                        model?.height = Integer.valueOf(heightSpinner.selectedItem as String)
                    }()
                }
                    .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
                    .setTitle("Enter your height:")
                return heightBuilder
            }
            "weight"->{
                val weightList= arrayListOf<String>()
                for (i in 40..150){
                    weightList.add(i.toString())
                }
                weightBuilder = AlertDialog.Builder(view.context)
                val inflater = LayoutInflater.from(requireActivity())
                val toInflate = inflater.inflate(R.layout.alertdialog_weight,null)
                weightBuilder.setView(toInflate)
                val weightSpinner: Spinner = toInflate.findViewById(R.id.dialog_spinner_weight)
                val arAdapter = ArrayAdapter<String>(
                    view.context,
                    android.R.layout.simple_list_item_1, weightList
                )
                weightSpinner.adapter = arAdapter
                weightSpinner.setSelection(settings!!.getInt("weight", 0) - 40)
                weightBuilder.setPositiveButton("Confirm") { _, _ ->
                    {
                        tvWeight.text = weightSpinner.selectedItem.toString() + " kg"
                        val editor = settings!!.edit()
                        editor.putInt(
                            "weight",
                            Integer.valueOf(weightSpinner.selectedItem as String)
                        )
                        editor.apply()
                        model?.weight = Integer.valueOf(weightSpinner.selectedItem as String)
                    }()
                }
                    .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
                    .setTitle("Enter your weight:")
                return weightBuilder
            }

        }
        return null
    }

    private fun initLabels() {
        tvAge.text = settings!!.getInt("age", 0).toString() + " years"
        tvHeight.text = settings!!.getInt("height", 0).toString() + "cm"
        tvWeight.text = settings!!.getInt("weight", 0).toString() + "kg"
        when (settings!!.getInt("gender", 0)) {
            0 -> tvGender.setText(R.string.gender_male)
            1 -> tvGender.setText(R.string.gender_female)
            2 -> tvGender.setText(R.string.gender_other)
        }
    }
}